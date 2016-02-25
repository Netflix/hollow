package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.write.objectmapper.HollowTypeName;
import java.util.List;
import java.util.Map;

public class SortedMapOfDateWindowToListOfInteger implements Cloneable {

    @HollowTypeName(name="SortedMapOfDateWindowToListOfInteger_map")
    public Map<DateWindow, List<Integer>> map = null;

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

    public SortedMapOfDateWindowToListOfInteger clone() {
        try {
            SortedMapOfDateWindowToListOfInteger clone = (SortedMapOfDateWindowToListOfInteger)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}