package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class VideoNodeType implements Cloneable {

    public char[] value = null;

    public VideoNodeType() { }

    public VideoNodeType(char[] value) {
        this.value = value;
    }

    public VideoNodeType(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoNodeType))
            return false;

        VideoNodeType o = (VideoNodeType) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public VideoNodeType clone() {
        try {
            VideoNodeType clone = (VideoNodeType)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}