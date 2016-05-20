package com.netflix.vms.transformer.namedlist;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.hollow.bitsandbytes.ThreadSafeBitSet;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFResourceID;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.PersonRole;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.modules.TransformModule;

public class NamedListCompletionModule implements TransformModule {

    private final VideoNamedListModule videoNamedLists;
    private final List<GlobalPerson> persons;
    private final HollowObjectMapper objectMapper;

    public NamedListCompletionModule(VideoNamedListModule videoNamedLists, List<GlobalPerson> persons, HollowObjectMapper objectMapper) {
        this.videoNamedLists = videoNamedLists;
        this.persons = persons;
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
                holder.country = new ISOCountry(country);

                ///// VIDEO LISTS
                holder.videoListMap = new HashMap<Strings, Set<Video>>();

                for(Map.Entry<VideoNamedListType, ThreadSafeBitSet> listEntry : countryEntry.getValue().entrySet()) {
                    Set<Video> videoSet = new HashSet<Video>();
                    VideoNamedListType listName = listEntry.getKey();
                    ThreadSafeBitSet list = listEntry.getValue();

                    int videoOrdinal = list.nextSetBit(0);
                    while(videoOrdinal != -1) {
                        Video v = videoOrdinalTracker.getVideoForOrdinal(videoOrdinal);
                        videoSet.add(v);

                        videoOrdinal = list.nextSetBit(videoOrdinal + 1);
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
