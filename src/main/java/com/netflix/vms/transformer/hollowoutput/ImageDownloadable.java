package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class ImageDownloadable {

    public long downloadableId;
    public List<Strings> originServerNames;
    public ImageDownloadableDescriptor descriptor;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ImageDownloadable))
            return false;

        ImageDownloadable o = (ImageDownloadable) other;
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