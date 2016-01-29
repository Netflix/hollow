package com.netflix.vms.hollowoutput.pojos;


public class FileEncodingData {

    public long downloadableId;
    public CodecPrivateDataString codecPrivateData;
    public ChunkDurationsString chunkDurations;
    public long dashHeaderSize;
    public long dashMediaStartByteOffset;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}