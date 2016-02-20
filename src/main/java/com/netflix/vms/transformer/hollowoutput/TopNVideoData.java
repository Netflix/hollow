package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Map;

public class TopNVideoData {

    public char[] countryId = null;
    public Map<Integer, Float> videoViewHrs1Day = null;
    public float countryViewHrs1Day = java.lang.Float.NaN;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TopNVideoData))
            return false;

        TopNVideoData o = (TopNVideoData) other;
        if(!Arrays.equals(o.countryId, countryId)) return false;
        if(o.videoViewHrs1Day == null) {
            if(videoViewHrs1Day != null) return false;
        } else if(!o.videoViewHrs1Day.equals(videoViewHrs1Day)) return false;
        if(o.countryViewHrs1Day != countryViewHrs1Day) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}