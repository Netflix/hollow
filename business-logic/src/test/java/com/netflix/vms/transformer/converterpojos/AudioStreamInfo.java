package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="AudioStreamInfo")
public class AudioStreamInfo implements Cloneable {

    public String audioLanguageCode = null;
    public int audioBitrateKBPS = java.lang.Integer.MIN_VALUE;

    public AudioStreamInfo setAudioLanguageCode(String audioLanguageCode) {
        this.audioLanguageCode = audioLanguageCode;
        return this;
    }
    public AudioStreamInfo setAudioBitrateKBPS(int audioBitrateKBPS) {
        this.audioBitrateKBPS = audioBitrateKBPS;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AudioStreamInfo))
            return false;

        AudioStreamInfo o = (AudioStreamInfo) other;
        if(o.audioLanguageCode == null) {
            if(audioLanguageCode != null) return false;
        } else if(!o.audioLanguageCode.equals(audioLanguageCode)) return false;
        if(o.audioBitrateKBPS != audioBitrateKBPS) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (audioLanguageCode == null ? 1237 : audioLanguageCode.hashCode());
        hashCode = hashCode * 31 + audioBitrateKBPS;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AudioStreamInfo{");
        builder.append("audioLanguageCode=").append(audioLanguageCode);
        builder.append(",audioBitrateKBPS=").append(audioBitrateKBPS);
        builder.append("}");
        return builder.toString();
    }

    public AudioStreamInfo clone() {
        try {
            AudioStreamInfo clone = (AudioStreamInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}