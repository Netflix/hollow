package com.netflix.vms.transformer.hollowoutput;


public class StreamDrmData {

    public DrmKey drmKey;
    public WmDrmKey wmDrmKey;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDrmData))
            return false;

        StreamDrmData o = (StreamDrmData) other;
        if(o.drmKey == null) {
            if(drmKey != null) return false;
        } else if(!o.drmKey.equals(drmKey)) return false;
        if(o.wmDrmKey == null) {
            if(wmDrmKey != null) return false;
        } else if(!o.wmDrmKey.equals(wmDrmKey)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}