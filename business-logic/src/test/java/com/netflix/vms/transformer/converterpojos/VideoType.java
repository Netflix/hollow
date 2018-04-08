package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="VideoType")
public class VideoType implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="VideoTypeDescriptorSet")
    public Set<VideoTypeDescriptor> countryInfos = null;

    public VideoType setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public VideoType setCountryInfos(Set<VideoTypeDescriptor> countryInfos) {
        this.countryInfos = countryInfos;
        return this;
    }
    public VideoType addToCountryInfos(VideoTypeDescriptor videoTypeDescriptor) {
        if (this.countryInfos == null) {
            this.countryInfos = new HashSet<VideoTypeDescriptor>();
        }
        this.countryInfos.add(videoTypeDescriptor);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoType))
            return false;

        VideoType o = (VideoType) other;
        if(o.videoId != videoId) return false;
        if(o.countryInfos == null) {
            if(countryInfos != null) return false;
        } else if(!o.countryInfos.equals(countryInfos)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (countryInfos == null ? 1237 : countryInfos.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoType{");
        builder.append("videoId=").append(videoId);
        builder.append(",countryInfos=").append(countryInfos);
        builder.append("}");
        return builder.toString();
    }

    public VideoType clone() {
        try {
            VideoType clone = (VideoType)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}