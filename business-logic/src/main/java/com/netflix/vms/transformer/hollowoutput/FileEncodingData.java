package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields="downloadableId")
public class FileEncodingData implements Cloneable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public CodecPrivateDataString codecPrivateData = null;
    public ChunkDurationsString chunkDurations = null;
    public long dashHeaderSize = java.lang.Long.MIN_VALUE;
    public long dashMediaStartByteOffset = java.lang.Long.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof FileEncodingData))
            return false;

        FileEncodingData o = (FileEncodingData) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.codecPrivateData == null) {
            if(codecPrivateData != null) return false;
        } else if(!o.codecPrivateData.equals(codecPrivateData)) return false;
        if(o.chunkDurations == null) {
            if(chunkDurations != null) return false;
        } else if(!o.chunkDurations.equals(chunkDurations)) return false;
        if(o.dashHeaderSize != dashHeaderSize) return false;
        if(o.dashMediaStartByteOffset != dashMediaStartByteOffset) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (downloadableId ^ (downloadableId >>> 32));
        hashCode = hashCode * 31 + (codecPrivateData == null ? 1237 : codecPrivateData.hashCode());
        hashCode = hashCode * 31 + (chunkDurations == null ? 1237 : chunkDurations.hashCode());
        hashCode = hashCode * 31 + (int) (dashHeaderSize ^ (dashHeaderSize >>> 32));
        hashCode = hashCode * 31 + (int) (dashMediaStartByteOffset ^ (dashMediaStartByteOffset >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("FileEncodingData{");
        builder.append("downloadableId=").append(downloadableId);
        builder.append(",codecPrivateData=").append(codecPrivateData);
        builder.append(",chunkDurations=").append(chunkDurations);
        builder.append(",dashHeaderSize=").append(dashHeaderSize);
        builder.append(",dashMediaStartByteOffset=").append(dashMediaStartByteOffset);
        builder.append("}");
        return builder.toString();
    }

    public FileEncodingData clone() {
        try {
            FileEncodingData clone = (FileEncodingData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}