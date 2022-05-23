package com.prokopchyk.service;

import com.prokopchyk.builder.FlatBilder;
import com.prokopchyk.building.Flat;
import com.prokopchyk.building.flatComparator.FlatAreaCompare;
import com.prokopchyk.building.flatComparator.FlatPersonsCompare;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlatService {
    private static FlatService flatService;
    private FlatService(){};
    public synchronized static FlatService getFlatService(){
        if(flatService == null)
            flatService = new FlatService();
        return flatService;
    }
    public List<Integer> compareFlats(Flat flat1, Flat flat2){
        List<Integer> answer = new ArrayList<Integer>();
        answer.add(new FlatAreaCompare().compare(flat1,flat2));
        answer.add(new FlatPersonsCompare().compare(flat1,flat2));
        return answer;
    }

    public Flat createFlat(int id,int groundId) {
        int numOfPeople = (int) (Math.random()*5 +1);
        double area = (int) (Math.random()*40 + 10);

        return new FlatBilder()
                .setArea(area)
                .setGroundId(groundId)
                .setId(id)
                .setNumberOfHuman(numOfPeople)
                .setNumOfFlat()
                .bilder();
    }

    public Flat cloneFlat(@NotNull Flat flat,int id,int groundId) {
        return new FlatBilder()
                .setNumOfFlat()
                .setId(id)
                .setGroundId(groundId)
                .setNumberOfHuman(flat.getNumberOfHuman())
                .setArea(flat.getArea())
                .bilder();
    }
}
