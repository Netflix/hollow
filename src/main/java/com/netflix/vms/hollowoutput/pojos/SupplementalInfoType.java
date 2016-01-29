package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class SupplementalInfoType {

    public char[] value;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}