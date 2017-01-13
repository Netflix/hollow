package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.List;
import java.util.Map;

public class SortedMapOfIntegerToListOfVideoEpisode implements Cloneable {

    @HollowTypeName(name="SortedMapOfIntegerToListOfVideoEpisode_map")
    public Map<Integer, List<VideoEpisode>> map = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (map == null ? 1237 : map.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("SortedMapOfIntegerToListOfVideoEpisode{");
        builder.append("map=").append(map);
        builder.append("}");
        return builder.toString();
    }

    public SortedMapOfIntegerToListOfVideoEpisode clone() {
        try {
            SortedMapOfIntegerToListOfVideoEpisode clone = (SortedMapOfIntegerToListOfVideoEpisode)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}