package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TrickPlayDownloadableFilename implements Cloneable {

    public char[] value = null;

    public TrickPlayDownloadableFilename() { }

    public TrickPlayDownloadableFilename(char[] value) {
        this.value = value;
    }

    public TrickPlayDownloadableFilename(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayDownloadableFilename))
            return false;

        TrickPlayDownloadableFilename o = (TrickPlayDownloadableFilename) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Strings{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public TrickPlayDownloadableFilename clone() {
        try {
            TrickPlayDownloadableFilename clone = (TrickPlayDownloadableFilename)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}