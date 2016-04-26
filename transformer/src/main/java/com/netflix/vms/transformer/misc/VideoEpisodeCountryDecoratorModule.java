package com.netflix.vms.transformer.misc;

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

    public void decorateVideoEpisodes(String country, VideoCollectionsDataHierarchy videoCollectionsDataHierarchy) {
        VideoCollectionsData topNodeVcd = videoCollectionsDataHierarchy.getTopNode();
        for(VideoEpisode episode : topNodeVcd.videoEpisodes) {
            VideoEpisode_CountryList videoEpisodeDecorator = new VideoEpisode_CountryList();
            videoEpisodeDecorator.country = new ISOCountry(country);
            videoEpisodeDecorator.item = episode;
            mapper.addObject(videoEpisodeDecorator);
        }
    }
}
