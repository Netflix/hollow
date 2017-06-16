package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowHashKey;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Set;

@HollowPrimaryKey(fields="downloadableId")
public class FileEncodingData implements Cloneable {

    public DownloadableId downloadableId = null;
    public CodecPrivateDataString codecPrivateData = null;
    public ChunkDurationsString chunkDurations = null;
    public long dashHeaderSize = java.lang.Long.MIN_VALUE;
    public long dashMediaStartByteOffset = java.lang.Long.MIN_VALUE;
    
    @HollowHashKey(fields="key")
    public Set<DashStreamBoxInfo> dashStreamBoxInfo = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof FileEncodingData))
            return false;

        FileEncodingData o = (FileEncodingData) other;
        if(o.downloadableId == null) {
            if(downloadableId != null) return false;
        } else if(!o.downloadableId.equals(downloadableId)) return false;
        if(o.codecPrivateData == null) {
            if(codecPrivateData != null) return false;
        } else if(!o.codecPrivateData.equals(codecPrivateData)) return false;
        if(o.chunkDurations == null) {
            if(chunkDurations != null) return false;
        } else if(!o.chunkDurations.equals(chunkDurations)) return false;
        if(o.dashHeaderSize != dashHeaderSize) return false;
        if(o.dashMediaStartByteOffset != dashMediaStartByteOffset) return false;
        if(o.dashStreamBoxInfo == null) {
            if(dashStreamBoxInfo != null) return false;
        } else if(!o.dashStreamBoxInfo.equals(dashStreamBoxInfo)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (downloadableId == null ? 1237 : downloadableId.hashCode());
        hashCode = hashCode * 31 + (codecPrivateData == null ? 1237 : codecPrivateData.hashCode());
        hashCode = hashCode * 31 + (chunkDurations == null ? 1237 : chunkDurations.hashCode());
        hashCode = hashCode * 31 + (int) (dashHeaderSize ^ (dashHeaderSize >>> 32));
        hashCode = hashCode * 31 + (int) (dashMediaStartByteOffset ^ (dashMediaStartByteOffset >>> 32));
        hashCode = hashCode * 31 + (dashStreamBoxInfo == null ? 1237 : dashStreamBoxInfo.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("FileEncodingData{");
        builder.append("downloadableId=").append(downloadableId);
        builder.append(",codecPrivateData=").append(codecPrivateData);
        builder.append(",chunkDurations=").append(chunkDurations);
        builder.append(",dashHeaderSize=").append(dashHeaderSize);
        builder.append(",dashMediaStartByteOffset=").append(dashMediaStartByteOffset);
        builder.append(",dashStreamBoxInfo=").append(dashStreamBoxInfo);
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
    private long __assigned_ordinal = -1;
}
