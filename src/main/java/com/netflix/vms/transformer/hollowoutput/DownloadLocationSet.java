package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class DownloadLocationSet {

    public Strings filename;
    public List<DownloadLocation> locations;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}