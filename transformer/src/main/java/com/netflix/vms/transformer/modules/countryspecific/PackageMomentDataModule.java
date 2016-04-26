package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.PackageMomentListHollow;

import com.netflix.vms.transformer.modules.packages.VideoFormatDescriptorIdentifier;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import java.util.Set;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowoutput.BaseDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadableDescriptor;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TargetDimensions;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDescriptor;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import java.util.ArrayList;
import com.netflix.vms.transformer.hollowinput.DownloadableIdHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowoutput.PackageData;

public class PackageMomentDataModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final VideoMomentModule videoMomentModule;
    private final VideoFormatDescriptorIdentifier videoFormatIdentifier;
    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;

    private final Map<Integer, TrickPlayType> trickPlayTypeMap;

    public PackageMomentDataModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.videoMomentModule = new VideoMomentModule();
        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, indexer);
        this.packageMomentDataByPackageId = new HashMap<Integer, PackageMomentData>();

        this.trickPlayTypeMap = getTrickPlayTypeMap();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackagesHollow inputPackage) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if(packageMomentData != null)
            return packageMomentData;

        packageMomentData = buildDownloadableIdsToVideoMomentsMap(packageData, inputPackage);
        buildIndexedPackageImageResult(inputPackage, packageMomentData);
        buildStillImagesMap(packageData, packageMomentData);

        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);

        return packageMomentData;
    }

    private PackageMomentData buildDownloadableIdsToVideoMomentsMap(PackageData packageData, PackagesHollow inputPackage) {
        PackageMomentData data = new PackageMomentData();

        PackageMomentListHollow moments = inputPackage._getMoments();

        if(moments != null) {
            for(PackageMomentHollow packageMoment : inputPackage._getMoments()) {
                String momentType = packageMoment._getMomentType()._getValue();

                if("SnackMoment".equals(momentType) && packageMoment._getClipSpecRuntimeMillis() != Long.MIN_VALUE) {
                    VideoMoment videoMoment = videoMomentModule.createVideoMoment(packageData.id, packageMoment, momentType);
                    data.phoneSnackMoments.add(videoMoment);
                } else {
                    List<DownloadableIdHollow> downloadableIdList = packageMoment._getDownloadableIds();

                    if(downloadableIdList != null) {
                        VideoMoment videoMoment = videoMomentModule.createVideoMoment(packageData.id, packageMoment, momentType);

                        for(DownloadableIdHollow id : downloadableIdList) {
                            Long downloadableId = id._getValueBoxed();
                            if(!data.downloadableIdsToVideoMoments.containsKey(downloadableId)) {
                                data.downloadableIdsToVideoMoments.put(downloadableId, videoMoment);
                            }
                            //list.add(videoMoment);
                        }
                    }
                }
            }
        }
        return data;
    }

    private void buildIndexedPackageImageResult(PackagesHollow inputPackage, PackageMomentData packageMomentData) {

        for(PackageStreamHollow stream : inputPackage._getDownloadables()) {
            long streamProfileId = stream._getStreamProfileId();
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(streamProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            if("MERCHSTILL".equals(streamProfileType)) {
                if(stream._getNonImageInfo() != null && stream._getNonImageInfo()._getRuntimeSeconds() != Long.MIN_VALUE)
                    continue;

                ImageDownloadable downloadable = new ImageDownloadable();

                downloadable.downloadableId = stream._getDownloadableId();

                downloadable.originServerNames = new ArrayList<Strings>();
                convertCdnDeploymentsAndAddToList(stream, downloadable.originServerNames);

                downloadable.descriptor = new ImageDownloadableDescriptor();
                downloadable.descriptor.streamProfileId = (int) streamProfileId;
                downloadable.descriptor.videoFormat = videoFormatIdentifier.selectVideoFormatDescriptor(stream);

                StreamDimensionsHollow dimensions = stream._getDimensions();
                if(dimensions != null) {
                    downloadable.descriptor.videoResolution = new VideoResolution();
                    downloadable.descriptor.targetDimensions = new TargetDimensions();
                    downloadable.descriptor.videoResolution.height = dimensions._getHeightInPixels();
                    downloadable.descriptor.videoResolution.width = dimensions._getWidthInPixels();
                    downloadable.descriptor.targetDimensions.heightInPixels = dimensions._getTargetHeightInPixels();
                    downloadable.descriptor.targetDimensions.widthInPixels = dimensions._getTargetWidthInPixels();
                }

                VideoMoment moment = packageMomentData.downloadableIdsToVideoMoments.get(stream._getDownloadableId());
                if(moment != null) {
                    //VideoMoment moment = videoMomentList.get(0);
                    //for(VideoMoment moment : videoMomentList) {
                        List<ImageDownloadable> list = packageMomentData.videoMomentToDownloadableListMap.get(moment);
                        if(list == null) {
                            list = new ArrayList<ImageDownloadable>();
                            packageMomentData.videoMomentToDownloadableListMap.put(moment, list);
                        }
                        list.add(downloadable);
                    //}
                }
            } else if("TRICKPLAY".equals(streamProfileType)) {
                TrickPlayItem trickplay = new TrickPlayItem();
                trickplay.imageCount = stream._getImageInfo()._getImageCount();
                trickplay.videoId = new Video((int)inputPackage._getMovieId());
                trickplay.trickPlayDownloadable = new TrickPlayDownloadable();
                trickplay.trickPlayDownloadable.fileName = new Strings(stream._getFileIdentification()._getFilename());
                trickplay.trickPlayDownloadable.descriptor = new TrickPlayDescriptor();
                trickplay.trickPlayDownloadable.descriptor.height = stream._getDimensions()._getHeightInPixels();
                trickplay.trickPlayDownloadable.descriptor.width = stream._getDimensions()._getWidthInPixels();
                trickplay.trickPlayDownloadable.baseDownloadable = new BaseDownloadable();
                trickplay.trickPlayDownloadable.baseDownloadable.downloadableId = stream._getDownloadableId();
                trickplay.trickPlayDownloadable.baseDownloadable.streamProfileId = (int)streamProfileId;
                trickplay.trickPlayDownloadable.baseDownloadable.originServerNames = new ArrayList<Strings>();

                convertCdnDeploymentsAndAddToList(stream, trickplay.trickPlayDownloadable.baseDownloadable.originServerNames);

                // Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
                // trickplay.trickPlayDownloadable.baseDownloadable.envBasedDirectory
                // trickplay.trickPlayDownloadable.baseDownloadable.originServerNames = stream

                packageMomentData.trickPlayItemMap.put(trickPlayTypeMap.get((int) streamProfileId), trickplay);
            }
        }
    }

    private void convertCdnDeploymentsAndAddToList(PackageStreamHollow stream, List<Strings> originServerNames) {
        Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
        for(CdnDeploymentHollow deployment : cdnDeployments) {
            originServerNames.add(new Strings(deployment._getOriginServer()._getValue()));
        }
    }

    private void buildStillImagesMap(PackageData packageData, PackageMomentData packageMomentData) {
        packageMomentData.stillImagesMap = new HashMap<Strings, List<VideoImage>>();
        for(Map.Entry<VideoMoment, List<ImageDownloadable>> entry : packageMomentData.videoMomentToDownloadableListMap.entrySet()) {
            VideoMoment moment = entry.getKey();

            List<VideoImage> list = packageMomentData.stillImagesMap.get(moment.videoMomentTypeName);
            if(list == null) {
                list = new ArrayList<VideoImage>();
                packageMomentData.stillImagesMap.put(moment.videoMomentTypeName, list);
            }

            VideoImage image = new VideoImage();
            image.videoId = packageData.video;
            image.videoMoment = moment;
            image.downloadableList = entry.getValue();

            list.add(image);
        }
    }

    private Map<Integer, TrickPlayType> getTrickPlayTypeMap() {
        Map<Integer, TrickPlayType> map = new HashMap<Integer, TrickPlayType>();

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
