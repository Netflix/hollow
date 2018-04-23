package com.netflix.vms.transformer.data;

import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoMediaData;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.modules.collections.VideoCollectionsDataHierarchy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates/holds media, meta, images & country specific video data by video id.
 */
public class VideoDataCollection {

    private Map<Integer, VideoData> videoIdDataMap;
    private Set<VideoCollectionsDataHierarchy> videoCollectionsDataHierarchies;

    protected VideoDataCollection() {
        this.videoIdDataMap = new HashMap<>();
        this.videoCollectionsDataHierarchies = new HashSet<>();
    }

    private VideoData checkAndFetch(Object obj, String name, int videoId) {
        if (obj == null)
            throw new IllegalArgumentException(name + " cannot be null");
        videoIdDataMap.computeIfAbsent(videoId, f -> new VideoData());
        return videoIdDataMap.get(videoId);
    }

    public Set<VideoCollectionsDataHierarchy> getVideoCollectionsDataHierarchies() {
        return videoCollectionsDataHierarchies;
    }

    public void setVideoCollectionsDataHierarchy(Set<VideoCollectionsDataHierarchy> collectionsDataHierarchies) {
        if (collectionsDataHierarchies == null)
            throw new IllegalArgumentException("Collection hierarchies cannot be null");
        this.videoCollectionsDataHierarchies = collectionsDataHierarchies;
    }

    public void addVideoImages(Integer videoId, VideoImages videoImages) {
        checkAndFetch(videoImages, "Video Images", videoId).setVideoImages(videoImages);
    }

    public void addVideoMetaData(Integer videoId, VideoMetaData videoMetaData) {
        checkAndFetch(videoMetaData, "Video Meta Data", videoId).setVideoMetaData(videoMetaData);
    }

    public void addVideoMediaData(Integer videoId, VideoMediaData videoMediaData) {
        checkAndFetch(videoMediaData, "Video Media Data", videoId).setVideoMediaData(videoMediaData);
    }

    public void addCompleteVideoCountrySpecificData(Integer videoId, CompleteVideoCountrySpecificData completeVideoCountrySpecificData) {
        checkAndFetch(completeVideoCountrySpecificData, "Country Specific Data", videoId).setCompleteVideoCountrySpecificData(completeVideoCountrySpecificData);
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

        private VideoMediaData getVideoMediaData() {
            return videoMediaData;
        }

        private void setVideoMediaData(VideoMediaData videoMediaData) {
            this.videoMediaData = videoMediaData;
        }

        private VideoMetaData getVideoMetaData() {
            return videoMetaData;
        }

        private void setVideoMetaData(VideoMetaData videoMetaData) {
            this.videoMetaData = videoMetaData;
        }

        private VideoImages getVideoImages() {
            return videoImages;
        }

        private void setVideoImages(VideoImages videoImages) {
            this.videoImages = videoImages;
        }

        private CompleteVideoCountrySpecificData getCompleteVideoCountrySpecificData() {
            return completeVideoCountrySpecificData;
        }

        private void setCompleteVideoCountrySpecificData(CompleteVideoCountrySpecificData completeVideoCountrySpecificData) {
            this.completeVideoCountrySpecificData = completeVideoCountrySpecificData;
        }
    }
}
