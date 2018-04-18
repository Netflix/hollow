package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamNonImageInfo")
public class StreamNonImageInfo implements Cloneable {

    public long runtimeSeconds = java.lang.Long.MIN_VALUE;
    public StreamDrmInfo drmInfo = null;
    public ChunkDurationsString chunkDurations = null;
    public CodecPrivateDataString codecPrivateData = null;
    public VideoStreamInfo videoInfo = null;
    public TextStreamInfo textInfo = null;
    public AudioStreamInfo audioInfo = null;

    public StreamNonImageInfo setRuntimeSeconds(long runtimeSeconds) {
        this.runtimeSeconds = runtimeSeconds;
        return this;
    }
    public StreamNonImageInfo setDrmInfo(StreamDrmInfo drmInfo) {
        this.drmInfo = drmInfo;
        return this;
    }
    public StreamNonImageInfo setChunkDurations(ChunkDurationsString chunkDurations) {
        this.chunkDurations = chunkDurations;
        return this;
    }
    public StreamNonImageInfo setCodecPrivateData(CodecPrivateDataString codecPrivateData) {
        this.codecPrivateData = codecPrivateData;
        return this;
    }
    public StreamNonImageInfo setVideoInfo(VideoStreamInfo videoInfo) {
        this.videoInfo = videoInfo;
        return this;
    }
    public StreamNonImageInfo setTextInfo(TextStreamInfo textInfo) {
        this.textInfo = textInfo;
        return this;
    }
    public StreamNonImageInfo setAudioInfo(AudioStreamInfo audioInfo) {
        this.audioInfo = audioInfo;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamNonImageInfo))
            return false;

        StreamNonImageInfo o = (StreamNonImageInfo) other;
        if(o.runtimeSeconds != runtimeSeconds) return false;
        if(o.drmInfo == null) {
            if(drmInfo != null) return false;
        } else if(!o.drmInfo.equals(drmInfo)) return false;
        if(o.chunkDurations == null) {
            if(chunkDurations != null) return false;
        } else if(!o.chunkDurations.equals(chunkDurations)) return false;
        if(o.codecPrivateData == null) {
            if(codecPrivateData != null) return false;
        } else if(!o.codecPrivateData.equals(codecPrivateData)) return false;
        if(o.videoInfo == null) {
            if(videoInfo != null) return false;
        } else if(!o.videoInfo.equals(videoInfo)) return false;
        if(o.textInfo == null) {
            if(textInfo != null) return false;
        } else if(!o.textInfo.equals(textInfo)) return false;
        if(o.audioInfo == null) {
            if(audioInfo != null) return false;
        } else if(!o.audioInfo.equals(audioInfo)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (runtimeSeconds ^ (runtimeSeconds >>> 32));
        hashCode = hashCode * 31 + (drmInfo == null ? 1237 : drmInfo.hashCode());
        hashCode = hashCode * 31 + (chunkDurations == null ? 1237 : chunkDurations.hashCode());
        hashCode = hashCode * 31 + (codecPrivateData == null ? 1237 : codecPrivateData.hashCode());
        hashCode = hashCode * 31 + (videoInfo == null ? 1237 : videoInfo.hashCode());
        hashCode = hashCode * 31 + (textInfo == null ? 1237 : textInfo.hashCode());
        hashCode = hashCode * 31 + (audioInfo == null ? 1237 : audioInfo.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamNonImageInfo{");
        builder.append("runtimeSeconds=").append(runtimeSeconds);
        builder.append(",drmInfo=").append(drmInfo);
        builder.append(",chunkDurations=").append(chunkDurations);
        builder.append(",codecPrivateData=").append(codecPrivateData);
        builder.append(",videoInfo=").append(videoInfo);
        builder.append(",textInfo=").append(textInfo);
        builder.append(",audioInfo=").append(audioInfo);
        builder.append("}");
        return builder.toString();
    }

    public StreamNonImageInfo clone() {
        try {
            StreamNonImageInfo clone = (StreamNonImageInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}