package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class ISOCountry {

    public char[] id;

    public ISOCountry() { }

    public ISOCountry(char[] value) {
        this.id = value;
    }

    public ISOCountry(String value) {
        this.id = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ISOCountry))
            return false;

        ISOCountry o = (ISOCountry) other;
        if(!Arrays.equals(o.id, id)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}