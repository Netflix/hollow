package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Set;

public class StorageGroup {

    public char[] idStr = null;
    public Set<ISOCountry> countries = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StorageGroup))
            return false;

        StorageGroup o = (StorageGroup) other;
        if(!Arrays.equals(o.idStr, idStr)) return false;
        if(o.countries == null) {
            if(countries != null) return false;
        } else if(!o.countries.equals(countries)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}