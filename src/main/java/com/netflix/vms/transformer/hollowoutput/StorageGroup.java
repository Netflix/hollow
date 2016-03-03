package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Set;

public class StorageGroup implements Cloneable {

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

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (idStr == null ? 1237 : idStr.hashCode());
        hashCode = hashCode * 31 + (countries == null ? 1237 : countries.hashCode());
        return hashCode;
    }

    public StorageGroup clone() {
        try {
            StorageGroup clone = (StorageGroup)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}