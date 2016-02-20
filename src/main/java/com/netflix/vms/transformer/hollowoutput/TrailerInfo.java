package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TrailerInfo {

    public SupplementalInfoType type = null;
    public char[] imageTag = null;
    public char[] imageBackgroundTone = null;
    public int mapIndex = java.lang.Integer.MIN_VALUE;
    public int seasonNumber = java.lang.Integer.MIN_VALUE;
    public char[] subtitleLocale = null;
    public char[] video = null;
    public int videoLength = java.lang.Integer.MIN_VALUE;
    public int videoOffset = java.lang.Integer.MIN_VALUE;
    public char[] videoValue = null;
    public int priority = java.lang.Integer.MIN_VALUE;

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