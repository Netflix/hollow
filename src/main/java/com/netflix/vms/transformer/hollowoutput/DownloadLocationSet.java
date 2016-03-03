package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class DownloadLocationSet implements Cloneable {

    public Strings filename = null;
    public List<DownloadLocation> locations = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DownloadLocationSet))
            return false;

        DownloadLocationSet o = (DownloadLocationSet) other;
        if(o.filename == null) {
            if(filename != null) return false;
        } else if(!o.filename.equals(filename)) return false;
        if(o.locations == null) {
            if(locations != null) return false;
        } else if(!o.locations.equals(locations)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (filename == null ? 1237 : filename.hashCode());
        hashCode = hashCode * 31 + (locations == null ? 1237 : locations.hashCode());
        return hashCode;
    }

    public DownloadLocationSet clone() {
        try {
            DownloadLocationSet clone = (DownloadLocationSet)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}