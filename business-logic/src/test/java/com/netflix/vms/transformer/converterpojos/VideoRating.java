package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoRating")
public class VideoRating implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="VideoRatingArrayOfRating")
    public List<VideoRatingRating> rating = null;

    public VideoRating setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public VideoRating setRating(List<VideoRatingRating> rating) {
        this.rating = rating;
        return this;
    }
    public VideoRating addToRating(VideoRatingRating videoRatingRating) {
        if (this.rating == null) {
            this.rating = new ArrayList<VideoRatingRating>();
        }
        this.rating.add(videoRatingRating);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoRating))
            return false;

        VideoRating o = (VideoRating) other;
        if(o.videoId != videoId) return false;
        if(o.rating == null) {
            if(rating != null) return false;
        } else if(!o.rating.equals(rating)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (rating == null ? 1237 : rating.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoRating{");
        builder.append("videoId=").append(videoId);
        builder.append(",rating=").append(rating);
        builder.append("}");
        return builder.toString();
    }

    public VideoRating clone() {
        try {
            VideoRating clone = (VideoRating)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}