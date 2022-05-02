package com.prokopchyk.builder;

import com.prokopchyk.building.Flat;

public class FlatBilder {
    private Flat flat;

     public FlatBilder()
     {
        flat = new Flat();
     }

    public FlatBilder setNumberOfHuman(int nHuman) {
        this.flat.setNumberOfHuman(nHuman);
        return this;
    }
    public FlatBilder setGroundId(int groundId){
         this.flat.setGroundId(groundId);
         return this;
    }
    public FlatBilder setId(int id){
         this.flat.setId(id);
         return this;
    }

    public FlatBilder setArea(double area) {
        this.flat.setArea(area);
        return this;
    }


    public FlatBilder setNumOfFlat() {
        this.flat.setNumOfFlat();
        return this;
    }

    public Flat bilder(){
        return flat;
    }
}
