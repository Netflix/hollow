package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class TrailerInfo implements Cloneable {

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + Arrays.hashCode(imageTag);
        hashCode = hashCode * 31 + Arrays.hashCode(imageBackgroundTone);
        hashCode = hashCode * 31 + mapIndex;
        hashCode = hashCode * 31 + seasonNumber;
        hashCode = hashCode * 31 + Arrays.hashCode(subtitleLocale);
        hashCode = hashCode * 31 + Arrays.hashCode(video);
        hashCode = hashCode * 31 + videoLength;
        hashCode = hashCode * 31 + videoOffset;
        hashCode = hashCode * 31 + Arrays.hashCode(videoValue);
        hashCode = hashCode * 31 + priority;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TrailerInfo{");
        builder.append("type=").append(type);
        builder.append(",imageTag=").append(imageTag);
        builder.append(",imageBackgroundTone=").append(imageBackgroundTone);
        builder.append(",mapIndex=").append(mapIndex);
        builder.append(",seasonNumber=").append(seasonNumber);
        builder.append(",subtitleLocale=").append(subtitleLocale);
        builder.append(",video=").append(video);
        builder.append(",videoLength=").append(videoLength);
        builder.append(",videoOffset=").append(videoOffset);
        builder.append(",videoValue=").append(videoValue);
        builder.append(",priority=").append(priority);
        builder.append("}");
        return builder.toString();
    }

    public TrailerInfo clone() {
        try {
            TrailerInfo clone = (TrailerInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}