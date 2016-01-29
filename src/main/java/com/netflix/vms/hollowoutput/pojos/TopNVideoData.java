package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.Arrays;

public class TopNVideoData {

    public char[] countryId;
    public Map<Integer, Float> videoViewHrs1Day;
    public float countryViewHrs1Day;

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