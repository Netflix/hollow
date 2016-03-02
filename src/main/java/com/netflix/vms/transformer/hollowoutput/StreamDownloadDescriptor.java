package com.netflix.vms.transformer.hollowoutput;


public class StreamDownloadDescriptor implements Cloneable {

    public ProfileTypeDescriptor profileType = null;
    public int streamProfileId = java.lang.Integer.MIN_VALUE;
    public VideoResolution videoResolution = null;
    public PixelAspect pixelAspect = null;
    public int bitrate = java.lang.Integer.MIN_VALUE;
    public Strings envBasedDirectory = null;
    public Strings languageBcp47code = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamDownloadDescriptor))
            return false;

        StreamDownloadDescriptor o = (StreamDownloadDescriptor) other;
        if(o.profileType == null) {
            if(profileType != null) return false;
        } else if(!o.profileType.equals(profileType)) return false;
        if(o.streamProfileId != streamProfileId) return false;
        if(o.videoResolution == null) {
            if(videoResolution != null) return false;
        } else if(!o.videoResolution.equals(videoResolution)) return false;
        if(o.pixelAspect == null) {
            if(pixelAspect != null) return false;
        } else if(!o.pixelAspect.equals(pixelAspect)) return false;
        if(o.bitrate != bitrate) return false;
        if(o.envBasedDirectory == null) {
            if(envBasedDirectory != null) return false;
        } else if(!o.envBasedDirectory.equals(envBasedDirectory)) return false;
        if(o.languageBcp47code == null) {
            if(languageBcp47code != null) return false;
        } else if(!o.languageBcp47code.equals(languageBcp47code)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (profileType == null ? 1237 : profileType.hashCode());
        hashCode = hashCode * 31 + streamProfileId;
        hashCode = hashCode * 31 + (videoResolution == null ? 1237 : videoResolution.hashCode());
        hashCode = hashCode * 31 + (pixelAspect == null ? 1237 : pixelAspect.hashCode());
        hashCode = hashCode * 31 + bitrate;
        hashCode = hashCode * 31 + (envBasedDirectory == null ? 1237 : envBasedDirectory.hashCode());
        hashCode = hashCode * 31 + (languageBcp47code == null ? 1237 : languageBcp47code.hashCode());
        return hashCode;
    }

    public StreamDownloadDescriptor clone() {
        try {
            StreamDownloadDescriptor clone = (StreamDownloadDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}