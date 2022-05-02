package com.prokopchyk.dao;

import com.prokopchyk.builder.GroundBuilder;
import com.prokopchyk.builder.HouseBuilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.Ground;
import com.prokopchyk.building.House;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GroundDaoImp extends AbstractDAO<Ground> {

    private static final String SELECT_ALL_GROUNDS = """
            SELECT id, houseId,floorNumber
            FROM floors
            """;

    private static final String SELECT_GROUND_BY_ID = """
                SELECT id, houseId,floorsNumber
                FROM floors
                WHERE id = ?
            """;
    private static final String DELETE_GROUND_BY_ID = """
                DELETE FROM floors
                WHERE id = ?
            """;
    private static final String CREATE_GROUND = """
            INSERT floors (id, houseId, floorNumber)
                VALUES (?,?,?)
            """;
    private static final String SELECT_GROUND_BY_HOUSEID = """
            SELECT id,houseId,floorNumber
            FROM floors 
            WHERE houseId = ?
            """;

    public GroundDaoImp(Connection connection) {
        super(connection);
    }

    @Override
    public List<Ground> findAll() throws DAOException, SQLException {
        try (var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_GROUNDS);
            return getListGroundsByResultSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Ground> findById(int id) throws DAOException {
        try (var preparedStatement = connection.prepareStatement(SELECT_GROUND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListGroundsByResultSet(result);
            return houseList.isEmpty() ? Optional.empty() : Optional.of(houseList.get(0));

        } catch (SQLException e) {
            throw new DAOException("Error in getting ground by id", e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        Optional<Ground> optionalGround = findById(id);
        if (optionalGround.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(DELETE_GROUND_BY_ID)) {
                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() == 1;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        else {
            throw new DAOException(new NoSuchElementException("No house with the given id"));
        }
    }

    @Override
    public boolean create(Ground ground) throws DAOException {
        try( var preparedStatement = connection.prepareStatement(CREATE_GROUND)){
            preparedStatement.setInt(1,ground.getId());
            preparedStatement.setInt(2,ground.getHouseId());
            preparedStatement.setInt(3,ground.getGroundNumber());
            preparedStatement.executeUpdate();
            return true;
        }
        catch (SQLException e){
            throw new DAOException(e);
        }
    }

    @Override
    public Ground update(Ground ground) throws DAOException {
        Optional<Ground> optionalHouses = findById(ground.getId());
        if (optionalHouses.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(SELECT_GROUND_BY_ID,
                    ResultSet.TYPE_SCROLL_SENSITIVE , ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setInt(1, ground.getId());
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    resultSet.updateInt("floorNumber",ground.getGroundNumber());
                    resultSet.updateInt("houseId",ground.getHouseId());
                    resultSet.updateRow();
                }
                resultSet.beforeFirst();
                var list = getListGroundsByResultSet(resultSet);
                return list.get(0);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        else {
            throw new DAOException(new NoSuchElementException("No maker with the given id"));
        }
    }

    public List<Ground> findByHouseID(int houseId) throws DAOException{
        try (var preparedStatement = connection.prepareStatement(SELECT_GROUND_BY_HOUSEID)) {
            preparedStatement.setInt(1, houseId);
            ResultSet result = preparedStatement.executeQuery();
            var groundsList = getListGroundsByResultSet(result);
            return groundsList;

        } catch (SQLException e) {
            throw new DAOException("Error in getting ground by id", e);
        }
    }
    private List<Ground> getListGroundsByResultSet(ResultSet resultSet) throws SQLException, DAOException {
        List<Ground> ground = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int houseId = resultSet.getInt("houseId");
            int groundNumber = resultSet.getInt("floorNumber");
            ground.add(new GroundBuilder().setId(id).setGroundNumber(groundNumber).setHouseId(houseId).Builder());
        }
        for(int i = 0; i <ground.size();i++){
            FlatDaoImp fdl = new FlatDaoImp(DBConnector.getConnection());
            List<Flat> flats = fdl.findByGroundID(ground.get(i).getId());
            ground.get(i).setFlat(flats);
        }
        return  ground;
    }
}
