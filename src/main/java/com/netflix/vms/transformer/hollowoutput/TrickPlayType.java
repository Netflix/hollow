package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TrickPlayType implements Cloneable {

    public char[] value = null;

    public TrickPlayType() { }

    public TrickPlayType(char[] value) {
        this.value = value;
    }

    public TrickPlayType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayType))
            return false;

        TrickPlayType o = (TrickPlayType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public TrickPlayType clone() {
        try {
            return (TrickPlayType)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}