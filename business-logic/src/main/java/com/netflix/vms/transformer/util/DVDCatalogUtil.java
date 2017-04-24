package com.netflix.vms.transformer.util;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeMediaHollow;

public class DVDCatalogUtil {
    /**
     * Determine whether a video / country is in DVD catalog
     */
    public static boolean isVideoInDVDCatalog(VMSHollowInputAPI api, HollowHashIndex videoTypeCountryIndex, long videoId, String countryCode) {
        HollowHashIndexResult queryResult = videoTypeCountryIndex.findMatches(videoId, countryCode);

        int ordinal = queryResult.iterator().next();

        VideoTypeDescriptorHollow countryType = api.getVideoTypeDescriptorHollow(ordinal);
        if ("US".equals(countryCode) && countryType._getExtended())
            return true;

        for (VideoTypeMediaHollow media : countryType._getMedia()) {
            if (media._getValue()._isValueEqual("Plastic"))
                return true;
        }

        return false;
    }
}
