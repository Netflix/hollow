package com.netflix.vms.transformer.hollowoutput;


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