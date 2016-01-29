package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;

public class DrmInfo {

    public int drmKeyGroup;
    public DrmKey drmKey;
    public Map<Integer, DrmHeader> drmHeaders;

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