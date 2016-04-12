package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;

import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageMomentData {

    public Map<Long, VideoMoment> downloadableIdsToVideoMoments = new HashMap<Long, VideoMoment>();
    public List<VideoMoment> phoneSnackMoments = new ArrayList<VideoMoment>();


    public Map<VideoMoment, List<ImageDownloadable>> videoMomentToDownloadableListMap = new HashMap<VideoMoment, List<ImageDownloadable>>();
    public Map<TrickPlayType, TrickPlayItem> trickPlayItemMap = new HashMap<TrickPlayType, TrickPlayItem>();
    public Map<Strings, List<VideoImage>> stillImagesMap;

}
