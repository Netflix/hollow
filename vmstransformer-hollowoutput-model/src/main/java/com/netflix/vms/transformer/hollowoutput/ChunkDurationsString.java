package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ChunkDurationsString implements Cloneable {

    public char[] value = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ChunkDurationsString{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public ChunkDurationsString clone() {
        try {
            ChunkDurationsString clone = (ChunkDurationsString)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
