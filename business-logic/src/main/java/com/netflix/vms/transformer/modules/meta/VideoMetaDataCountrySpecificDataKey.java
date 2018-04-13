package com.netflix.vms.transformer.modules.meta;

import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import java.util.Set;

public class VideoMetaDataCountrySpecificDataKey {

    boolean isSearchOnly;
    boolean isTheatricalRelease;
    Date theatricalReleaseDate;
    Date broadcastReleaseDate;
    int broadcastYear;
    Strings broadcastDistributorName;
    int year;
    int latestYear;
    Set<VideoSetType> videoSetTypes;
    int showMemberTypeId = Integer.MIN_VALUE;
    Strings copyright;
    boolean hasNewContent;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((broadcastDistributorName == null) ? 0 : broadcastDistributorName.hashCode());
        result = prime * result + ((broadcastReleaseDate == null) ? 0 : broadcastReleaseDate.hashCode());
        result = prime * result + broadcastYear;
        result = prime * result + ((copyright == null) ? 0 : copyright.hashCode());
        result = prime * result + (hasNewContent ? 1231 : 1237);
        result = prime * result + (isSearchOnly ? 1231 : 1237);
        result = prime * result + (isTheatricalRelease ? 1231 : 1237);
        result = prime * result + latestYear;
        result = prime * result + showMemberTypeId;
        result = prime * result + ((theatricalReleaseDate == null) ? 0 : theatricalReleaseDate.hashCode());
        result = prime * result + ((videoSetTypes == null) ? 0 : videoSetTypes.hashCode());
        result = prime * result + year;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoMetaDataCountrySpecificDataKey other = (VideoMetaDataCountrySpecificDataKey) obj;
        if (broadcastDistributorName == null) {
            if (other.broadcastDistributorName != null)
                return false;
        } else if (!broadcastDistributorName.equals(other.broadcastDistributorName))
            return false;
        if (broadcastReleaseDate == null) {
            if (other.broadcastReleaseDate != null)
                return false;
        } else if (!broadcastReleaseDate.equals(other.broadcastReleaseDate))
            return false;
        if (broadcastYear != other.broadcastYear)
            return false;
        if (copyright == null) {
            if (other.copyright != null)
                return false;
        } else if (!copyright.equals(other.copyright))
            return false;
        if (hasNewContent != other.hasNewContent)
            return false;
        if (isSearchOnly != other.isSearchOnly)
            return false;
        if (isTheatricalRelease != other.isTheatricalRelease)
            return false;
        if (latestYear != other.latestYear)
            return false;
        if (showMemberTypeId != other.showMemberTypeId)
            return false;
        if (theatricalReleaseDate == null) {
            if (other.theatricalReleaseDate != null)
                return false;
        } else if (!theatricalReleaseDate.equals(other.theatricalReleaseDate))
            return false;
        if (videoSetTypes == null) {
            if (other.videoSetTypes != null)
                return false;
        } else if (!videoSetTypes.equals(other.videoSetTypes))
            return false;
        if (year != other.year)
            return false;
        return true;
    }


}
