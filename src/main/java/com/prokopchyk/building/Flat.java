package com.prokopchyk.building;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Flat implements Externalizable{
    private int numberHuman = 0;
    private double area = 0;
    private static int flatNumberChecker = 0;
    private int numOfFlat = 0;
    private int GroundId = 0;
    private int id = 0;



    public void setNumberOfHuman(int NHuman) {
        this.numberHuman = NHuman;
    }
    public int getGroundId() {
        return GroundId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroundId(int groundId) {
        GroundId = groundId;
    }

    public void setArea(double area) {this.area = area;}

    public void setNumOfFlat(){
        this.numOfFlat = flatNumberChecker;
        flatNumberChecker++;
    }
    public int getNumberOfHuman() {return numberHuman;}
    public double getArea() {
        return area;
    }
    public void setChecker(int k){
        flatNumberChecker = k;
    }
    public int getNumber() {
        return numOfFlat;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(numOfFlat);
        out.writeObject(numberHuman);
        out.writeObject(area);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        numOfFlat = (int) in.readObject();
        numberHuman = (int) in.readObject();
        area = (double) in.readObject();

    }
}

