package com.netflix.vms.transformer.hollowoutput;


public class DownloadDescriptor implements Cloneable {

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (assetTypeDescriptor == null ? 1237 : assetTypeDescriptor.hashCode());
        hashCode = hashCode * 31 + (timedTextTypeDescriptor == null ? 1237 : timedTextTypeDescriptor.hashCode());
        hashCode = hashCode * 31 + encodingProfileId;
        hashCode = hashCode * 31 + (videoFormatDescriptor == null ? 1237 : videoFormatDescriptor.hashCode());
        hashCode = hashCode * 31 + (audioLanguageBcp47code == null ? 1237 : audioLanguageBcp47code.hashCode());
        hashCode = hashCode * 31 + (assetMetaData == null ? 1237 : assetMetaData.hashCode());
        hashCode = hashCode * 31 + (textLanguageBcp47code == null ? 1237 : textLanguageBcp47code.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DownloadDescriptor{");
        builder.append("assetTypeDescriptor=").append(assetTypeDescriptor);
        builder.append(",timedTextTypeDescriptor=").append(timedTextTypeDescriptor);
        builder.append(",encodingProfileId=").append(encodingProfileId);
        builder.append(",videoFormatDescriptor=").append(videoFormatDescriptor);
        builder.append(",audioLanguageBcp47code=").append(audioLanguageBcp47code);
        builder.append(",assetMetaData=").append(assetMetaData);
        builder.append(",textLanguageBcp47code=").append(textLanguageBcp47code);
        builder.append("}");
        return builder.toString();
    }

    public DownloadDescriptor clone() {
        try {
            DownloadDescriptor clone = (DownloadDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
