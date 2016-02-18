package com.netflix.vms.transformer.hollowoutput;


public class TrickPlayDownloadable {

    public Strings fileName;
    public BaseDownloadable baseDownloadable;
    public TrickPlayDescriptor descriptor;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayDownloadable))
            return false;

        TrickPlayDownloadable o = (TrickPlayDownloadable) other;
        if(o.fileName == null) {
            if(fileName != null) return false;
        } else if(!o.fileName.equals(fileName)) return false;
        if(o.baseDownloadable == null) {
            if(baseDownloadable != null) return false;
        } else if(!o.baseDownloadable.equals(baseDownloadable)) return false;
        if(o.descriptor == null) {
            if(descriptor != null) return false;
        } else if(!o.descriptor.equals(descriptor)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}