package com.prokopchyk.service;

import com.prokopchyk.builder.HouseBuilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.House;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HouseServiceTest extends SQLException {

    private House house;


    @BeforeEach
     void setUp() throws Exception {
         house = new HouseBuilder().setHouseName("Alex").setNumOfGrounds(2).builder();

        house.addGround(GroundService.getGroundService().createGround(2,10,1));
        int firstFloor = 0;
        for (int i = 1; i < 2; ++i) {
            house.addGround(GroundService.getGroundService().cloneGround(house.getGround(firstFloor),1));
        }
    }
    @Test
    void getAllTest() throws SQLException {
        HouseService.getHouseService().saveToDatabase(house);
        List<House> expList = HouseService.getHouseService().getAllFromDatabase();
        Assertions.assertFalse(expList.isEmpty());
    }

    @Test
    void saveTest() throws SQLException {
        HouseService.getHouseService().saveToDatabase(house);
    }

    @Test
    void createTest() throws SQLException {
        HouseService.getHouseService().createHouse(2,3,"Alex",15);
    }

    @Test
    void getByIdFromDatabaseTest() throws SQLException{
        Optional<House> expHouse =  HouseService.getHouseService().getByIdFromDatabase(1);
        Assertions.assertEquals(1,expHouse.get().getId());
    }

    @Test
    void deleteTest() throws SQLException {
        HouseService.getHouseService().saveToDatabase(house);
        HouseService.getHouseService().deleteFromDatabase(10);
        assertEquals(0,HouseService.getHouseService().getAllFromDatabase().size());
    }

    @Test
    void updateTest() throws SQLException {
        House expHouse = HouseService.getHouseService().createHouse(2,2,"Max",1);
        HouseService.getHouseService().saveToDatabase(house);
        String teor = "Max";
        HouseService.getHouseService().updateInDatabase(expHouse);
    }

    @Test
     void testGetHouseArea() throws SQLException {
        double teorHouseArea = 40;
        double expHouseArea = HouseService.getHouseService().getHouseArea(house);
        assertEquals(teorHouseArea,expHouseArea,0);
    }

    @Test
     void testGetNumberOfHuman() throws SQLException {
        int teorNumber = 12;
        int expNumber = HouseService.getHouseService().getNumberOfHuman(house);
        assertEquals(teorNumber, expNumber);
    }
    @Test
     void testCompareHouses() throws SQLException {
        List<Integer> teor = new ArrayList<Integer>();
        teor.add(0);
        teor.add(0);
        List<Integer> exp = new ArrayList<Integer>(HouseService.getHouseService().compareHouses(house,house));
        assertEquals(teor,exp);
    }
    @Test
    void createHouseTest() throws SQLException {
        House expHouse = HouseService.getHouseService().createHouse(2,2,"Alex",2);
        Assertions.assertEquals(expHouse,house);
    }

    @Test
    void getByIndTest() throws SQLException {
        HouseService.getHouseService().saveToDatabase(house);

    }
    @Test
    void getNumberOfFlatsTest() throws SQLException {
        int teorNum = 4;
        int expNum =  HouseService.getHouseService().getNumberOfFlats(house);
        assertEquals(expNum,teorNum);
    }

    @Test
    void getFlatByNumberTest() throws SQLException {
        Flat expFlat =  HouseService.getHouseService().getFlatByNumber(house,0);
        int teorFlatNum = 0;
        int expFlatNum = expFlat.getNumber();
        assertEquals(teorFlatNum,expFlatNum);
    }
    @Test
    void readHouseListTest() throws SQLException {
        HouseService.getHouseService().saveToDatabase(house);
        HouseService.getHouseService().writeHouseList("src\\test\\houseTest.txt");
        HouseService.getHouseService().deleteFromDatabase(1);
        HouseService.getHouseService().readHouseList("src\\test\\houseTest.txt");
        List<House> expList = HouseService.getHouseService().getAllFromDatabase();
        Assertions.assertFalse(expList.isEmpty());
    }
}

