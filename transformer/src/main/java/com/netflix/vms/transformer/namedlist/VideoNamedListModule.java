package com.netflix.vms.transformer.namedlist;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;

import com.netflix.hollow.bitsandbytes.ThreadSafeBitSet;
import com.netflix.vms.transformer.hollowoutput.CompleteVideo;
import java.util.concurrent.ConcurrentHashMap;

public class VideoNamedListModule {



    private final VideoOrdinalTracker videoOrdinalTracker;
    private final ConcurrentHashMap<String, ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet>> videoListsByCountryAndName;

    public VideoNamedListModule(HollowObjectMapper objectMapper) {
        this.videoListsByCountryAndName = new ConcurrentHashMap<>();
        this.videoOrdinalTracker = new VideoOrdinalTracker(objectMapper);
    }

    public void convert(String country, CompleteVideo video) {

        int videoIdOrdinal = videoOrdinalTracker.getVideoOrdinal(video.id);

        ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> lists = videoListsByCountryAndName.get(country);

        addToList(lists, VideoNamedListType.VALID_VIDEOS, videoIdOrdinal);

        char nodeType[] = video.facetData.videoCollectionsData.nodeType.value;

    }

    private void addToList(ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> videoLists, VideoNamedListType type, int videoIdOrdinal) {
        ThreadSafeBitSet list = getNamedList(videoLists, type);
        list.set(videoIdOrdinal);
    }

    private ThreadSafeBitSet getNamedList(ConcurrentHashMap<VideoNamedListType, ThreadSafeBitSet> videoLists, VideoNamedListType type) {
        ThreadSafeBitSet list = videoLists.get(type);
        if(list == null) {
            list = new ThreadSafeBitSet();
            ThreadSafeBitSet existingList = videoLists.putIfAbsent(type, list);
            if(existingList != null)
                list = existingList;
        }
        return list;
    }



}
