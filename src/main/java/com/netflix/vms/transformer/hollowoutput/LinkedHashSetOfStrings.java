package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.write.objectmapper.HollowTypeName;
import java.util.List;

public class LinkedHashSetOfStrings implements Cloneable {

    @HollowTypeName(name="LinkedHashSetOfStrings_ordinals")
    public List<Strings> ordinals = null;

    public LinkedHashSetOfStrings() { }

    public LinkedHashSetOfStrings(List<Strings> value) {
        this.ordinals = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof LinkedHashSetOfStrings))
            return false;

        LinkedHashSetOfStrings o = (LinkedHashSetOfStrings) other;
        if(o.ordinals == null) {
            if(ordinals != null) return false;
        } else if(!o.ordinals.equals(ordinals)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (ordinals == null ? 1237 : ordinals.hashCode());
        return hashCode;
    }

    public LinkedHashSetOfStrings clone() {
        try {
            LinkedHashSetOfStrings clone = (LinkedHashSetOfStrings)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}