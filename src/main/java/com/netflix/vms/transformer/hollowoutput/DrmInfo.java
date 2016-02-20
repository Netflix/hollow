package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class DrmInfo {

    public int drmKeyGroup = java.lang.Integer.MIN_VALUE;
    public DrmKey drmKey = null;
    public Map<Integer, DrmHeader> drmHeaders = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmInfo))
            return false;

        DrmInfo o = (DrmInfo) other;
        if(o.drmKeyGroup != drmKeyGroup) return false;
        if(o.drmKey == null) {
            if(drmKey != null) return false;
        } else if(!o.drmKey.equals(drmKey)) return false;
        if(o.drmHeaders == null) {
            if(drmHeaders != null) return false;
        } else if(!o.drmHeaders.equals(drmHeaders)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}