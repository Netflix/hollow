package com.netflix.vms.transformer.namedlist;

import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.modules.TransformModule;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NamedListCompletionModule implements TransformModule {

    private final VideoNamedListModule videoNamedLists;
    private final CycleConstants cycleConstants;
    private final HollowObjectMapper objectMapper;
    
    public NamedListCompletionModule(VideoNamedListModule videoNamedLists, CycleConstants cycleConstants, HollowObjectMapper objectMapper) {
        this.videoNamedLists = videoNamedLists;
        this.cycleConstants = cycleConstants;
        this.objectMapper = objectMapper;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void transform() throws Exception {

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "transform");

        VideoOrdinalTracker videoOrdinalTracker = videoNamedLists.getVideoOrdinalTracker();
        
        for(Map.Entry<String, ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet>> countryEntry : videoNamedLists.getVideoListsByCountryAndName().entrySet()) {
            String country = countryEntry.getKey();

            executor.execute(() -> {

                NamedCollectionHolder holder = new NamedCollectionHolder();
                holder.country = cycleConstants.getISOCountry(country);

                ///// VIDEO LISTS
                holder.videoListMap = new HashMap<Strings, Set<Video>>();

                for(VideoNamedListType listName : VideoNamedListType.values()) {
                    Set<Video> videoSet = Collections.emptySet();
                    ThreadSafeBitSet list = countryEntry.getValue().get(listName);
                    
                    if(list != null) {
                    	videoSet = new HashSet<>();
	                    int videoOrdinal = list.nextSetBit(0);
	                    while(videoOrdinal != -1) {
	                        Video v = videoOrdinalTracker.getVideoForOrdinal(videoOrdinal);
	                        videoSet.add(v);
	
	                        videoOrdinal = list.nextSetBit(videoOrdinal + 1);
	                    }
                    } else {
                    	videoSet = Collections.emptySet();
                    }
                    
                    holder.videoListMap.put(new Strings(listName.toString()), videoSet);
                }

                objectMapper.add(holder);
            });

        }

        executor.awaitSuccessfulCompletion();

    }






}
