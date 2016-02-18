package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;
import java.util.List;

public class SortedMapOfDateWindowToListOfInteger {

    public Map<DateWindow, List<Integer>> map;

    public SortedMapOfDateWindowToListOfInteger() { }

    public SortedMapOfDateWindowToListOfInteger(Map<DateWindow, List<Integer>> value) {
        this.map = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SortedMapOfDateWindowToListOfInteger))
            return false;

        SortedMapOfDateWindowToListOfInteger o = (SortedMapOfDateWindowToListOfInteger) other;
        if(o.map == null) {
            if(map != null) return false;
        } else if(!o.map.equals(map)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}