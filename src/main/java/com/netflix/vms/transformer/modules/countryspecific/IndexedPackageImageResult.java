package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;

import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import java.util.HashMap;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import java.util.List;
import java.util.Map;

public class IndexedPackageImageResult {

    public Map<VideoMoment, List<ImageDownloadable>> videoMomentToDownloadableListMap = new HashMap<VideoMoment, List<ImageDownloadable>>();
    public Map<TrickPlayType, TrickPlayItem> trickPlayItemMap = new HashMap<TrickPlayType, TrickPlayItem>();
}
