package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.List;

public class BaseDownloadable implements Cloneable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public int streamProfileId = java.lang.Integer.MIN_VALUE;
    public List<Strings> originServerNames = null;
    public char[] envBasedDirectory = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof BaseDownloadable))
            return false;

        BaseDownloadable o = (BaseDownloadable) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.streamProfileId != streamProfileId) return false;
        if(o.originServerNames == null) {
            if(originServerNames != null) return false;
        } else if(!o.originServerNames.equals(originServerNames)) return false;
        if(!Arrays.equals(o.envBasedDirectory, envBasedDirectory)) return false;
        return true;
    }

    public BaseDownloadable clone() {
        try {
            BaseDownloadable clone = (BaseDownloadable)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}