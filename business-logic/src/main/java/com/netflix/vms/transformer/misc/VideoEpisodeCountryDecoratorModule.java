package com.netflix.vms.transformer.misc;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode_CountryList;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;
import java.util.Set;

public class VideoEpisodeCountryDecoratorModule {
    protected final VMSHollowInputAPI api;
    protected final CycleConstants cycleConstants;
    protected final HollowObjectMapper mapper;
    
    public VideoEpisodeCountryDecoratorModule(VMSHollowInputAPI api, CycleConstants cycleConstants, HollowObjectMapper mapper) {
        this.api = api;
        this.cycleConstants = cycleConstants;
        this.mapper = mapper;
    }

    public void decorateVideoEpisodes(String country, Set<VideoCollectionsDataHierarchy> hierarchies) {
        for(VideoCollectionsDataHierarchy hierarchy : hierarchies) {
            VideoCollectionsData topNodeVcd = hierarchy.getTopNode();
            for(VideoEpisode episode : topNodeVcd.videoEpisodes) {
                VideoEpisode_CountryList videoEpisodeDecorator = new VideoEpisode_CountryList();
                videoEpisodeDecorator.country = cycleConstants.getISOCountry(country);
                videoEpisodeDecorator.item = episode;
                mapper.addObject(videoEpisodeDecorator);
            }
        }
    }
}
