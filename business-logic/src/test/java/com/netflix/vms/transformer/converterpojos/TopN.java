package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="TopN")
public class TopN implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="TopNAttributesSet")
    public Set<TopNAttribute> attributes = null;

    public TopN setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public TopN setAttributes(Set<TopNAttribute> attributes) {
        this.attributes = attributes;
        return this;
    }
    public TopN addToAttributes(TopNAttribute topNAttribute) {
        if (this.attributes == null) {
            this.attributes = new HashSet<TopNAttribute>();
        }
        this.attributes.add(topNAttribute);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TopN))
            return false;

        TopN o = (TopN) other;
        if(o.videoId != videoId) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (attributes == null ? 1237 : attributes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TopN{");
        builder.append("videoId=").append(videoId);
        builder.append(",attributes=").append(attributes);
        builder.append("}");
        return builder.toString();
    }

    public TopN clone() {
        try {
            TopN clone = (TopN)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}