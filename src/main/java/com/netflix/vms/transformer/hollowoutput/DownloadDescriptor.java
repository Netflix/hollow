package com.netflix.vms.transformer.hollowoutput;


public class DownloadDescriptor {

    public AssetTypeDescriptor assetTypeDescriptor = null;
    public TimedTextTypeDescriptor timedTextTypeDescriptor = null;
    public int encodingProfileId = java.lang.Integer.MIN_VALUE;
    public VideoFormatDescriptor videoFormatDescriptor = null;
    public Strings audioLanguageBcp47code = null;
    public AssetMetaData assetMetaData = null;
    public Strings textLanguageBcp47code = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DownloadDescriptor))
            return false;

        DownloadDescriptor o = (DownloadDescriptor) other;
        if(o.assetTypeDescriptor == null) {
            if(assetTypeDescriptor != null) return false;
        } else if(!o.assetTypeDescriptor.equals(assetTypeDescriptor)) return false;
        if(o.timedTextTypeDescriptor == null) {
            if(timedTextTypeDescriptor != null) return false;
        } else if(!o.timedTextTypeDescriptor.equals(timedTextTypeDescriptor)) return false;
        if(o.encodingProfileId != encodingProfileId) return false;
        if(o.videoFormatDescriptor == null) {
            if(videoFormatDescriptor != null) return false;
        } else if(!o.videoFormatDescriptor.equals(videoFormatDescriptor)) return false;
        if(o.audioLanguageBcp47code == null) {
            if(audioLanguageBcp47code != null) return false;
        } else if(!o.audioLanguageBcp47code.equals(audioLanguageBcp47code)) return false;
        if(o.assetMetaData == null) {
            if(assetMetaData != null) return false;
        } else if(!o.assetMetaData.equals(assetMetaData)) return false;
        if(o.textLanguageBcp47code == null) {
            if(textLanguageBcp47code != null) return false;
        } else if(!o.textLanguageBcp47code.equals(textLanguageBcp47code)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}