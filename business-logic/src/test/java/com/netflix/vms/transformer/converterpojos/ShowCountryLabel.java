package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ShowCountryLabel")
public class ShowCountryLabel implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="ShowMemberTypeList")
    public List<ShowMemberType> showMemberTypes = null;

    public ShowCountryLabel setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public ShowCountryLabel setShowMemberTypes(List<ShowMemberType> showMemberTypes) {
        this.showMemberTypes = showMemberTypes;
        return this;
    }
    public ShowCountryLabel addToShowMemberTypes(ShowMemberType showMemberType) {
        if (this.showMemberTypes == null) {
            this.showMemberTypes = new ArrayList<ShowMemberType>();
        }
        this.showMemberTypes.add(showMemberType);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ShowCountryLabel))
            return false;

        ShowCountryLabel o = (ShowCountryLabel) other;
        if(o.videoId != videoId) return false;
        if(o.showMemberTypes == null) {
            if(showMemberTypes != null) return false;
        } else if(!o.showMemberTypes.equals(showMemberTypes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (showMemberTypes == null ? 1237 : showMemberTypes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ShowCountryLabel{");
        builder.append("videoId=").append(videoId);
        builder.append(",showMemberTypes=").append(showMemberTypes);
        builder.append("}");
        return builder.toString();
    }

    public ShowCountryLabel clone() {
        try {
            ShowCountryLabel clone = (ShowCountryLabel)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}