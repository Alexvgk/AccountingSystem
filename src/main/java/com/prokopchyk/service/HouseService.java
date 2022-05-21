package com.prokopchyk.service;

import com.prokopchyk.builder.HouseBuilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.Ground;
import com.prokopchyk.building.House;
import com.prokopchyk.building.houseComparator.AreaCompare;
import com.prokopchyk.building.houseComparator.PersonsCompare;
import com.prokopchyk.dao.DAOException;
import com.prokopchyk.dao.DBConnector;
import com.prokopchyk.dao.HouseDaoImp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class HouseService  {
    private static  HouseService houseService;
    private static HouseDaoImp houseDaoImp;
    private HouseService() {
        try {
            houseDaoImp = new HouseDaoImp(DBConnector.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };
    public synchronized static HouseService getHouseService() throws SQLException {
        if(houseService == null)
            houseService = new HouseService();
        return houseService;
    }


 public List<House> getAllFromDatabase(){
     try {
         return houseDaoImp.findAll();
     } catch (DAOException | SQLException e) {
         e.printStackTrace();
         return null;
     }
 }

    public void saveToDatabase(House house){
        try {
            houseDaoImp.create(house);
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromDatabase(int id){
        try {
            houseDaoImp.delete(id);
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }
    public void updateInDatabase(House houseNew){
        try {
            houseDaoImp.update(houseNew);
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    public Optional<House> getByIdFromDatabase(int id){
        try {
          return houseDaoImp.findById(id);
        } catch (DAOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<House> getByNameFromDatabase(String name){
        try{
            return houseDaoImp.findAllByName(name);
        } catch (DAOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Optional<House> getByFlatId(int flatId){
        try{
            return houseDaoImp.findHouseByFlatId(flatId);
        } catch (DAOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<House> selectByAreaFromDatabase(double area){
        try {
            return houseDaoImp.selectHousesByArea(area);
        } catch (DAOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public House createHouse(int numOfGrounds, int numOfFlatsInGround,String name,int id) {
        House house = new HouseBuilder().setHouseName(name).setNumOfGrounds(numOfGrounds).setId(id).builder();;
        house.addGround(GroundService.getGroundService().createGround(numOfFlatsInGround,house.getId(),0));
        int firstFloor = 0;
        for (int i = 1; i < numOfGrounds; ++i) {
            Ground addGround = GroundService.getGroundService().cloneGround(house.getGround(firstFloor),i);
            house.addGround(addGround);
        }

        return house;
    }

    public  double getHouseArea(House house) {
        int sqrt = 0;
        for(int i = 0; i < house.getNumberOfGrounds(); i++){
            for(int j = 0; j < house.getGrounds().get(0).getFlatsOnGround(); j++) {
            sqrt += house.getGround(i).getFlat(j).getArea();
            }
        }
        return sqrt;
    }

    public int getNumberOfHuman(House house){  // возвращает общее число жильцов
        int kol = 0;
        for(int i = 0; i < house.getNumberOfGrounds(); i++){
            for(int j = 0; j < house.getGrounds().get(0).getFlatsOnGround(); j++) {
                kol += house.getGround(i).getFlat(j).getNumberOfHuman();
            }
        }
        return kol;
    }
    public  List<Integer> compareHouses(House house1,House house2){
        List<Integer> answer = new ArrayList<Integer>();
        answer.add(new AreaCompare().compare(house1,house2));
        answer.add(new PersonsCompare().compare(house2,house2));
        return  answer;
    }
    public Flat getFlatByNumber(House house, int i){
        int gr = i / (house.getGrounds().get(0).getFlatsOnGround());
        int fl = i % (house.getGrounds().size());
        return house.getGrounds().get(gr).getFlat(fl);
    }

    public int getNumberOfFlats(House house){
        return house.getNumberOfGrounds() * house.getGrounds().get(0).getFlatsOnGround();
    }

    public void initPersons(House house){
        for(int i = 0; i < house.getNumberOfGrounds(); i++){
            for(int j = 0; j < house.getGrounds().get(0).getFlatsOnGround(); j++){
                System.out.println("Enter number of persons in "+house.getGrounds().get(i).getFlat(j).getNumber()+" flat");
                Scanner in = new Scanner(System.in);
                int persons =  in.nextInt();
                house.getGrounds().get(i).getFlat(j).setNumberOfHuman(persons);
            }
        }
    }

    public void initPersonsRandom(House house){
        for(int i = 0; i < house.getNumberOfGrounds(); i++){
            for(int j = 0; j < house.getGrounds().get(0).getFlatsOnGround(); j++){
                int randPersons = (int) (Math.random()*5);
                house.getGrounds().get(i).getFlat(j).setNumberOfHuman(randPersons);
            }
        }
    }
    public void readHouseList(String fileName){
        HouseListService.getHouseListService().readHouseList(fileName);
    }
    public void writeHouseList(String fileName){
        HouseListService.getHouseListService().writeHouseList(fileName);
    }

}
