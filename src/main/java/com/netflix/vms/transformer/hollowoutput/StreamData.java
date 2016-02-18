package com.netflix.vms.transformer.hollowoutput;


public class StreamData {

    public long downloadableId;
    public int packageId;
    public long fileSizeInBytes;
    public long creationTimeStampInSeconds;
    public StreamDrmData drmData;
    public StreamHashData hashData;
    public StreamAdditionalData additionalData;
    public DownloadDescriptor downloadDescriptor;
    public StreamDataDescriptor streamDataDescriptor;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}