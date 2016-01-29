package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class CodecPrivateDataString {

    public char[] value;

    public CodecPrivateDataString() { }

    public CodecPrivateDataString(char[] value) {
        this.value = value;
    }

    public CodecPrivateDataString(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CodecPrivateDataString))
            return false;

        CodecPrivateDataString o = (CodecPrivateDataString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}