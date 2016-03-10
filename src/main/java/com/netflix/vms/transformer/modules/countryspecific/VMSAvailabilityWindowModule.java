package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.CdnDeploymentHollow;
import com.netflix.vms.transformer.hollowinput.DownloadableIdHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.StreamDimensionsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractIdHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadable;
import com.netflix.vms.transformer.hollowoutput.ImageDownloadableDescriptor;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.PixelAspect;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TargetDimensions;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.VideoMoment;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.VideoResolution;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.VideoFormatDescriptorIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class VMSAvailabilityWindowModule {
    

    private final VMSHollowVideoInputAPI api;
    

    private final HollowPrimaryKeyIndex packageIdx;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final VideoFormatDescriptorIdentifier videoFormatIdentifier;

    private final Map<Integer, Strings> soundTypesMap;
    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);
    private final VideoPackageInfo FILTERED_VIDEO_PACKAGE_INFO;

    private Map<Integer, List<PackageData>> transformedPackageData;
    
    public VMSAvailabilityWindowModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        
        this.packageIdx = indexer.getPrimaryKeyIndex(IndexSpec.PACKAGES);
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, indexer);

        this.soundTypesMap = getSoundTypesMap();
        FILTERED_VIDEO_PACKAGE_INFO = getFilteredVideoPackageInfo();
    }
    
    public void setTransformedPackageData(Map<Integer, List<PackageData>> data) {
        this.transformedPackageData = data;
    }
    
    private Map<Integer, Strings> getSoundTypesMap() {
        Map<Integer, Strings> map = new HashMap<Integer, Strings>();

        map.put(1, new Strings("1.0"));
        map.put(2, new Strings("2.0"));
        map.put(6, new Strings("5.1"));
        map.put(8, new Strings("8.1"));

        return map;
    }



    public void populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, VideoRightsHollow videoRights, CountrySpecificRollupValues rollup) {
        List<VMSAvailabilityWindow> availabilityWindows;
        boolean isGoLive = isGoLive(videoRights);

        //if(videoId == 80011539 && "CL".equals(country))
        //    System.out.println("asdf");
        
        VideoRightsRightsHollow rights = videoRights._getRights();
        if((rollup.doShow() && rollup.wasShowEpisodeFound()) || (rollup.doSeason() && rollup.wasSeasonEpisodeFound())) {
            long minStartDate = Long.MAX_VALUE;
            long maxEndDate = 0;
            
            for(VideoRightsWindowHollow window : rights._getWindows()) {
                long startDate = window._getStartDate()._getValue();
                long endDate = window._getEndDate()._getValue();
                if(startDate < minStartDate)
                    minStartDate = startDate;
                if(endDate > maxEndDate)
                    maxEndDate = endDate;
                
            }
            
            VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
            outputWindow.startDate = new Date(minStartDate);
            outputWindow.endDate = new Date(maxEndDate);
            outputWindow.bundledAssetsGroupId = rollup.getFirstEpisodeBundledAssetId();
            outputWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();
            
            WindowPackageContractInfo contractInfo = new WindowPackageContractInfo();
            contractInfo.videoContractInfo = new VideoContractInfo();
            contractInfo.videoContractInfo.contractId = outputWindow.bundledAssetsGroupId;
            
            contractInfo.videoPackageInfo = new VideoPackageInfo();
            contractInfo.videoPackageInfo.packageId = 0;
            
            outputWindow.windowInfosByPackageId.put(ZERO, contractInfo);
            
            availabilityWindows = Collections.singletonList(outputWindow);
            
        } else {
            availabilityWindows = new ArrayList<VMSAvailabilityWindow>();
        
            int maxPackageId = 0;
            int bundledAssetsGroupId = 0; /// the contract ID for the highest package ID;
            for(VideoRightsWindowHollow window : rights._getWindows()) {
                VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
                outputWindow.startDate = new Date(window._getStartDate()._getValue());
                outputWindow.endDate = new Date(window._getEndDate()._getValue());
                outputWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();
    
                for(VideoRightsContractIdHollow contractIdHollow : window._getContractIds()) {
                    long contractId = contractIdHollow._getValue();
                    
                    VideoRightsContractHollow contract = getContract(rights, contractIdHollow._getValue());
    
                    if(contract != null) {
                        List<VideoRightsContractPackageHollow> packageIdList = contract._getPackages();
    
                        for(VideoRightsContractPackageHollow pkg : packageIdList) {
                            com.netflix.vms.transformer.hollowoutput.Integer packageId = new com.netflix.vms.transformer.hollowoutput.Integer((int)pkg._getPackageId());
    
                            WindowPackageContractInfo windowPackageContractInfo = outputWindow.windowInfosByPackageId.get(packageId);
                            if(windowPackageContractInfo != null) {
                                VideoPackageInfo videoPackageInfo = windowPackageContractInfo.videoPackageInfo;
                                // MERGE MULTIPLE CONTRACTS
    
    
                            } else {
                                PackageData packageData = getPackageData(videoId, pkg._getPackageId());
                                if(packageData != null) {
                                    if(shouldFilterOutWindowInfo(isGoLive, contract)) {
                                        outputWindow.windowInfosByPackageId.put(ZERO, buildFilteredWindowPackageContractInfo((int) contractId));
                                    } else {
                                        outputWindow.windowInfosByPackageId.put(packageId, buildWindowPackageContractInfo(packageData, contract, country));
                                        if(packageData.id > maxPackageId) {
                                            maxPackageId = packageData.id;
                                            bundledAssetsGroupId = (int)contractId;
                                        }
                                    }
                                }
                            }
    
                        }
                    } else {
                        outputWindow.windowInfosByPackageId.put(ZERO, buildFilteredWindowPackageContractInfo((int) contractIdHollow._getValue()));
                    }
                }
    
                outputWindow.bundledAssetsGroupId = bundledAssetsGroupId;
    
                availabilityWindows.add(outputWindow);
                rollup.newEpisodeData(isGoLive, bundledAssetsGroupId);
            }
            
            Collections.sort(availabilityWindows, new Comparator<VMSAvailabilityWindow>() {
                public int compare(VMSAvailabilityWindow o1, VMSAvailabilityWindow o2) {
                    return Long.compare(o1.startDate.val, o2.startDate.val);
                }
            });
        }


        data.mediaAvailabilityWindows = availabilityWindows;
        data.imagesAvailabilityWindows = availabilityWindows;
    }

    private WindowPackageContractInfo buildWindowPackageContractInfo(PackageData packageData, VideoRightsContractHollow contract, String country) {
        PackagesHollow inputPackage = api.getPackagesHollow(packageIdx.getMatchingOrdinal((long)packageData.id));

        Map<Long, List<VideoMoment>> downloadableIdsToVideoMoments = new HashMap<Long, List<VideoMoment>>();
        for(PackageMomentHollow packageMoment : inputPackage._getMoments()) {
            List<DownloadableIdHollow> downloadableIdList = packageMoment._getDownloadableIds();

            if(downloadableIdList != null) {
                VideoMoment videoMoment = new VideoMoment();
                videoMoment.bifIndex = packageMoment._getBifIndex();
                videoMoment.msOffset = packageMoment._getOffsetMillis();
                videoMoment.packageId = packageData.id;
                videoMoment.runtimeMs = packageMoment._getClipSpecRuntimeMillis();
                videoMoment.sequenceNumber = (int) packageMoment._getMomentSeqNumber();
                videoMoment.videoMomentTypeName = new Strings(packageMoment._getMomentType()._getValue());

                StringHollow packageMomentTags = packageMoment._getTags();
                if(packageMomentTags != null) {
                    String tags = packageMomentTags._getValue();
                    if(!"".equals(tags)) {
                        videoMoment.momentTags = new ArrayList<Strings>();
                        for(String tag : tags.split(",")) {
                            videoMoment.momentTags.add(new Strings(tag));
                        }
                    }
                }

                if(videoMoment.momentTags == null)
                    videoMoment.momentTags = Collections.emptyList();

                for(DownloadableIdHollow id : downloadableIdList) {
                    Long downloadableId = id._getValueBoxed();
                    List<VideoMoment> list = downloadableIdsToVideoMoments.get(downloadableId);
                    if(list == null) {
                        list = new ArrayList<VideoMoment>();
                        downloadableIdsToVideoMoments.put(downloadableId, list);
                    }
                    list.add(videoMoment);
                }
            }
        }


        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) contract._getContractId();
        info.videoContractInfo.primaryPackageId = (int) contract._getPackageId();
        if(contract._getPrePromotionDays() != 0)
            info.videoContractInfo.prePromotionDays = (int) contract._getPrePromotionDays();
        info.videoContractInfo.isDayAfterBroadcast = contract._getDayAfterBroadcast();
        info.videoContractInfo.hasRollingEpisodes = contract._getDayAfterBroadcast();
        info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.singletonList(new Strings(contract._getCupToken()._getValue())));
        info.videoContractInfo.assetBcp47Codes = new HashSet<Strings>();

        for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
            info.videoContractInfo.assetBcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
        }

        info.videoPackageInfo = new VideoPackageInfo();
        info.videoPackageInfo.packageId = packageData.id;
        info.videoPackageInfo.formats = new HashSet<VideoFormatDescriptor>();
        info.videoPackageInfo.soundTypes = new ArrayList<Strings>();

        Set<com.netflix.vms.transformer.hollowoutput.Long> excludedDownloadables = findRelevantExcludedDownloadables(packageData, country);

        Set<Integer> soundTypesAudioChannels = new TreeSet<Integer>();
        Set<String> screenFormats = new TreeSet<String>();

        long longestRuntimeInSeconds = 0;

        for(StreamData streamData : packageData.streams) {
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) streamData.downloadDescriptor.encodingProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            /// add the videoFormatDescriptor
            VideoFormatDescriptor descriptor = streamData.downloadDescriptor.videoFormatDescriptor;
            if(descriptor.id == 1 || descriptor.id == 3 || descriptor.id == 4)  // Only interested in HD or better
                info.videoPackageInfo.formats.add(descriptor);

            if("VIDEO".equals(streamProfileType) || "MUXED".equals(streamProfileType)) {
                if(streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds)
                    longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;

                PixelAspect pixelAspect = streamData.streamDataDescriptor.pixelAspect;
                VideoResolution videoResolution = streamData.streamDataDescriptor.videoResolution;

                if(pixelAspect != null && videoResolution != null && videoResolution.height != 0 && videoResolution.width != 0) {
                    int parHeight = Math.max(pixelAspect.height, 1);
                    int parWidth = Math.max(pixelAspect.width, 1);

                    float screenFormat = ((float) (videoResolution.width * parWidth)) / (videoResolution.height * parHeight);
                    screenFormats.add(String.format("%.2f:1", screenFormat));
                }

            } else if("AUDIO".equals(streamProfileType)) {
                if(excludedDownloadables != null && !excludedDownloadables.contains(new com.netflix.vms.transformer.hollowoutput.Long(streamData.downloadableId)))
                    soundTypesAudioChannels.add(Integer.valueOf((int)profile._getAudioChannelCount()));
            }
        }

        Map<VideoMoment, List<ImageDownloadable>> videoMomentToDownloadableListMap = buildVideoMomentToDownloadableListMap(inputPackage, downloadableIdsToVideoMoments);

        info.videoPackageInfo.stillImagesMap = new HashMap<Strings, List<VideoImage>>();
        for(Map.Entry<VideoMoment, List<ImageDownloadable>> entry : videoMomentToDownloadableListMap.entrySet()) {
            VideoMoment moment = entry.getKey();

            List<VideoImage> list = info.videoPackageInfo.stillImagesMap.get(moment.videoMomentTypeName);
            if(list == null) {
                list = new ArrayList<VideoImage>();
                info.videoPackageInfo.stillImagesMap.put(moment.videoMomentTypeName, list);
            }

            VideoImage image = new VideoImage();
            image.videoId = packageData.video;
            image.videoMoment = moment;
            image.downloadableList = entry.getValue();

            list.add(image);
        }

        info.videoPackageInfo.screenFormats = new ArrayList<Strings>(screenFormats.size());
        for(String screenFormat : screenFormats) {
            info.videoPackageInfo.screenFormats.add(new Strings(screenFormat));
        }

        info.videoPackageInfo.soundTypes = new ArrayList<Strings>(soundTypesAudioChannels.size());
        for(Integer soundType : soundTypesAudioChannels) {
            Strings soundTypeStr= soundTypesMap.get(soundType);
            if(soundTypeStr != null)
                info.videoPackageInfo.soundTypes.add(soundTypeStr);
        }


        info.videoPackageInfo.runtimeInSeconds = (int) longestRuntimeInSeconds;

        return info;
    }

    private VideoRightsContractHollow getContract(VideoRightsRightsHollow rights, long contractId) {
        for(VideoRightsContractHollow contract : rights._getContracts()) {
            if(contract._getContractId() == contractId)
                return contract;
        }
        return null;
    }

    private PackageData getPackageData(Integer videoId, long packageId) {
        List<PackageData> list = transformedPackageData.get(videoId);
        if(list == null)
            return null;

        for(int i=0;i<list.size();i++) {
            if(list.get(i).id == packageId)
                return list.get(i);
        }

        return null;
    }

    private boolean shouldFilterOutWindowInfo(boolean isGoLive, VideoRightsContractHollow contract) {
        if(isGoLive)
            return false;

        if (contract._getDayAfterBroadcast()) return false;

        return contract._getPrePromotionDays() <= 0;
    }


    private boolean isGoLive(VideoRightsHollow rights) {
        VideoRightsFlagsHollow flags = rights._getFlags();
        return flags != null && flags._getGoLive();
    }

    private WindowPackageContractInfo buildFilteredWindowPackageContractInfo(int contractId) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = getFilteredVideoContractInfo(contractId);
        info.videoPackageInfo = FILTERED_VIDEO_PACKAGE_INFO;
        return info;
    }


    private Map<VideoMoment, List<ImageDownloadable>> buildVideoMomentToDownloadableListMap(PackagesHollow inputPackage, Map<Long, List<VideoMoment>> downloadableIdsToVideoMoments) {
        Map<VideoMoment, List<ImageDownloadable>> videoMomentToDownloadableListMap = new HashMap<VideoMoment, List<ImageDownloadable>>();
        for(PackageStreamHollow stream : inputPackage._getDownloadables()) {
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal(stream._getStreamProfileId());
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            if("MERCHSTILL".equals(streamProfileType)) {
                ImageDownloadable downloadable = new ImageDownloadable();

                downloadable.downloadableId = stream._getDownloadableId();

                downloadable.originServerNames = new ArrayList<Strings>();
                Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
                for(CdnDeploymentHollow deployment : cdnDeployments) {
                    downloadable.originServerNames.add(new Strings(deployment._getOriginServer()._getValue()));
                }

                downloadable.descriptor = new ImageDownloadableDescriptor();
                downloadable.descriptor.streamProfileId = (int) stream._getStreamProfileId();
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

                List<VideoMoment> videoMomentList = downloadableIdsToVideoMoments.get(stream._getDownloadableId());
                if(videoMomentList != null) {
                    for(VideoMoment moment : videoMomentList) {
                        List<ImageDownloadable> list = videoMomentToDownloadableListMap.get(moment);
                        if(list == null) {
                            list = new ArrayList<ImageDownloadable>();
                            videoMomentToDownloadableListMap.put(moment, list);
                        }
                        list.add(downloadable);
                    }
                }
            }
        }
        return videoMomentToDownloadableListMap;
    }

    private Set<com.netflix.vms.transformer.hollowoutput.Long> findRelevantExcludedDownloadables(PackageData packageData, String country) {
        Set<ContractRestriction> countryContractRestrictions = packageData.contractRestrictions.get(new ISOCountry(country));

        if(countryContractRestrictions == null)
            return null;

        long now = System.currentTimeMillis();

        Set<com.netflix.vms.transformer.hollowoutput.Long> nextExcludedDownloadables = Collections.emptySet();
        long nextStartDate = Long.MAX_VALUE;

        for(ContractRestriction restriction : countryContractRestrictions) {
            if(now > restriction.availabilityWindow.startDate.val && now < restriction.availabilityWindow.endDate.val) {
                return restriction.excludedDownloadables;
            } else if(now < restriction.availabilityWindow.startDate.val) {
                if(nextStartDate > restriction.availabilityWindow.startDate.val) {
                    nextStartDate = restriction.availabilityWindow.startDate.val;
                    nextExcludedDownloadables = restriction.excludedDownloadables;
                }
            }
        }

        return nextExcludedDownloadables;
    }

    private VideoContractInfo getFilteredVideoContractInfo(int contractId) {
        VideoContractInfo info = new VideoContractInfo();
        info.contractId = contractId;
        info.primaryPackageId = 0;
        info.cupTokens = new LinkedHashSetOfStrings();
        info.cupTokens.ordinals = Collections.emptyList();
        info.assetBcp47Codes = Collections.emptySet();
        return info;
    }

    private VideoPackageInfo getFilteredVideoPackageInfo() {
        VideoPackageInfo info = new VideoPackageInfo();
        info.packageId = 0;
        info.runtimeInSeconds = 0;
        info.soundTypes = Collections.emptyList();
        info.screenFormats = Collections.emptyList();
        info.phoneSnacks = Collections.emptyList();
        info.stillImagesMap = Collections.emptyMap();
        info.videoClipMap = Collections.emptyMap();
        info.trickPlayMap = Collections.emptyMap();
        info.formats = Collections.emptySet();
        return info;
    }
    
    public void reset() {
        this.transformedPackageData = null;
    }

}
