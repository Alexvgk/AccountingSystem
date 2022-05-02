package com.prokopchyk.building.houseComparator;
import com.prokopchyk.building.House;
import com.prokopchyk.service.HouseService;

import java.sql.SQLException;
import java.util.Comparator;


public class PersonsCompare implements Comparator<House> {
    @Override
    public int compare(House o1, House o2) {
        int answ = 2;
        try {
            if(HouseService.getHouseService().getNumberOfHuman(o1) > HouseService.getHouseService().getNumberOfHuman(o2))
                answ = 1;
            else if(HouseService.getHouseService().getNumberOfHuman(o1) == HouseService.getHouseService().getNumberOfHuman(o2))
                answ = 0;
            else
                answ = -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answ;
    }
}
