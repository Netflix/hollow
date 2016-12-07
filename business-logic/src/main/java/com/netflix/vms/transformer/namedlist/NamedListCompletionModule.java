package com.netflix.vms.transformer.namedlist;


import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.NFResourceID;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.PersonRole;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.modules.TransformModule;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NamedListCompletionModule implements TransformModule {

    private final VideoNamedListModule videoNamedLists;
    private final List<GlobalPerson> persons;
    private final CycleConstants cycleConstants;
    private final HollowObjectMapper objectMapper;
    
    public NamedListCompletionModule(VideoNamedListModule videoNamedLists, List<GlobalPerson> persons, CycleConstants cycleConstants, HollowObjectMapper objectMapper) {
        this.videoNamedLists = videoNamedLists;
        this.persons = persons;
        this.cycleConstants = cycleConstants;
        this.objectMapper = objectMapper;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void transform() throws Exception {

        SimultaneousExecutor executor = new SimultaneousExecutor();

        VideoOrdinalTracker videoOrdinalTracker = videoNamedLists.getVideoOrdinalTracker();
        videoOrdinalTracker.prepareEpisodes();
        
        Map<Integer, Integer> videoIdToOrdinalMap = videoOrdinalTracker.getVideoIdToOrdinalMap();

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

                /// VALID_EPISODES
                holder.episodeListMap = new HashMap<Strings, Set<Episode>>();

                ThreadSafeBitSet episodeList = videoNamedLists.getEpisodeListForCountry(country);
                Set<Episode> episodeSet = new HashSet<Episode>();

                int episodeOrdinal = episodeList.nextSetBit(0);
                while(episodeOrdinal != -1) {
                    Episode ep = videoOrdinalTracker.getEpisodeForOrdinal(episodeOrdinal);
                    episodeSet.add(ep);

                    episodeOrdinal = episodeList.nextSetBit(episodeOrdinal + 1);
                }

                holder.episodeListMap.put(new Strings("VALID_EPISODES"), episodeSet);


                //// VALID_PERSONS
                holder.personListMap = new HashMap<Strings, Set<VPerson>>();
                ThreadSafeBitSet validVideosForCountry = countryEntry.getValue().get(VideoNamedListType.VALID_VIDEOS);
                Set<VPerson> personSet = new HashSet<VPerson>();

                for(GlobalPerson person : persons) {
                    for(PersonRole personRole : person.personRoles) {
                        Integer videoOrdinal = videoIdToOrdinalMap.get(personRole.video.value);
                        if (videoOrdinal==null) continue;

                        if(validVideosForCountry.get(videoOrdinal)) {
                            personSet.add(personRole.person);
                            break;
                        }
                    }
                }

                holder.personListMap.put(new Strings("VALID_PERSONS"), personSet);

                holder.resourceIdListMap = Collections.emptyMap();

                objectMapper.addObject(holder);
            });

        }

        objectMapper.addObject(new NFResourceID("invalid"));

        executor.awaitSuccessfulCompletion();

    }






}
