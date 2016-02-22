package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class Strings implements Cloneable {

    public char[] value = null;

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

    public Strings clone() {
        try {
            return (Strings)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}