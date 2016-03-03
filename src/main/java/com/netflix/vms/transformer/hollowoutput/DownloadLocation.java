package com.netflix.vms.transformer.hollowoutput;


public class DownloadLocation implements Cloneable {

    public Strings directory = null;
    public Strings originServerName = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DownloadLocation))
            return false;

        DownloadLocation o = (DownloadLocation) other;
        if(o.directory == null) {
            if(directory != null) return false;
        } else if(!o.directory.equals(directory)) return false;
        if(o.originServerName == null) {
            if(originServerName != null) return false;
        } else if(!o.originServerName.equals(originServerName)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (directory == null ? 1237 : directory.hashCode());
        hashCode = hashCode * 31 + (originServerName == null ? 1237 : originServerName.hashCode());
        return hashCode;
    }

    public DownloadLocation clone() {
        try {
            DownloadLocation clone = (DownloadLocation)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}