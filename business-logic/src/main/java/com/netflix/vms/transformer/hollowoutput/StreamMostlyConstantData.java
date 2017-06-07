package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.List;

public class StreamMostlyConstantData implements Cloneable {

    public int deploymentLabel = java.lang.Integer.MIN_VALUE;
    public int conformingGroupId = java.lang.Integer.MIN_VALUE;
    public char[] s3FullPath = null;
    public ImageSubtitleIndexByteRange imageSubtitleIndexByteRange = null;
    public List<Strings> tags = null;
    public int deploymentPriority = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamMostlyConstantData))
            return false;

        StreamMostlyConstantData o = (StreamMostlyConstantData) other;
        if(o.deploymentLabel != deploymentLabel) return false;
        if(o.conformingGroupId != conformingGroupId) return false;
        if(!Arrays.equals(o.s3FullPath, s3FullPath)) return false;
        if(o.imageSubtitleIndexByteRange == null) {
            if(imageSubtitleIndexByteRange != null) return false;
        } else if(!o.imageSubtitleIndexByteRange.equals(imageSubtitleIndexByteRange)) return false;
        if(o.tags == null) {
            if(tags != null) return false;
        } else if(!o.tags.equals(tags)) return false;
        if(o.deploymentPriority != deploymentPriority) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + deploymentLabel;
        hashCode = hashCode * 31 + conformingGroupId;
        hashCode = hashCode * 31 + Arrays.hashCode(s3FullPath);
        hashCode = hashCode * 31 + (imageSubtitleIndexByteRange == null ? 1237 : imageSubtitleIndexByteRange.hashCode());
        hashCode = hashCode * 31 + (tags == null ? 1237 : tags.hashCode());
        hashCode = hashCode * 31 + deploymentPriority;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamMostlyConstantData{");
        builder.append("deploymentLabel=").append(deploymentLabel);
        builder.append(",conformingGroupId=").append(conformingGroupId);
        builder.append(",s3FullPath=").append(s3FullPath);
        builder.append(",imageSubtitleIndexByteRange=").append(imageSubtitleIndexByteRange);
        builder.append(",tags=").append(tags);
        builder.append(",deploymentPriority=").append(deploymentPriority);
        builder.append("}");
        return builder.toString();
    }

    public StreamMostlyConstantData clone() {
        try {
            StreamMostlyConstantData clone = (StreamMostlyConstantData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
