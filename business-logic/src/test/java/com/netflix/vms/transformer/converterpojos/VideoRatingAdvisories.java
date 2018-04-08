package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoRatingAdvisories")
public class VideoRatingAdvisories implements Cloneable {

    public boolean ordered = false;
    public boolean imageOnly = false;
    @HollowTypeName(name="VideoRatingAdvisoryIdList")
    public List<VideoRatingAdvisoryId> ids = null;

    public VideoRatingAdvisories setOrdered(boolean ordered) {
        this.ordered = ordered;
        return this;
    }
    public VideoRatingAdvisories setImageOnly(boolean imageOnly) {
        this.imageOnly = imageOnly;
        return this;
    }
    public VideoRatingAdvisories setIds(List<VideoRatingAdvisoryId> ids) {
        this.ids = ids;
        return this;
    }
    public VideoRatingAdvisories addToIds(VideoRatingAdvisoryId videoRatingAdvisoryId) {
        if (this.ids == null) {
            this.ids = new ArrayList<VideoRatingAdvisoryId>();
        }
        this.ids.add(videoRatingAdvisoryId);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoRatingAdvisories))
            return false;

        VideoRatingAdvisories o = (VideoRatingAdvisories) other;
        if(o.ordered != ordered) return false;
        if(o.imageOnly != imageOnly) return false;
        if(o.ids == null) {
            if(ids != null) return false;
        } else if(!o.ids.equals(ids)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (ordered? 1231 : 1237);
        hashCode = hashCode * 31 + (imageOnly? 1231 : 1237);
        hashCode = hashCode * 31 + (ids == null ? 1237 : ids.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoRatingAdvisories{");
        builder.append("ordered=").append(ordered);
        builder.append(",imageOnly=").append(imageOnly);
        builder.append(",ids=").append(ids);
        builder.append("}");
        return builder.toString();
    }

    public VideoRatingAdvisories clone() {
        try {
            VideoRatingAdvisories clone = (VideoRatingAdvisories)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}