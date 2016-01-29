package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.List;

public class SortedMapOfIntegerToListOfVideoEpisode {

    public Map<Integer, List<VideoEpisode>> map;

    public SortedMapOfIntegerToListOfVideoEpisode() { }

    public SortedMapOfIntegerToListOfVideoEpisode(Map<Integer, List<VideoEpisode>> value) {
        this.map = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SortedMapOfIntegerToListOfVideoEpisode))
            return false;

        SortedMapOfIntegerToListOfVideoEpisode o = (SortedMapOfIntegerToListOfVideoEpisode) other;
        if(o.map == null) {
            if(map != null) return false;
        } else if(!o.map.equals(map)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}