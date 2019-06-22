package com.netflix.vms.transformer.util;

import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;
import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.ListOfRightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import java.util.HashSet;
import java.util.Set;

public class VideoSetTypeUtil {

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer, Gatekeeper2Dataset gk2Dataset) {
        return computeSetTypes(videoId, countryCode, null, null, api, ctx, constants, indexer, gk2Dataset);
    }

    public static Set<VideoSetType> computeSetTypes(long videoId, String countryCode, Status rights, VideoTypeDescriptorHollow typeDescriptor, VMSHollowInputAPI api, TransformerContext ctx, CycleConstants constants, VMSTransformerIndexer indexer, Gatekeeper2Dataset gk2Dataset) {
        boolean isInWindow = false;
        boolean isInFuture = false;
        boolean isExtended = false;

        if (rights == null) {
            rights = gk2Dataset.getStatus(videoId, countryCode);
        }
        if (rights != null) {
            ListOfRightsWindow windows = rights.getRights().getWindows();
            for (RightsWindow window : windows) {
                long windowStart = window.getStartDate();
                long windowEnd = window.getEndDate();
                if(window.getOnHold()) {
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