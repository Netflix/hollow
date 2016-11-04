package com.netflix.vms.transformer.modules.countryspecific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;

public class VideoMomentModule {

    public final static String START_MOMENT_KEY = "Start";
    public final static String END_MOMENT_KEY = "Ending";

    /// extract to class
    public VideoMoment createVideoMoment(int packageId, PackageMomentHollow packageMoment, String momentType) {
        VideoMoment videoMoment = new VideoMoment();
        videoMoment.bifIndex = packageMoment._getBifIndex();
        videoMoment.msOffset = packageMoment._getOffsetMillis();
        videoMoment.packageId = packageId;
        videoMoment.runtimeMs = packageMoment._getClipSpecRuntimeMillis();
        videoMoment.sequenceNumber = (int) packageMoment._getMomentSeqNumber();
        videoMoment.videoMomentTypeName = new Strings(momentType);
        
        StringHollow packageMomentTags = packageMoment._getTags();
        List<String> momentTags = new ArrayList<String>();
        if(packageMomentTags != null) {
            String tags = packageMomentTags._getValue();
            if(!"".equals(tags)) {
                videoMoment.momentTags = new ArrayList<Strings>();
                for(String tag : tags.split(",")) {
                    momentTags.add(tag);
                }
            }
        }

        if(momentTags.isEmpty()) {
            videoMoment.momentTags = Collections.emptyList();
        } else {
            videoMoment.momentTags = new ArrayList<Strings>();
            Collections.sort(momentTags);
            for(String tag : momentTags) {
                videoMoment.momentTags.add(new Strings(tag));
            }
        }                
        
        return videoMoment;
    }

}
