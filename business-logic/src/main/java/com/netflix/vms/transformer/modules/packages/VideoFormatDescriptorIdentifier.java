package com.netflix.vms.transformer.modules.packages;

import com.netflix.vms.transformer.CycleConstants;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfileGroupsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfileIdHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoStreamInfoHollow;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoFormatDescriptorIdentifier {

    private final CycleConstants cycleConstants;
    private final Set<Integer> ultraHDEncodingProfileIds;
    private final Set<SuperHDIdentifier> validSuperHDIdentifiers;
    private final Set<TargetResolution> aspectRatioVideoFormatIdentifiers;


    public VideoFormatDescriptorIdentifier(VMSHollowInputAPI api, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        this.cycleConstants = cycleConstants;
        this.ultraHDEncodingProfileIds = getUltraHDEncodingProfileIds(api, indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE_GROUP));
        this.validSuperHDIdentifiers = getValidSuperHDIdentifiers();
        this.aspectRatioVideoFormatIdentifiers = getAspectRatioVideoFormatIdentifiers();

    }

    public VideoFormatDescriptor selectVideoFormatDescriptor(PackageStreamHollow stream) {
        StreamNonImageInfoHollow nonImageInfo = stream._getNonImageInfo();
        StreamDimensionsHollow dimensions = stream._getDimensions();

        if(dimensions != null && nonImageInfo != null) {
            VideoStreamInfoHollow videoInfo = nonImageInfo._getVideoInfo();

            if(videoInfo != null) {
                return selectVideoFormatDescriptor((int) stream._getStreamProfileId(),
                                                   videoInfo._getVideoBitrateKBPS(),
                                                   dimensions._getHeightInPixels(),
                                                   dimensions._getWidthInPixels(),
                                                   dimensions._getTargetHeightInPixels(),
                                                   dimensions._getTargetWidthInPixels());
            }
        }
        return null;
    }

    public VideoFormatDescriptor selectVideoFormatDescriptor(int encodingProfileId, int bitrate, int height, int width, int targetHeight, int targetWidth) {
        if(ultraHDEncodingProfileIds.contains(Integer.valueOf(encodingProfileId)))
            return cycleConstants.ULTRA_HD;

        if(height == Integer.MIN_VALUE)
            return getUnknownVideoFormatDescriptor();

        if(targetHeight != Integer.MIN_VALUE && targetWidth != Integer.MIN_VALUE) {
            if(aspectRatioVideoFormatIdentifiers.contains(new TargetResolution(targetHeight, targetWidth))) {
                if(height > width)
                    return getUnknownVideoFormatDescriptor();
                
                float div = ((float)width / (float)height);
                final int index = (int)(div * 100) - 100;

                if (index <= 55) return cycleConstants.SD;

                return cycleConstants.HD;
            }
        }

        if(height <= 719)
            return cycleConstants.SD;

        if(validSuperHDIdentifiers.contains(new SuperHDIdentifier(encodingProfileId, bitrate)))
            return cycleConstants.SUPER_HD;

        return cycleConstants.HD;
    }
    
    public VideoFormatDescriptor getUnknownVideoFormatDescriptor() {
        return cycleConstants.VIDEOFORMAT_UNKNOWN;
    }

    private Set<Integer> getUltraHDEncodingProfileIds(VMSHollowInputAPI api, HollowPrimaryKeyIndex primaryKeyIndex) {
        Set<Integer> ultraHDEncodingProfiles = new HashSet<Integer>();

        int ordinal = primaryKeyIndex.getMatchingOrdinal("CE4DASHVideo-4K");
        if(ordinal != -1) {
            StreamProfileGroupsHollow group = api.getStreamProfileGroupsHollow(ordinal);
            List<StreamProfileIdHollow>idList = group._getStreamProfileIds();
            for(StreamProfileIdHollow id : idList) {
                ultraHDEncodingProfiles.add(Integer.valueOf((int)id._getValue()));
            }
        }

        return ultraHDEncodingProfiles;
    }

    private Set<SuperHDIdentifier> getValidSuperHDIdentifiers() {
        Set<SuperHDIdentifier> set = new HashSet<SuperHDIdentifier>();

        set.add(new SuperHDIdentifier(214, 4300));
        set.add(new SuperHDIdentifier(214, 4302));
        set.add(new SuperHDIdentifier(67, 4300));
        set.add(new SuperHDIdentifier(214, 5800));
        set.add(new SuperHDIdentifier(214, 5802));
        set.add(new SuperHDIdentifier(67, 5800));

        return set;
    }

    ///TODO: This seems wrong.  What is the point of checking for these specific target resolutions?
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
