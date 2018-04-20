package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ConsolidatedVideoRatings")
public class ConsolidatedVideoRatings implements Cloneable {

    @HollowTypeName(name="ConsolidatedVideoRatingList")
    public List<ConsolidatedVideoRating> ratings = null;
    public long videoId = java.lang.Long.MIN_VALUE;

    public ConsolidatedVideoRatings setRatings(List<ConsolidatedVideoRating> ratings) {
        this.ratings = ratings;
        return this;
    }
    public ConsolidatedVideoRatings setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public ConsolidatedVideoRatings addToRatings(ConsolidatedVideoRating consolidatedVideoRating) {
        if (this.ratings == null) {
            this.ratings = new ArrayList<ConsolidatedVideoRating>();
        }
        this.ratings.add(consolidatedVideoRating);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ConsolidatedVideoRatings))
            return false;

        ConsolidatedVideoRatings o = (ConsolidatedVideoRatings) other;
        if(o.ratings == null) {
            if(ratings != null) return false;
        } else if(!o.ratings.equals(ratings)) return false;
        if(o.videoId != videoId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (ratings == null ? 1237 : ratings.hashCode());
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ConsolidatedVideoRatings{");
        builder.append("ratings=").append(ratings);
        builder.append(",videoId=").append(videoId);
        builder.append("}");
        return builder.toString();
    }

    public ConsolidatedVideoRatings clone() {
        try {
            ConsolidatedVideoRatings clone = (ConsolidatedVideoRatings)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}