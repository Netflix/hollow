package com.netflix.vms.transformer.hollowoutput;


public class DownloadLocation {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}