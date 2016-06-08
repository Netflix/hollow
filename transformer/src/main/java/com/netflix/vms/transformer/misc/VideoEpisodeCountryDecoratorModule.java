package com.netflix.vms.transformer.misc;

import java.util.Set;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.VideoCollectionsData;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode_CountryList;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;

public class VideoEpisodeCountryDecoratorModule {
    protected final VMSHollowInputAPI api;
    protected final HollowObjectMapper mapper;
    
    public VideoEpisodeCountryDecoratorModule(VMSHollowInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public void decorateVideoEpisodes(String country, Set<VideoCollectionsDataHierarchy> hierarchies) {
        for(VideoCollectionsDataHierarchy hierarchy : hierarchies) {
            VideoCollectionsData topNodeVcd = hierarchy.getTopNode();
            for(VideoEpisode episode : topNodeVcd.videoEpisodes) {
                VideoEpisode_CountryList videoEpisodeDecorator = new VideoEpisode_CountryList();
                videoEpisodeDecorator.country = new ISOCountry(country);
                videoEpisodeDecorator.item = episode;
                mapper.addObject(videoEpisodeDecorator);
            }
        }
    }
}
