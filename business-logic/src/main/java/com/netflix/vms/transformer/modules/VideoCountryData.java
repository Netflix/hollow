package com.netflix.vms.transformer.modules;

import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POJO to encapsulate data for each video.
 */
public class VideoCountryData {

    private Map<Integer, VideoData> videoIdDataMap;
    private Set<VideoCollectionsDataHierarchy> videoCollectionsDataHierarchies;

    public VideoCountryData() {
        this.videoIdDataMap = new ConcurrentHashMap<>();
    }

    private void checkNull(Object obj, String name) {
        if (obj == null)
            throw new IllegalArgumentException(name + " cannot be null");
    }

    public Set<VideoCollectionsDataHierarchy> getVideoCollectionsDataHierarchies() {
        return videoCollectionsDataHierarchies;
    }

    public void addVideoCollectionsDataHierarchy(Set<VideoCollectionsDataHierarchy> collectionsDataHierarchies) {
        checkNull(collectionsDataHierarchies, "Collection data hierarchy");
        this.videoCollectionsDataHierarchies = collectionsDataHierarchies;
    }

    public void addVideoImages(Integer videoId, VideoImages videoImages) {
        checkNull(videoImages, "Video Images");
        videoIdDataMap.computeIfAbsent(videoId, f -> new VideoData());
        videoIdDataMap.get(videoId).setVideoImages(videoImages);
    }

    public void addVideoMetaData(Integer videoId, VideoMetaData videoMetaData) {
        checkNull(videoMetaData, "Video Meta Data");
        videoIdDataMap.computeIfAbsent(videoId, f -> new VideoData());
        videoIdDataMap.get(videoId).setVideoMetaData(videoMetaData);
    }

    public void addVideoMediaData(Integer videoId, VideoMediaData videoMediaData) {
        checkNull(videoMediaData, "Video Media Data");
        videoIdDataMap.computeIfAbsent(videoId, f -> new VideoData());
        videoIdDataMap.get(videoId).setVideoMediaData(videoMediaData);
    }

    public void addCompleteVideoCountrySpecificData(Integer id, CompleteVideoCountrySpecificData completeVideoCountrySpecificData) {
        checkNull(completeVideoCountrySpecificData, "Country Specific Data");
        videoIdDataMap.computeIfAbsent(id, f -> new VideoData());
        videoIdDataMap.get(id).setCompleteVideoCountrySpecificData(completeVideoCountrySpecificData);
    }

    public VideoMetaData getVideoMetaData(Integer videoId) {
        VideoData videoData = videoIdDataMap.get(videoId);
        if (videoData != null)
            return videoData.getVideoMetaData();
        return null;
    }

    public VideoMediaData getVideoMediaData(Integer videoId) {
        VideoData videoData = videoIdDataMap.get(videoId);
        if (videoData != null)
            return videoData.getVideoMediaData();
        return null;
    }

    public VideoImages getVideoImages(Integer videoId) {
        VideoData videoData = videoIdDataMap.get(videoId);
        if (videoData != null)
            return videoData.getVideoImages();
        return null;
    }

    public CompleteVideoCountrySpecificData getCompleteVideoCountrySpecificData(Integer videoId) {
        VideoData videoData = videoIdDataMap.get(videoId);
        if (videoData != null)
            return videoData.getCompleteVideoCountrySpecificData();
        return null;
    }

    public Set<Integer> getVideoIdSetForVideoData() {
        return videoIdDataMap.keySet();
    }

    private class VideoData {

        private VideoMediaData videoMediaData;
        private VideoMetaData videoMetaData;
        private VideoImages videoImages;
        private CompleteVideoCountrySpecificData completeVideoCountrySpecificData;

        public VideoMediaData getVideoMediaData() {
            return videoMediaData;
        }

        public void setVideoMediaData(VideoMediaData videoMediaData) {
            this.videoMediaData = videoMediaData;
        }

        public VideoMetaData getVideoMetaData() {
            return videoMetaData;
        }

        public void setVideoMetaData(VideoMetaData videoMetaData) {
            this.videoMetaData = videoMetaData;
        }

        public VideoImages getVideoImages() {
            return videoImages;
        }

        public void setVideoImages(VideoImages videoImages) {
            this.videoImages = videoImages;
        }

        public CompleteVideoCountrySpecificData getCompleteVideoCountrySpecificData() {
            return completeVideoCountrySpecificData;
        }

        public void setCompleteVideoCountrySpecificData(CompleteVideoCountrySpecificData completeVideoCountrySpecificData) {
            this.completeVideoCountrySpecificData = completeVideoCountrySpecificData;
        }
    }
}
