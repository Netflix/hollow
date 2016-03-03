package com.netflix.vms.transformer.hollowoutput;


public class StreamData implements Cloneable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public int packageId = java.lang.Integer.MIN_VALUE;
    public long fileSizeInBytes = java.lang.Long.MIN_VALUE;
    public long creationTimeStampInSeconds = java.lang.Long.MIN_VALUE;
    public StreamDrmData drmData = null;
    public StreamHashData hashData = null;
    public StreamAdditionalData additionalData = null;
    public DownloadDescriptor downloadDescriptor = null;
    public StreamDataDescriptor streamDataDescriptor = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamData))
            return false;

        StreamData o = (StreamData) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.packageId != packageId) return false;
        if(o.fileSizeInBytes != fileSizeInBytes) return false;
        if(o.creationTimeStampInSeconds != creationTimeStampInSeconds) return false;
        if(o.drmData == null) {
            if(drmData != null) return false;
        } else if(!o.drmData.equals(drmData)) return false;
        if(o.hashData == null) {
            if(hashData != null) return false;
        } else if(!o.hashData.equals(hashData)) return false;
        if(o.additionalData == null) {
            if(additionalData != null) return false;
        } else if(!o.additionalData.equals(additionalData)) return false;
        if(o.downloadDescriptor == null) {
            if(downloadDescriptor != null) return false;
        } else if(!o.downloadDescriptor.equals(downloadDescriptor)) return false;
        if(o.streamDataDescriptor == null) {
            if(streamDataDescriptor != null) return false;
        } else if(!o.streamDataDescriptor.equals(streamDataDescriptor)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (downloadableId ^ (downloadableId >>> 32));
        hashCode = hashCode * 31 + packageId;
        hashCode = hashCode * 31 + (int) (fileSizeInBytes ^ (fileSizeInBytes >>> 32));
        hashCode = hashCode * 31 + (int) (creationTimeStampInSeconds ^ (creationTimeStampInSeconds >>> 32));
        hashCode = hashCode * 31 + (drmData == null ? 1237 : drmData.hashCode());
        hashCode = hashCode * 31 + (hashData == null ? 1237 : hashData.hashCode());
        hashCode = hashCode * 31 + (additionalData == null ? 1237 : additionalData.hashCode());
        hashCode = hashCode * 31 + (downloadDescriptor == null ? 1237 : downloadDescriptor.hashCode());
        hashCode = hashCode * 31 + (streamDataDescriptor == null ? 1237 : streamDataDescriptor.hashCode());
        return hashCode;
    }

    public StreamData clone() {
        try {
            StreamData clone = (StreamData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}