package com.prokopchyk.dao;

import com.prokopchyk.builder.FlatBilder;
import com.prokopchyk.builder.GroundBuilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.Ground;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FlatDaoImp  extends AbstractDAO<Flat>{

    private static final String SELECT_ALL_FLATS = """
            SELECT id, flatNumber,floorId,areaCount,residentsCount 
            FROM flats
            """;

    private static final String SELECT_FLAT_BY_ID = """
                SELECT id, flatNumber,floorId,areaCount,residentsCount
                FROM flats
                WHERE id = ?
            """;
    private static final String DELETE_FLAT_BY_ID = """
                DELETE FROM flats
                WHERE id = ?
            """;
    private static final String CREATE_FLAT = """
            INSERT flats (id,flatNumber,floorId,areaCount,residentsCount)
                VALUES (?,?,?,?,?)
            """;

    private static final String SELECT_FLATS_BY_GROUNDID = """
            SELECT id, flatNumber,floorId,areaCount,residentsCount 
            FROM flats 
            WHERE floorId = ?
            """;

    public FlatDaoImp(Connection connection) {
        super(connection);
    }

    @Override
    public List<Flat> findAll() throws DAOException, SQLException {
        try (var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_FLATS);
            return getListFlatsByResultSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Flat> findById(int id) throws DAOException {
        try (var preparedStatement = connection.prepareStatement(SELECT_FLAT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListFlatsByResultSet(result);
            return houseList.isEmpty() ? Optional.empty() : Optional.of(houseList.get(0));

        } catch (SQLException e) {
            throw new DAOException("Error in getting flat by id", e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        Optional<Flat> optionalFlats = findById(id);
        if (optionalFlats.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(DELETE_FLAT_BY_ID)) {
                preparedStatement.setInt(1, id);
                return preparedStatement.executeUpdate() == 1;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        else {
            throw new DAOException(new NoSuchElementException("No flat with the given id"));
        }
    }


    @Override
    public boolean create(@NotNull Flat flat) throws DAOException {
        try( var preparedStatement = connection.prepareStatement(CREATE_FLAT)){
            preparedStatement.setInt(1,flat.getId());
            preparedStatement.setInt(2,flat.getNumber());
            preparedStatement.setInt(3,flat.getGroundId());
            preparedStatement.setDouble(4,flat.getArea());
            preparedStatement.setInt(5,flat.getNumberOfHuman());
            preparedStatement.executeUpdate();
            return true;
        }
        catch (SQLException e){
            throw new DAOException(e);
        }
    }

    @Override
    public Flat update(Flat flat) throws DAOException {
        Optional<Flat> optionalFlat = findById(flat.getId());
        if (optionalFlat.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(SELECT_FLAT_BY_ID,
                    ResultSet.TYPE_SCROLL_SENSITIVE , ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setInt(1, flat.getId());
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    resultSet.updateInt("flatNumber",flat.getNumber());
                    resultSet.updateInt("floorId",flat.getGroundId());
                    resultSet.updateDouble("areaCount",flat.getArea());
                    resultSet.updateInt("residentsCount",flat.getNumberOfHuman());
                    resultSet.updateRow();
                }
                resultSet.beforeFirst();
                var list = getListFlatsByResultSet(resultSet);
                return list.get(0);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        else {
            throw new DAOException(new NoSuchElementException("No maker with the given id"));
        }
    }
    public List<Flat> findByGroundID(int groundId) throws DAOException{
        try (var preparedStatement = connection.prepareStatement(SELECT_FLATS_BY_GROUNDID)) {
            preparedStatement.setInt(1, groundId);
            ResultSet result = preparedStatement.executeQuery();
            var flatsList = getListFlatsByResultSet(result);
            return flatsList;

        } catch (SQLException e) {
            throw new DAOException("Error in getting ground by id", e);
        }
    }

    private List<Flat> getListFlatsByResultSet(ResultSet resultSet) throws SQLException {
        List<Flat> flats = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int residentsCount = resultSet.getInt("residentsCount");
            int areaCount = resultSet.getInt("areaCount");
            int groundId = resultSet.getInt("floorId");

            flats.add(new FlatBilder()
                    .setId(id)
                    .setNumberOfHuman(residentsCount)
                    .setArea(areaCount)
                    .setGroundId(groundId)
                    .bilder());
        }
        return  flats;
    }
}
