package com.netflix.vms.transformer.namedlist;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.Video;
import java.util.HashMap;
import java.util.Map;

public class VideoOrdinalTracker {

    ////TODO: The number of unique Video IDs cannot grow beyond 10 million with this structure.
    private static final int MAX_UNIQUE_VIDEO_IDS = 10000000;

    private final HollowObjectMapper objectMapper;
    private final Video[] videos;
    private Episode[] episodes;

    public VideoOrdinalTracker(HollowObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.videos = new Video[MAX_UNIQUE_VIDEO_IDS];
    }

    public int getVideoOrdinal(Video video) {
        int ordinal = objectMapper.addObject(video);
        videos[ordinal] = video;

        return ordinal;
    }

    public int getMaxVideoOrdinal() {
        for(int i=videos.length-1;i>=0;i--) {
            if(videos[i] != null)
                return i;
        }
        return -1;
    }
    
    public Video getVideoForOrdinal(int videoOrdinal) {
        return videos[videoOrdinal];
    }

    public Episode getEpisodeForOrdinal(int episodeOrdinal) {
        return episodes[episodeOrdinal];
    }
    
    public Map<Integer, Integer> getVideoIdToOrdinalMap() {
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    	
    	for(int i=0;i<videos.length;i++) {
    		if(videos[i] != null) {
    			map.put(videos[i].value, i);
    		}
    	}
    	
    	return map;
    }

    public void prepareEpisodes() {
        this.episodes = new Episode[getMaxVideoOrdinal() + 1];

        for(int i=0;i<episodes.length;i++) {
            if(videos[i] != null)
                episodes[i] = new Episode(videos[i].value);
        }
    }

}
