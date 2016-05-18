package com.netflix.vms.transformer.util;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_RIGHTS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.vms.transformer.CycleConstants;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.Set;

public class VideoSetTypeUtil {

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        return computeSetTypes(videoId, countryCode, null, null, api, ctx, constants, indexer);
    }

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, VideoRightsHollow rights, VideoTypeDescriptorHollow typeDescriptor, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer) {
        boolean isInWindow = false;
        boolean isInFuture = false;
        boolean isExtended = false;

        if (rights == null) {
            HollowPrimaryKeyIndex videoRightsIdx = indexer.getPrimaryKeyIndex(VIDEO_RIGHTS);
            int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId, countryCode);
            if (rightsOrdinal != -1) {
                rights = api.getVideoRightsHollow(rightsOrdinal);
            }
        }
        if (rights != null) {
            Set<VideoRightsWindowHollow> windows = rights._getRights()._getWindows();
            for (VideoRightsWindowHollow window : windows) {
                long windowStart = window._getStartDate()._getValue();
                if (windowStart < ctx.getNowMillis() && window._getEndDate()._getValue() > ctx.getNowMillis()) {
                    isInWindow = true;
                    break;
                } else if (windowStart > ctx.getNowMillis()) {
                    isInFuture = true;
                }
            }
        }

        if (typeDescriptor == null) {
            HollowHashIndex videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
            HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches(videoId, countryCode);
            if (videoTypeMatches != null) {
                typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());
            }
        }

        if (typeDescriptor != null) {
            isExtended = "US".equals(countryCode) && typeDescriptor._getExtended();
        }

        Set<VideoSetType> setOfVideoSetType = new HashSet<VideoSetType>();
        if (isInWindow) {
            setOfVideoSetType.add(constants.PRESENT);
        } else if (isInFuture) {
            setOfVideoSetType.add(constants.FUTURE);
        } else if (isExtended) {
            setOfVideoSetType.add(constants.EXTENDED);
        }

        if (setOfVideoSetType.isEmpty())
            setOfVideoSetType.add(constants.PAST);

        return setOfVideoSetType;
    }
}