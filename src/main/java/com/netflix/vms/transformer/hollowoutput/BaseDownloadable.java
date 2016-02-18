package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.List;

public class BaseDownloadable {

    public long downloadableId;
    public int streamProfileId;
    public List<Strings> originServerNames;
    public char[] envBasedDirectory;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}