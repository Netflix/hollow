package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class VideoSetType {

    public char[] value;

    public VideoSetType() { }

    public VideoSetType(char[] value) {
        this.value = value;
    }

    public VideoSetType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoSetType))
            return false;

        VideoSetType o = (VideoSetType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}