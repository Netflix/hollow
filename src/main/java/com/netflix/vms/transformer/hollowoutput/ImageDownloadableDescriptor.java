package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ImageDownloadableDescriptor implements Cloneable {

    public int streamProfileId = java.lang.Integer.MIN_VALUE;
    public VideoResolution videoResolution = null;
    public TargetDimensions targetDimensions = null;
    public VideoFormatDescriptor videoFormat = null;
    public char[] envBasedDirectory = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ImageDownloadableDescriptor))
            return false;

        ImageDownloadableDescriptor o = (ImageDownloadableDescriptor) other;
        if(o.streamProfileId != streamProfileId) return false;
        if(o.videoResolution == null) {
            if(videoResolution != null) return false;
        } else if(!o.videoResolution.equals(videoResolution)) return false;
        if(o.targetDimensions == null) {
            if(targetDimensions != null) return false;
        } else if(!o.targetDimensions.equals(targetDimensions)) return false;
        if(o.videoFormat == null) {
            if(videoFormat != null) return false;
        } else if(!o.videoFormat.equals(videoFormat)) return false;
        if(!Arrays.equals(o.envBasedDirectory, envBasedDirectory)) return false;
        return true;
    }

    public ImageDownloadableDescriptor clone() {
        try {
            return (ImageDownloadableDescriptor)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}