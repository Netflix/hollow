package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

/**
 * Adding attributes that are specific to Video of type show. An instance of this class is used in VideoCollectionsData.
 */
public class VideoShow {

    public Boolean hideSeasonNumbers = false;
    public Boolean episodicNewBadge = false;
    public @HollowInline String merchOrder = "regular";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoShow)) return false;

        VideoShow videoShow = (VideoShow) o;

        if (!hideSeasonNumbers.equals(videoShow.hideSeasonNumbers)) return false;
        if (!episodicNewBadge.equals(videoShow.episodicNewBadge)) return false;
        return merchOrder.equals(videoShow.merchOrder);
    }

    @Override
    public int hashCode() {
        int result = hideSeasonNumbers.hashCode();
        result = 31 * result + episodicNewBadge.hashCode();
        result = 31 * result + merchOrder.hashCode();
        return result;
    }
}
