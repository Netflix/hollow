package com.netflix.vms.transformer.modules.packages;

import com.netflix.encodingtools.videoresolutiontypelibrary.VideoResolutionType;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.hollowinput.StreamProfileGroupsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfileIdHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoFormatDescriptorIdentifier {

    private final TransformerContext ctx;
    private final CycleConstants cycleConstants;

    @Deprecated
    private final Set<Integer> ultraHDEncodingProfileIds;
    @Deprecated
    private final Set<TargetResolution> aspectRatioVideoFormatIdentifiers;
    public VideoFormatDescriptorIdentifier(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        this.ctx = ctx;
        this.cycleConstants = cycleConstants;
        this.ultraHDEncodingProfileIds = getUltraHDEncodingProfileIds(api, indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE_GROUP));
        this.aspectRatioVideoFormatIdentifiers = getAspectRatioVideoFormatIdentifiers();

    }

    public VideoFormatDescriptor selectVideoFormatDescriptor(int height, int width) {
        boolean isInclude4K = false; // NOTE: TO BE PARITY, continue to exclude 4K for default code path - @TODO deprecate VideoFormat to encapsulate Video Resolution and introduce new mechanism to return VideoResolutionType
        return selectVideoFormatDescriptor(ctx.getCupLibrary(), cycleConstants, height, width, isInclude4K);
    }

    protected static VideoFormatDescriptor selectVideoFormatDescriptor(CupLibrary cupLibrary, CycleConstants cycleConstants, int height, int width, boolean isInclude4K) {
        if (height == Integer.MIN_VALUE) return cycleConstants.VIDEOFORMAT_UNKNOWN;

        VideoResolutionType videoResType = cupLibrary.getResolutionType(width, height);
        if (isInclude4K && videoResType.compareTo(cupLibrary.getResolutionType(VideoResolutionType.ID_QHD)) > 0) return cycleConstants.FOUR_K;

        if (videoResType.compareTo(cupLibrary.getResolutionType(VideoResolutionType.ID_FULL_HD)) > 0) return cycleConstants.ULTRA_HD;
        if (videoResType.compareTo(cupLibrary.getResolutionType(VideoResolutionType.ID_FULL_HD)) == 0) return cycleConstants.SUPER_HD;
        if (videoResType.compareTo(cupLibrary.getResolutionType(VideoResolutionType.ID_MIN_HD)) >= 0) return cycleConstants.HD;

        return cycleConstants.SD;
    }

    @Deprecated
    public VideoFormatDescriptor selectVideoFormatDescriptorOld(int encodingProfileId, int bitrate, int height, int width, int targetHeight, int targetWidth) {
        if (encodingProfileId == 214)
            return cycleConstants.SUPER_HD;

        if (ultraHDEncodingProfileIds.contains(Integer.valueOf(encodingProfileId)))
            return cycleConstants.ULTRA_HD;

        if (height == Integer.MIN_VALUE)
            return getUnknownVideoFormatDescriptor();

        if (targetHeight != Integer.MIN_VALUE && targetWidth != Integer.MIN_VALUE) {
            if (aspectRatioVideoFormatIdentifiers.contains(new TargetResolution(targetHeight, targetWidth))) {
                if (height > width)
                    return getUnknownVideoFormatDescriptor();

                float div = ((float) width / (float) height);
                final int index = (int) (div * 100) - 100;

                if (index <= 55) return cycleConstants.SD;

                return cycleConstants.HD;
            }
        }

        if (height <= 719)
            return cycleConstants.SD;

        return cycleConstants.HD;
    }

    @Deprecated
    public VideoFormatDescriptor getUnknownVideoFormatDescriptor() {
        return cycleConstants.VIDEOFORMAT_UNKNOWN;
    }

    @Deprecated
    private Set<Integer> getUltraHDEncodingProfileIds(VMSHollowInputAPI api, HollowPrimaryKeyIndex primaryKeyIndex) {
        Set<Integer> ultraHDEncodingProfiles = new HashSet<Integer>();

        int ordinal = primaryKeyIndex.getMatchingOrdinal("4K");
        if (ordinal != -1) {
            StreamProfileGroupsHollow group = api.getStreamProfileGroupsHollow(ordinal);
            List<StreamProfileIdHollow> idList = group._getStreamProfileIds();
            for (StreamProfileIdHollow id : idList) {
                ultraHDEncodingProfiles.add(Integer.valueOf((int) id._getValue()));
            }
        }

        return ultraHDEncodingProfiles;
    }

    ///TODO: This seems wrong.  What is the point of checking for these specific target resolutions?
    @Deprecated
    private Set<TargetResolution> getAspectRatioVideoFormatIdentifiers() {
        Set<TargetResolution> set = new HashSet<TargetResolution>();

        set.add(new TargetResolution(0, 0));
        set.add(new TargetResolution(1080, 0));
        set.add(new TargetResolution(720, 0));
        set.add(new TargetResolution(612, 0));
        set.add(new TargetResolution(480, 0));
        set.add(new TargetResolution(0, 1536));
        set.add(new TargetResolution(0, 912));
        set.add(new TargetResolution(0, 567));
        set.add(new TargetResolution(0, 350));
        set.add(new TargetResolution(0, 342));
        set.add(new TargetResolution(0, 300));
        set.add(new TargetResolution(0, 260));
        set.add(new TargetResolution(0, 240));
        set.add(new TargetResolution(0, 233));
        set.add(new TargetResolution(0, 180));
        set.add(new TargetResolution(0, 171));
        set.add(new TargetResolution(0, 228));
        set.add(new TargetResolution(0, 114));
        set.add(new TargetResolution(159, 0));
        set.add(new TargetResolution(185, 0));
        set.add(new TargetResolution(370, 0));
        set.add(new TargetResolution(450, 0));
        set.add(new TargetResolution(900, 0));

        return set;
    }
}
