package com.netflix.vms.transformer.hollowoutput;


public class EncodeSummaryDescriptorData {

    public AssetTypeDescriptor assetType;
    public TimedTextTypeDescriptor timedTextType;
    public Strings audioLanguage;
    public Strings textLanguage;
    public boolean isNative;
    public int encodingProfileId;
    public boolean isSubtitleBurnedIn;
    public boolean isImageBasedSubtitles;
    public AssetMetaData assetMetaData;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof EncodeSummaryDescriptorData))
            return false;

        EncodeSummaryDescriptorData o = (EncodeSummaryDescriptorData) other;
        if(o.assetType == null) {
            if(assetType != null) return false;
        } else if(!o.assetType.equals(assetType)) return false;
        if(o.timedTextType == null) {
            if(timedTextType != null) return false;
        } else if(!o.timedTextType.equals(timedTextType)) return false;
        if(o.audioLanguage == null) {
            if(audioLanguage != null) return false;
        } else if(!o.audioLanguage.equals(audioLanguage)) return false;
        if(o.textLanguage == null) {
            if(textLanguage != null) return false;
        } else if(!o.textLanguage.equals(textLanguage)) return false;
        if(o.isNative != isNative) return false;
        if(o.encodingProfileId != encodingProfileId) return false;
        if(o.isSubtitleBurnedIn != isSubtitleBurnedIn) return false;
        if(o.isImageBasedSubtitles != isImageBasedSubtitles) return false;
        if(o.assetMetaData == null) {
            if(assetMetaData != null) return false;
        } else if(!o.assetMetaData.equals(assetMetaData)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}