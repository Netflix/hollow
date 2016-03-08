package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class ImageDownloadable implements Cloneable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public List<Strings> originServerNames = null;
    public ImageDownloadableDescriptor descriptor = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (downloadableId ^ (downloadableId >>> 32));
        hashCode = hashCode * 31 + (originServerNames == null ? 1237 : originServerNames.hashCode());
        hashCode = hashCode * 31 + (descriptor == null ? 1237 : descriptor.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ImageDownloadable{");
        builder.append("downloadableId=").append(downloadableId);
        builder.append(",originServerNames=").append(originServerNames);
        builder.append(",descriptor=").append(descriptor);
        builder.append("}");
        return builder.toString();
    }

    public ImageDownloadable clone() {
        try {
            ImageDownloadable clone = (ImageDownloadable)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}