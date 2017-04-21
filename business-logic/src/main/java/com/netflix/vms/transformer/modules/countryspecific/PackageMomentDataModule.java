package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentListHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.BaseDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDescriptor;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageMomentDataModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final VideoMomentModule videoMomentModule;
    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;
    private final Map<Integer, TrickPlayType> trickPlayTypeMap;

    public PackageMomentDataModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);
        this.videoMomentModule = new VideoMomentModule();
        this.packageMomentDataByPackageId = new HashMap<>();
        this.trickPlayTypeMap = getTrickPlayTypeMap();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if (packageMomentData != null)
            return packageMomentData;

        // create new package moment data
        packageMomentData = new PackageMomentData();

        // get video moment list
        List<VideoMoment> videoMoments = extractVideoMomentsAndOffsets(packageData, inputPackage, packageMomentData);

        // set empty ImageDownloadable list for each moment in map -> ImageDownloadable are not in use.
        packageMomentData.videoMomentToDownloadableListMap = new HashMap<>();
        for (VideoMoment moment : videoMoments)
            packageMomentData.videoMomentToDownloadableListMap.put(moment, new ArrayList<>());

        // Trick play map and still image map.
        buildTrickPlayMap(inputPackage, packageMomentData);
        buildStillImagesMap(packageData, packageMomentData);

        // cache the package moment data.
        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);
        return packageMomentData;
    }

    /**
     * Get all video moments. Find and set start and end offsets from moments in PackageMomentData.
     *
     * @param packageData
     * @param inputPackage
     * @param packageMomentData
     * @return
     */
    private List<VideoMoment> extractVideoMomentsAndOffsets(PackageData packageData, PackageHollow inputPackage, PackageMomentData packageMomentData) {
        List<VideoMoment> videoMoments = new ArrayList<>();
        PackageMomentListHollow momentListHollow = inputPackage._getMoments();
        if (momentListHollow != null) {
            for (PackageMomentHollow packageMomentHollow : momentListHollow) {
                String momentType = packageMomentHollow._getMomentType()._getValue();
                if (momentType.equals("SnackMoment") && packageMomentHollow._getClipSpecRuntimeMillis() != Long.MIN_VALUE) {
                    VideoMoment videoMoment = videoMomentModule.createVideoMoment(packageData.id, packageMomentHollow, momentType);
                    packageMomentData.phoneSnackMoments.add(videoMoment);
                } else {
                    VideoMoment videoMoment = videoMomentModule.createVideoMoment(packageData.id, packageMomentHollow, momentType);
                    videoMoments.add(videoMoment);

                    // find and set start and ending moment offsets.
                    long offset = packageMomentHollow._getOffsetMillis();
                    if (momentType.equals("Start") && offset != Long.MIN_VALUE) {
                        packageMomentData.startMomentOffsetInSeconds = offset / 1000;
                    } else if (momentType.equals("Ending") && offset != Long.MIN_VALUE)
                        packageMomentData.endMomentOffsetInSeconds = offset / 1000;
                }
            }
        }
        return videoMoments;
    }

    private void buildTrickPlayMap(PackageHollow inputPackage, PackageMomentData packageMomentData) {

        for (PackageStreamHollow stream : inputPackage._getDownloadables()) {
            long streamProfileId = stream._getStreamProfileId();
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(streamProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            if ("TRICKPLAY".equals(streamProfileType)) {
                TrickPlayItem trickplay = new TrickPlayItem();
                trickplay.imageCount = stream._getImageInfo()._getImageCount();
                trickplay.videoId = new Video((int) inputPackage._getMovieId());
                trickplay.trickPlayDownloadable = new TrickPlayDownloadable();
                trickplay.trickPlayDownloadable.fileName = new Strings(stream._getFileIdentification()._getFilename());
                trickplay.trickPlayDownloadable.descriptor = new TrickPlayDescriptor();
                trickplay.trickPlayDownloadable.descriptor.height = stream._getDimensions()._getHeightInPixels();
                trickplay.trickPlayDownloadable.descriptor.width = stream._getDimensions()._getWidthInPixels();
                trickplay.trickPlayDownloadable.baseDownloadable = new BaseDownloadable();
                trickplay.trickPlayDownloadable.baseDownloadable.downloadableId = stream._getDownloadableId();
                trickplay.trickPlayDownloadable.baseDownloadable.streamProfileId = (int) streamProfileId;
                trickplay.trickPlayDownloadable.baseDownloadable.originServerNames = new ArrayList<>();

                convertCdnDeploymentsAndAddToList(stream, trickplay.trickPlayDownloadable.baseDownloadable.originServerNames);
                packageMomentData.trickPlayItemMap.put(trickPlayTypeMap.get((int) streamProfileId), trickplay);
            }
        }
    }

    private void convertCdnDeploymentsAndAddToList(PackageStreamHollow stream, List<Strings> originServerNames) {
        Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
        for (CdnDeploymentHollow deployment : cdnDeployments) {
            originServerNames.add(new Strings(deployment._getOriginServer()._getValue()));
        }
    }

    private void buildStillImagesMap(PackageData packageData, PackageMomentData packageMomentData) {
        packageMomentData.stillImagesMap = new HashMap<>();
        for (Map.Entry<VideoMoment, List<ImageDownloadable>> entry : packageMomentData.videoMomentToDownloadableListMap.entrySet()) {
            VideoMoment moment = entry.getKey();

            List<VideoImage> list = packageMomentData.stillImagesMap.get(moment.videoMomentTypeName);
            if (list == null) {
                list = new ArrayList<>();
                packageMomentData.stillImagesMap.put(moment.videoMomentTypeName, list);
            }

            VideoImage image = new VideoImage();
            image.videoId = packageData.video;
            image.videoMoment = moment;
            image.downloadableList = Collections.emptyList();// use empty list for ImageDownloadable.
            list.add(image);
        }

    }

    private Map<Integer, TrickPlayType> getTrickPlayTypeMap() {
        Map<Integer, TrickPlayType> map = new HashMap<>();
        map.put(241, new TrickPlayType("BIF_W240"));
        map.put(321, new TrickPlayType("BIF_W320"));
        map.put(641, new TrickPlayType("BIF_W640"));
        map.put(242, new TrickPlayType("ZIP_W240"));
        map.put(322, new TrickPlayType("ZIP_W320"));
        map.put(642, new TrickPlayType("ZIP_W640"));
        return map;
    }

    public void reset() {
        this.packageMomentDataByPackageId.clear();
    }

}
