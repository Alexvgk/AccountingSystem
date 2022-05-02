package com.prokopchyk.building.flatComparator;

import com.prokopchyk.building.Flat;

import java.util.Comparator;

public class FlatAreaCompare  implements Comparator<Flat> {
    @Override
    public int compare(Flat o1, Flat o2) {
        if(o1.getArea() > o2.getArea())
            return 1;
        if(o1.getArea() == o2.getArea())
            return 0;
        else
            return -1;
    }
}
