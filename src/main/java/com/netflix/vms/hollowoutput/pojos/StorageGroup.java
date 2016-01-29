package com.netflix.vms.hollowoutput.pojos;

import java.util.Set;
import java.util.Arrays;

public class StorageGroup {

    public char[] idStr;
    public Set<ISOCountry> countries;

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