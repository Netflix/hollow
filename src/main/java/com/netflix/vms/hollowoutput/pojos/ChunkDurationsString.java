package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class ChunkDurationsString {

    public char[] value;

    public ChunkDurationsString() { }

    public ChunkDurationsString(char[] value) {
        this.value = value;
    }

    public ChunkDurationsString(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ChunkDurationsString))
            return false;

        ChunkDurationsString o = (ChunkDurationsString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}