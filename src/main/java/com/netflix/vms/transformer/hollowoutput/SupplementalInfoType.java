package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class SupplementalInfoType implements Cloneable {

    public char[] value = null;

    public SupplementalInfoType() { }

    public SupplementalInfoType(char[] value) {
        this.value = value;
    }

    public SupplementalInfoType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SupplementalInfoType))
            return false;

        SupplementalInfoType o = (SupplementalInfoType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public SupplementalInfoType clone() {
        try {
            return (SupplementalInfoType)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}