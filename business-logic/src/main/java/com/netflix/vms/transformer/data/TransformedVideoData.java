package com.netflix.vms.transformer.data;

import com.google.common.collect.Maps;
import com.netflix.vms.transformer.hollowoutput.VideoMiscData;
import com.netflix.vms.transformer.modules.packages.PackageDataCollection;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates two data types for videos.
 * <ul>
 * <li>All the video data (media, meta, images & country specific data) that needs to be grouped by country and video id</li>
 * <li>All the video data needs to be grouped only by videoId (Video miscellaneous data and transformed packages).</li>
 * </ul>
 */
public class TransformedVideoData {

    private Map<String, VideoDataCollection> videoDataCollectionMap;
    private Map<Integer, VideoData> videoDataMap;

    public TransformedVideoData() {
        this.videoDataCollectionMap = Maps.newHashMap();
        this.videoDataMap = Maps.newHashMap();
    }

    public Map<String, VideoDataCollection> getVideoDataCollectionMap() {
        return videoDataCollectionMap;
    }

    public TransformedPackageData getTransformedPackageData(int videoId) {
        videoDataMap.putIfAbsent(videoId, new VideoData());
        return videoDataMap.get(videoId).getTransformedPackageData();
    }

    public VideoDataCollection getVideoDataCollection(String country) {
        videoDataCollectionMap.putIfAbsent(country, new VideoDataCollection());
        return videoDataCollectionMap.get(country);
    }

    public VideoData getVideoData(int videoId) {
        videoDataMap.putIfAbsent(videoId, new VideoData());
        return videoDataMap.get(videoId);
    }

    /**
     * This class represents contains data for a video packages and misc data.
     */
    public class VideoData {

        TransformedPackageData transformedPackageData;
        VideoMiscData videoMiscData;

        protected VideoData() {
            transformedPackageData = new TransformedPackageData();
        }

        public TransformedPackageData getTransformedPackageData() {
            return transformedPackageData;
        }

        public VideoMiscData getVideoMiscData() {
            return videoMiscData;
        }

        public void setVideoMiscData(VideoMiscData miscData) {
            videoMiscData = miscData;
        }
    }

    /**
     * This class encapsulates packages for a video.
     */
    public class TransformedPackageData {
        private Map<Integer, PackageDataCollection> packageDataCollectionMap;
        private int maxPackageId;

        protected TransformedPackageData() {
            packageDataCollectionMap = Maps.newHashMap();
            maxPackageId = Integer.MIN_VALUE;
        }

        public PackageDataCollection getPackageDataCollection(int packageId) {
            return packageDataCollectionMap.get(packageId);
        }

        private int getMaxPackageId() {
            return maxPackageId;
        }

        private void updateMaxPackageId(int packageId) {
            if (maxPackageId < packageId) maxPackageId = packageId;
        }

        public void setPackageDataCollectionMap(Set<PackageDataCollection> packageDataCollections) {
            for (PackageDataCollection packageDataCollection : packageDataCollections) {
                packageDataCollectionMap.put(packageDataCollection.getPackageData().id, packageDataCollection);
                updateMaxPackageId(packageDataCollection.getPackageData().id);
            }
        }
    }
}
