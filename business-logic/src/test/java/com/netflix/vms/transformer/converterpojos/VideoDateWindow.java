package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="VideoDateWindow")
public class VideoDateWindow implements Cloneable {

    public String countryCode = null;
    @HollowTypeName(name="ListOfReleaseDates")
    public List<ReleaseDate> releaseDates = null;

    public VideoDateWindow setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    public VideoDateWindow setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
        return this;
    }
    public VideoDateWindow addToReleaseDates(ReleaseDate releaseDate) {
        if (this.releaseDates == null) {
            this.releaseDates = new ArrayList<ReleaseDate>();
        }
        this.releaseDates.add(releaseDate);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoDateWindow))
            return false;

        VideoDateWindow o = (VideoDateWindow) other;
        if(o.countryCode == null) {
            if(countryCode != null) return false;
        } else if(!o.countryCode.equals(countryCode)) return false;
        if(o.releaseDates == null) {
            if(releaseDates != null) return false;
        } else if(!o.releaseDates.equals(releaseDates)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (countryCode == null ? 1237 : countryCode.hashCode());
        hashCode = hashCode * 31 + (releaseDates == null ? 1237 : releaseDates.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoDateWindow{");
        builder.append("countryCode=").append(countryCode);
        builder.append(",releaseDates=").append(releaseDates);
        builder.append("}");
        return builder.toString();
    }

    public VideoDateWindow clone() {
        try {
            VideoDateWindow clone = (VideoDateWindow)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}