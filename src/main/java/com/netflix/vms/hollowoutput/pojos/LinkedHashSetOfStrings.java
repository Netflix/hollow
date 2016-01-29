package com.netflix.vms.hollowoutput.pojos;

import java.util.List;

public class LinkedHashSetOfStrings {

    public List<Strings> ordinals;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}