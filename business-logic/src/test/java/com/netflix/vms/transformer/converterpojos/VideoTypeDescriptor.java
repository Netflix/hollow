package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoTypeDescriptor")
public class VideoTypeDescriptor implements Cloneable {

    public String countryCode = null;
    public String copyright = null;
    public String tierType = null;
    public boolean original = false;
    @HollowTypeName(name="VideoTypeMediaList")
    public List<VideoTypeMedia> media = null;
    public boolean extended = false;

    public VideoTypeDescriptor setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public VideoTypeDescriptor setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }
    public VideoTypeDescriptor setTierType(String tierType) {
        this.tierType = tierType;
        return this;
    }
    public VideoTypeDescriptor setOriginal(boolean original) {
        this.original = original;
        return this;
    }
    public VideoTypeDescriptor setMedia(List<VideoTypeMedia> media) {
        this.media = media;
        return this;
    }
    public VideoTypeDescriptor setExtended(boolean extended) {
        this.extended = extended;
        return this;
    }
    public VideoTypeDescriptor addToMedia(VideoTypeMedia videoTypeMedia) {
        if (this.media == null) {
            this.media = new ArrayList<VideoTypeMedia>();
        }
        this.media.add(videoTypeMedia);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoTypeDescriptor))
            return false;

        VideoTypeDescriptor o = (VideoTypeDescriptor) other;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.copyright == null) {
            if(copyright != null) return false;
        } else if(!o.copyright.equals(copyright)) return false;
        if(o.tierType == null) {
            if(tierType != null) return false;
        } else if(!o.tierType.equals(tierType)) return false;
        if(o.original != original) return false;
        if(o.media == null) {
            if(media != null) return false;
        } else if(!o.media.equals(media)) return false;
        if(o.extended != extended) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (copyright == null ? 1237 : copyright.hashCode());
        hashCode = hashCode * 31 + (tierType == null ? 1237 : tierType.hashCode());
        hashCode = hashCode * 31 + (original? 1231 : 1237);
        hashCode = hashCode * 31 + (media == null ? 1237 : media.hashCode());
        hashCode = hashCode * 31 + (extended? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoTypeDescriptor{");
        builder.append("countryCode=").append(countryCode);
        builder.append(",copyright=").append(copyright);
        builder.append(",tierType=").append(tierType);
        builder.append(",original=").append(original);
        builder.append(",media=").append(media);
        builder.append(",extended=").append(extended);
        builder.append("}");
        return builder.toString();
    }

    public VideoTypeDescriptor clone() {
        try {
            VideoTypeDescriptor clone = (VideoTypeDescriptor)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}