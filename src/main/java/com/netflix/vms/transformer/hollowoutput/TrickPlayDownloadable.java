package com.netflix.vms.transformer.hollowoutput;


public class TrickPlayDownloadable implements Cloneable {

    public Strings fileName = null;
    public BaseDownloadable baseDownloadable = null;
    public TrickPlayDescriptor descriptor = null;

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

    public TrickPlayDownloadable clone() {
        try {
            TrickPlayDownloadable clone = (TrickPlayDownloadable)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}