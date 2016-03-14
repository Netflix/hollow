package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.VideoMoment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMomentData {

    public Map<Long, VideoMoment> downloadableIdsToVideoMoments = new HashMap<Long, VideoMoment>();
    public List<VideoMoment> phoneSnackMoments = new ArrayList<VideoMoment>();
    
}
