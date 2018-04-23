package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoAward")
public class VideoAward implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="VideoAwardList")
    public List<VideoAwardMapping> award = null;

    public VideoAward setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public VideoAward setAward(List<VideoAwardMapping> award) {
        this.award = award;
        return this;
    }
    public VideoAward addToAward(VideoAwardMapping videoAwardMapping) {
        if (this.award == null) {
            this.award = new ArrayList<VideoAwardMapping>();
        }
        this.award.add(videoAwardMapping);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAward))
            return false;

        VideoAward o = (VideoAward) other;
        if(o.videoId != videoId) return false;
        if(o.award == null) {
            if(award != null) return false;
        } else if(!o.award.equals(award)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (award == null ? 1237 : award.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoAward{");
        builder.append("videoId=").append(videoId);
        builder.append(",award=").append(award);
        builder.append("}");
        return builder.toString();
    }

    public VideoAward clone() {
        try {
            VideoAward clone = (VideoAward)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}