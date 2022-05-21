package com.prokopchyk.dao;

import com.prokopchyk.builder.HouseBuilder;
import com.prokopchyk.building.Ground;
import com.prokopchyk.building.House;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HouseDaoImp extends AbstractDAO<House>{

    private static final String SELECT_ALL_HOUSES = """
            SELECT id, houseName
            FROM houses
            """;
    private static final String CREATE_HOUSE_BY_NAME = """
                INSERT houses (id ,houseName)
                VALUES (?,?)
            """;
    private static final String SELECT_HOUSE_BY_ID = """
                SELECT id, houseName
                FROM houses
                WHERE id = ?
            """;
    private static final String SELECT_HOUSE_BY_NAME = """
                SELECT id, houseName
                FROM houses
                WHERE houseName = ?
            """;
    private static final String DELETE_HOUSE_BY_ID = """
                DELETE FROM houses
                WHERE id = ?
            """;
    private static final String SELECT_HOUSE_BY_AREA = """
            Select Sum(flats.areaCount) as sum, floors.houseId as id, houses.houseName as houseName
             from floors inner Join flats
             on floors.id = flats.floorId
             inner join houses
             on floors.houseId = houses.id
             group by houseId
             having Sum(flats.areaCount) > ?;
            """;
    private static final String FIND_HOUSE_BY_FLATID = """
            Select id,houseName from houses
            where id =
             (select houseid from floors
             where id =
            (select floorId from flats
            where id = ?));
            """;

    public HouseDaoImp(Connection connection) {
        super(connection);
    }

    @Override
    public List<House> findAll() throws DAOException, SQLException {
        try (var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_HOUSES);
            return getListHousesByResultSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<House> findById(int id) throws DAOException {
        try (var preparedStatement = connection.prepareStatement(SELECT_HOUSE_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListHousesByResultSet(result);
            return houseList.isEmpty() ? Optional.empty() : Optional.of(houseList.get(0));

        } catch (SQLException e) {
            throw new DAOException("Error in getting house by id", e);
        }
    }
    public List<House> findAllByName(String name) throws DAOException {
        try (var preparedStatement = connection.prepareStatement(SELECT_HOUSE_BY_NAME)) {
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListHousesByResultSet(result);
            return houseList;

        } catch (SQLException e) {
            throw new DAOException("Error in getting house by id", e);
        }
    }

    public  Optional<House> findHouseByFlatId(int flatId) throws  DAOException{
        try (var preparedStatement = connection.prepareStatement(FIND_HOUSE_BY_FLATID)) {
            preparedStatement.setInt(1, flatId);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListHousesByResultSet(result);
            return houseList.isEmpty() ? Optional.empty() : Optional.of(houseList.get(0));

        } catch (SQLException e) {
            throw new DAOException("Error in getting house by flat id", e);
        }
    }

    public List<House> selectHousesByArea(double area) throws DAOException{
        try (var preparedStatement = connection.prepareStatement(SELECT_HOUSE_BY_AREA)) {
            preparedStatement.setDouble(1, area);
            ResultSet result = preparedStatement.executeQuery();
            var houseList = getListHousesByResultSet(result);
            return houseList;

        } catch (SQLException e) {
            throw new DAOException("Error in getting house by flat id", e);
        }

    }

    @Override
    public boolean delete(int id) throws DAOException {
        Optional<House> optionalMaker = findById(id);
        if (optionalMaker.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(DELETE_HOUSE_BY_ID)) {
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
    public boolean create(@NotNull House house) throws DAOException {
        try (var preparedStatement = connection.prepareStatement(CREATE_HOUSE_BY_NAME)) {
            preparedStatement.setInt(1, house.getId());
            preparedStatement.setString(2,house.getHouseName());
            preparedStatement.executeUpdate();
            for(int i = 0;i< house.getNumberOfGrounds();i++){
                GroundDaoImp gdi = new GroundDaoImp(DBConnector.getConnection());
                gdi.create(house.getGround(i));
                for(int k = 0;k< house.getGround(i).getFlatsOnGround();k++) {
                    FlatDaoImp fdi = new FlatDaoImp(DBConnector.getConnection());
                    fdi.create(house.getGround(i).getFlat(k));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }




    @Override
    public House update(House house) throws DAOException {
        Optional<House> optionalHouses = findById(house.getId());
        if (optionalHouses.isPresent()) {
            try (var preparedStatement = connection.prepareStatement(SELECT_HOUSE_BY_ID,
                    ResultSet.TYPE_SCROLL_SENSITIVE , ResultSet.CONCUR_UPDATABLE)) {
                preparedStatement.setInt(1, house.getId());
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    resultSet.updateString("name", house.getHouseName());
                    resultSet.updateRow();
                }
                resultSet.beforeFirst();
                var list = getListHousesByResultSet(resultSet);
                return list.get(0);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
        else {
            throw new DAOException(new NoSuchElementException("No house with the given id"));
        }
    }


    private List<House> getListHousesByResultSet(ResultSet resultSet) throws SQLException, DAOException {
        List<House> houses = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("houseName");
            int id = resultSet.getInt("id");
            houses.add(new HouseBuilder().setHouseName(name).setId(id).builder());
        }
        for(int i = 0;i<houses.size();i++){
            GroundDaoImp gdl = new GroundDaoImp(DBConnector.getConnection());
            List<Ground> grounds = gdl.findByHouseID(houses.get(i).getId());
            houses.get(i).setGrounds(grounds);
        }

        return  houses;
    }
}
