package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class TrailerInfo {

    public SupplementalInfoType type;
    public char[] imageTag;
    public char[] imageBackgroundTone;
    public int mapIndex;
    public int seasonNumber;
    public char[] subtitleLocale;
    public char[] video;
    public int videoLength;
    public int videoOffset;
    public char[] videoValue;
    public int priority;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrailerInfo))
            return false;

        TrailerInfo o = (TrailerInfo) other;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(!Arrays.equals(o.imageTag, imageTag)) return false;
        if(!Arrays.equals(o.imageBackgroundTone, imageBackgroundTone)) return false;
        if(o.mapIndex != mapIndex) return false;
        if(o.seasonNumber != seasonNumber) return false;
        if(!Arrays.equals(o.subtitleLocale, subtitleLocale)) return false;
        if(!Arrays.equals(o.video, video)) return false;
        if(o.videoLength != videoLength) return false;
        if(o.videoOffset != videoOffset) return false;
        if(!Arrays.equals(o.videoValue, videoValue)) return false;
        if(o.priority != priority) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}