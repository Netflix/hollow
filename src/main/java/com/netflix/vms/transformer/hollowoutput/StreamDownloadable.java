package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class StreamDownloadable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public List<Strings> originServerNames = null;
    public StreamDownloadDescriptor descriptor = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDownloadable))
            return false;

        StreamDownloadable o = (StreamDownloadable) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.originServerNames == null) {
            if(originServerNames != null) return false;
        } else if(!o.originServerNames.equals(originServerNames)) return false;
        if(o.descriptor == null) {
            if(descriptor != null) return false;
        } else if(!o.descriptor.equals(descriptor)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}