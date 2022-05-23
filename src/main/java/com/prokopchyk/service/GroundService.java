package com.prokopchyk.service;

import com.prokopchyk.builder.GroundBuilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.Ground;
import com.prokopchyk.building.House;

public class GroundService {
    private static GroundService groundService;

    private GroundService() {
    }

    public static synchronized GroundService getGroundService() {
        if (groundService == null) {
            groundService= new GroundService();
        }
        return groundService;
    }

    public Ground createGround(int numberOfFlatsInGround,int houseId,int groundNumber) {
       if(numberOfFlatsInGround < 0){
           throw new IllegalArgumentException("Incorrect grounds settings");
       }
       else{
        Ground ground = new GroundBuilder()
                .setNumOfFlats(numberOfFlatsInGround)
                .setId(houseId * 10 + groundNumber)
                .setGroundNumber(groundNumber)
                .setHouseId(houseId)
                .Builder();
        for (int i = 0; i < numberOfFlatsInGround; ++i) {
            int id = ground.getId() * 10 + i;
            int groundId = ground.getId();
            Flat addFlat =  FlatService.getFlatService().createFlat(id,groundId);
            ground.addFlat(addFlat);
        }

        return ground;
       }
    }

    public Ground cloneGround(Ground ground,int groundNumber) {
        Ground groundNew = new GroundBuilder()
                .setNumOfFlats(ground.getFlatsOnGround())
                .setId(ground.getHouseId() *10 + groundNumber)
                .setGroundNumber(groundNumber)
                .setHouseId(ground.getHouseId())
                .Builder();
        int i =1;
        for (Flat flat : ground.getFlat()) {
            int id = groundNew.getId() *10 + i;
            int groundId = groundNew.getId();
            Flat addFlat =  FlatService.getFlatService().cloneFlat(flat,id,groundId);
            groundNew.addFlat(addFlat);
            i++;
        }

        return groundNew;
    }
}
