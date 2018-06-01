package com.netflix.vms.transformer.hollowoutput;


public class EncodeSummaryDescriptorData implements Cloneable {

    public AssetTypeDescriptor assetType = null;
    public TimedTextTypeDescriptor timedTextType = null;
    public Strings audioLanguage = null;
    public Strings textLanguage = null;
    public boolean isNative = false;
    @Deprecated
    public int encodingProfileId = java.lang.Integer.MIN_VALUE;
    public AudioChannelsDescriptor audioChannels;
    public boolean isSubtitleBurnedIn = false;
    public AssetMetaData assetMetaData = null;

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
        if(o.assetMetaData == null) {
            if(assetMetaData != null) return false;
        } else if(!o.assetMetaData.equals(assetMetaData)) return false;
        if(o.audioChannels == null) {
            if(audioChannels != null) return false;
        } else if(!o.audioChannels.equals(audioChannels)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (assetType == null ? 1237 : assetType.hashCode());
        hashCode = hashCode * 31 + (timedTextType == null ? 1237 : timedTextType.hashCode());
        hashCode = hashCode * 31 + (audioLanguage == null ? 1237 : audioLanguage.hashCode());
        hashCode = hashCode * 31 + (textLanguage == null ? 1237 : textLanguage.hashCode());
        hashCode = hashCode * 31 + (isNative? 1231 : 1237);
        hashCode = hashCode * 31 + encodingProfileId;
        hashCode = hashCode * 31 + (isSubtitleBurnedIn? 1231 : 1237);
        hashCode = hashCode * 31 + (assetMetaData == null ? 1237 : assetMetaData.hashCode());
        hashCode = hashCode * 31 + (audioChannels == null ? 1237 : audioChannels.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("EncodeSummaryDescriptorData{");
        builder.append("assetType=").append(assetType);
        builder.append(",timedTextType=").append(timedTextType);
        builder.append(",audioLanguage=").append(audioLanguage);
        builder.append(",textLanguage=").append(textLanguage);
        builder.append(",isNative=").append(isNative);
        builder.append(",encodingProfileId=").append(encodingProfileId);
        builder.append(",audioChannels=").append(audioChannels);
        builder.append(",isSubtitleBurnedIn=").append(isSubtitleBurnedIn);
        builder.append(",assetMetaData=").append(assetMetaData);
        builder.append("}");
        return builder.toString();
    }

    public EncodeSummaryDescriptorData clone() {
        try {
            EncodeSummaryDescriptorData clone = (EncodeSummaryDescriptorData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
