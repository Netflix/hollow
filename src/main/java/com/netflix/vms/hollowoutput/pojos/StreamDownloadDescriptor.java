package com.netflix.vms.hollowoutput.pojos;


public class StreamDownloadDescriptor {

    public ProfileTypeDescriptor profileType;
    public int streamProfileId;
    public VideoResolution videoResolution;
    public PixelAspect pixelAspect;
    public int bitrate;
    public Strings envBasedDirectory;
    public Strings languageBcp47code;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}