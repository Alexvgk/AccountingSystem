package com.prokopchyk.building;

import java.io.Externalizable;
import java.util.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Ground  implements Externalizable {
    private List<Flat> flat;
    private int groundNumber = 0;
    private int houseId = 0;
    private int numberOfFlatsInGround = 0;
    private int id;

    public Ground() {
        flat = new ArrayList<>(0);
        setNumberOfFlatsInGround(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setHouseId(int houseId){
        this.houseId = houseId;
    }
    public void setGroundNumber(int groundNumber){
        this.groundNumber = groundNumber;
    }


    public void setIdByHouse(int houseId,int k){
        this.id = houseId * 10 + k;
    }
    public int getHouseId() {
        return houseId;
    }
    public int getGroundNumber() {
        return groundNumber;
    }

    public void setFlat(List<Flat>flat){

        this.flat = new ArrayList<Flat>(flat);
    }

    public void addFlat(Flat flat){

        this.flat.add(flat);
    }

    public List<Flat> getFlat(){
        return flat;
    }

    public void setNumberOfFlatsInGround(int numberOfFlatsInGround) {
        this.numberOfFlatsInGround = numberOfFlatsInGround;
    }

    public Flat getFlat(int i){
        return flat.get(i);
    }

    public void setFlatChecker(int k) {//для зануление сатической переменной в flat
        this.flat.iterator().next().setChecker(0);
    }

    public int getFlatsOnGround() {
        return numberOfFlatsInGround;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(numberOfFlatsInGround);
        out.writeObject(flat);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        numberOfFlatsInGround = (int) in.readObject();
        flat = (List<Flat>) in.readObject();

    }
}

