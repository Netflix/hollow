package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields="downloadableId")
public class StreamData implements Cloneable {

    public DownloadableId downloadableId = null;
    public int packageId = java.lang.Integer.MIN_VALUE;
    public long fileSizeInBytes = java.lang.Long.MIN_VALUE;
    public long creationTimeStampInSeconds = java.lang.Long.MIN_VALUE;
    public Strings encodingAlgorithmHash = null;
    public StreamDrmData drmData = null;
    public StreamAdditionalData additionalData = null;
    public DownloadDescriptor downloadDescriptor = null;
    public StreamDataDescriptor streamDataDescriptor = null;
    
    /// hash data
    public long cRC32Hash = java.lang.Long.MIN_VALUE;
    public long sha1_1 = java.lang.Long.MIN_VALUE;
    public long sha1_2 = java.lang.Long.MIN_VALUE;
    public long sha1_3 = java.lang.Long.MIN_VALUE;


    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamData))
            return false;

        StreamData o = (StreamData) other;
        if(o.downloadableId == null) {
            if(downloadableId != null) return false;
        } else if(!o.downloadableId.equals(downloadableId)) return false;
        if(o.packageId != packageId) return false;
        if(o.fileSizeInBytes != fileSizeInBytes) return false;
        if(o.creationTimeStampInSeconds != creationTimeStampInSeconds) return false;
        if(o.drmData == null) {
            if(drmData != null) return false;
        } else if(!o.drmData.equals(drmData)) return false;
        if(o.additionalData == null) {
            if(additionalData != null) return false;
        } else if(!o.additionalData.equals(additionalData)) return false;
        if(o.downloadDescriptor == null) {
            if(downloadDescriptor != null) return false;
        } else if(!o.downloadDescriptor.equals(downloadDescriptor)) return false;
        if(o.streamDataDescriptor == null) {
            if(streamDataDescriptor != null) return false;
        } else if(!o.streamDataDescriptor.equals(streamDataDescriptor)) return false;
        if(o.encodingAlgorithmHash == null) {
        	if(encodingAlgorithmHash != null) return false;
        } else if(!o.encodingAlgorithmHash.equals(encodingAlgorithmHash)) return false;
        	
        if(o.cRC32Hash != cRC32Hash) return false;
        if(o.sha1_1 != sha1_1) return false;
        if(o.sha1_2 != sha1_2) return false;
        if(o.sha1_3 != sha1_3) return false;

        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (downloadableId == null ? 1237 : downloadableId.hashCode());
        hashCode = hashCode * 31 + packageId;
        hashCode = hashCode * 31 + (int) (fileSizeInBytes ^ (fileSizeInBytes >>> 32));
        hashCode = hashCode * 31 + (int) (creationTimeStampInSeconds ^ (creationTimeStampInSeconds >>> 32));
        hashCode = hashCode * 31 + (drmData == null ? 1237 : drmData.hashCode());
        hashCode = hashCode * 31 + (additionalData == null ? 1237 : additionalData.hashCode());
        hashCode = hashCode * 31 + (downloadDescriptor == null ? 1237 : downloadDescriptor.hashCode());
        hashCode = hashCode * 31 + (streamDataDescriptor == null ? 1237 : streamDataDescriptor.hashCode());
        hashCode = hashCode * 31 + (encodingAlgorithmHash == null ? 1237 : encodingAlgorithmHash.hashCode());
        hashCode = hashCode * 31 + (int) (cRC32Hash ^ (cRC32Hash >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_1 ^ (sha1_1 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_2 ^ (sha1_2 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_3 ^ (sha1_3 >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamData{");
        builder.append("downloadableId=").append(downloadableId);
        builder.append(",packageId=").append(packageId);
        builder.append(",fileSizeInBytes=").append(fileSizeInBytes);
        builder.append(",creationTimeStampInSeconds=").append(creationTimeStampInSeconds);
        builder.append(",drmData=").append(drmData);
        builder.append(",additionalData=").append(additionalData);
        builder.append(",downloadDescriptor=").append(downloadDescriptor);
        builder.append(",streamDataDescriptor=").append(streamDataDescriptor);
        builder.append(",encodingAlgorithmHash=").append(encodingAlgorithmHash);
        builder.append(",cRC32Hash=").append(cRC32Hash);
        builder.append(",sha1_1=").append(sha1_1);
        builder.append(",sha1_2=").append(sha1_2);
        builder.append(",sha1_3=").append(sha1_3);

        builder.append("}");
        return builder.toString();
    }

    public StreamData clone() {
        try {
            StreamData clone = (StreamData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
