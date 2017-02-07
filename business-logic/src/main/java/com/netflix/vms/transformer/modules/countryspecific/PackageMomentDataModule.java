package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadableFilename;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.DownloadableIdHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentListHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.BaseDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadableDescriptor;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TargetDimensions;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDescriptor;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.VideoFormatDescriptorIdentifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageMomentDataModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final VideoMomentModule videoMomentModule;
    private final VideoFormatDescriptorIdentifier videoFormatIdentifier;
    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;

    private final Map<Integer, TrickPlayType> trickPlayTypeMap;

    public PackageMomentDataModule(VMSHollowInputAPI api, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.videoMomentModule = new VideoMomentModule();
        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, cycleConstants, indexer);
        this.packageMomentDataByPackageId = new HashMap<Integer, PackageMomentData>();

        this.trickPlayTypeMap = getTrickPlayTypeMap();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if(packageMomentData != null)
            return packageMomentData;

        packageMomentData = buildDownloadableIdsToVideoMomentsMap(packageData, inputPackage);
        buildIndexedPackageImageResult(inputPackage, packageMomentData);
        buildStillImagesMap(packageData, packageMomentData);

        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);

        return packageMomentData;
    }

    private PackageMomentData buildDownloadableIdsToVideoMomentsMap(PackageData packageData, PackageHollow inputPackage) {
        PackageMomentData data = new PackageMomentData();

        PackageMomentListHollow moments = inputPackage._getMoments();

        if(moments != null) {
            for(PackageMomentHollow packageMoment : inputPackage._getMoments()) {
                String momentType = packageMoment._getMomentType()._getValue();

                if("SnackMoment".equals(momentType) && packageMoment._getClipSpecRuntimeMillis() != Long.MIN_VALUE) {
                    VideoMoment videoMoment = videoMomentModule.createVideoMoment(packageData.id, packageMoment, momentType);
                    data.phoneSnackMoments.add(videoMoment);
                } else if("Start".equals(momentType) || "Ending".equals(momentType) || "BladeImage".equals(momentType)) {
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

    private void buildIndexedPackageImageResult(PackageHollow inputPackage, PackageMomentData packageMomentData) {

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
                    ListOfStringHollow modifications = stream._getModifications();
                    if(modifications != null && !modifications.isEmpty()) {
                        moment = moment.clone();
                        moment.videoMomentTypeName = buildModifiedVideoMomentTypeName(moment.videoMomentTypeName, modifications);
                    }

                    List<ImageDownloadable> list = packageMomentData.videoMomentToDownloadableListMap.get(moment);
                    if(list == null) {
                        list = new ArrayList<ImageDownloadable>();
                        packageMomentData.videoMomentToDownloadableListMap.put(moment, list);
                    }
                    list.add(downloadable);
                }
            } else if("TRICKPLAY".equals(streamProfileType)) {
                TrickPlayItem trickplay = new TrickPlayItem();
                trickplay.imageCount = stream._getImageInfo()._getImageCount();
                trickplay.videoId = new Video((int)inputPackage._getMovieId());
                trickplay.trickPlayDownloadable = new TrickPlayDownloadable();
                trickplay.trickPlayDownloadable.fileName = new TrickPlayDownloadableFilename(stream._getFileIdentification()._getFilename());
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

    private final Map<ModifiedVideoMomentTypeNameKey, Strings> modifiedVideoMomentTypeNameByModificationsOrdinal = new HashMap<>();

    private Strings buildModifiedVideoMomentTypeName(Strings originalTypeName, ListOfStringHollow modifications) {
        ModifiedVideoMomentTypeNameKey cacheKey = new ModifiedVideoMomentTypeNameKey(originalTypeName, modifications.getOrdinal());
        
        Strings tag = modifiedVideoMomentTypeNameByModificationsOrdinal.get(cacheKey);
        if(tag != null)
            return tag;

        StringBuilder builder = new StringBuilder(new String(originalTypeName.value).toUpperCase());

        for(StringHollow modification : modifications) {
            builder.append('_');
            builder.append(modification._getValue().toUpperCase());
        }

        tag = new Strings(builder.toString());
        modifiedVideoMomentTypeNameByModificationsOrdinal.put(cacheKey, tag);
        return tag;
    }
    
    private class ModifiedVideoMomentTypeNameKey {
        private final Strings videoMomentTypeName;
        private final int modificationsOrdinal;
        private final int hashCode;
        
        public ModifiedVideoMomentTypeNameKey(Strings videoMomentTypeName, int modificationsOrdinal) {
            this.videoMomentTypeName = videoMomentTypeName;
            this.modificationsOrdinal = modificationsOrdinal;
            this.hashCode = videoMomentTypeName.hashCode() * 997 + modificationsOrdinal;
        }
        
        public int hashCode() {
            return hashCode;
        }
        
        public boolean equals(Object other) {
            if(other instanceof ModifiedVideoMomentTypeNameKey) {
                return ((ModifiedVideoMomentTypeNameKey) other).modificationsOrdinal == modificationsOrdinal 
                        && Arrays.equals(((ModifiedVideoMomentTypeNameKey) other).videoMomentTypeName.value, videoMomentTypeName.value);
            }
            return false;
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
            
            Collections.sort(image.downloadableList, IMAGE_DOWNLOADABLE_COMPARATOR);

            list.add(image);
        }
        
        for(Map.Entry<Strings, List<VideoImage>> entry : packageMomentData.stillImagesMap.entrySet()) {
            Collections.sort(entry.getValue(), VIDEO_IMAGE_COMPARATOR);
        }
    }
    
    private static final Comparator<ImageDownloadable> IMAGE_DOWNLOADABLE_COMPARATOR = new Comparator<ImageDownloadable>() {
        public int compare(ImageDownloadable o1, ImageDownloadable o2) {
            return Long.compare(o1.downloadableId, o2.downloadableId);
        }
    };

    private static final Comparator<VideoImage> VIDEO_IMAGE_COMPARATOR = new Comparator<VideoImage>() {
        public int compare(VideoImage o1, VideoImage o2) {
            return Integer.compare(o1.videoMoment.sequenceNumber, o2.videoMoment.sequenceNumber);
        }
    };
    
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
