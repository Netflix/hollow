package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class Strings {

    public char[] value;

    public Strings() { }

    public Strings(char[] value) {
        this.value = value;
    }

    public Strings(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Strings))
            return false;

        Strings o = (Strings) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}