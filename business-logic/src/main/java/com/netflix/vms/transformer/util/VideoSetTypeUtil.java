package com.netflix.vms.transformer.util;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;
import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.Set;

public class VideoSetTypeUtil {

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer, GatekeeperStatusRetriever statusRetriever) {
        return computeSetTypes(videoId, countryCode, null, null, api, ctx, constants, indexer, statusRetriever);
    }

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, StatusHollow rights, VideoTypeDescriptorHollow typeDescriptor, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer, GatekeeperStatusRetriever statusRetriever) {
        boolean isInWindow = false;
        boolean isInFuture = false;
        boolean isExtended = false;

        if (rights == null) {
            rights = statusRetriever.getStatus(videoId, countryCode);
        }
        if (rights != null) {
            ListOfRightsWindowHollow windows = rights._getRights()._getWindows();
            for (RightsWindowHollow window : windows) {
                long windowStart = window._getStartDate();
                long windowEnd = window._getEndDate();
                if(window._getOnHold()) {
                    windowStart += ONE_THOUSAND_YEARS;
                    windowEnd += ONE_THOUSAND_YEARS;
                }
                if (windowStart < ctx.getNowMillis() && windowEnd > ctx.getNowMillis()) {
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