package com.prokopchyk.dao;


import com.prokopchyk.building.Ground;
import com.prokopchyk.building.House;
import com.prokopchyk.service.GroundService;
import com.prokopchyk.service.HouseService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class HouseDaoImpTest  {
    private HouseDaoImp houseDaoImp;

    @Test
    public void findAll () throws SQLException, DAOException {
        houseDaoImp = new HouseDaoImp(DBConnector.getConnection());
         Optional<House> house= houseDaoImp.findById(1);
         System.out.println(house.get().getHouseName());
        System.out.println(house.get().getGround(0).getFlat(0).getId());

        }
    @Test
    public void delete() throws SQLException,DAOException{
        houseDaoImp = new HouseDaoImp(DBConnector.getConnection());
        houseDaoImp.delete(7);
        List<House> houses =  houseDaoImp.findAll();
        for(House house : houses){
            System.out.println(house.getHouseName());
        }
    }
    @Test
    public void create() throws SQLException,DAOException{
        House house = HouseService.getHouseService().createHouse(4,4,"Brazil",10);
        HouseService.getHouseService().initPersonsRandom(house);
        houseDaoImp = new HouseDaoImp(DBConnector.getConnection());
        houseDaoImp.create(house);
    }
}