package com.netflix.vms.transformer.namedlist;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowoutput.Video;

public class VideoOrdinalTracker {

    private final HollowObjectMapper objectMapper;
    private final Video[] videos;

    public VideoOrdinalTracker(HollowObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.videos = new Video[10000000];  ////TODO: The number of unique Video IDs cannot grow beyond 10 million with this.
    }

    public int getVideoOrdinal(Video video) {
        int ordinal = objectMapper.addObject(video);
        videos[ordinal] = video;

        return ordinal;
    }

}
