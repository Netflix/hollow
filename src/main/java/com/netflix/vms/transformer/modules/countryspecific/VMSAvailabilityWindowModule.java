package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.CdnDeploymentSetHollow;

import com.netflix.vms.transformer.hollowoutput.BaseDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDescriptor;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.TrickPlayItem;
import com.netflix.vms.transformer.hollowoutput.TrickPlayDownloadable;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
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
    private final Map<Integer, TrickPlayType> trickPlayTypeMap;
    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);
    private final VideoPackageInfo FILTERED_VIDEO_PACKAGE_INFO;
    private final LinkedHashSetOfStrings EMPTY_CUP_TOKENS;

    private Map<Integer, List<PackageData>> transformedPackageData;

    public VMSAvailabilityWindowModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;

        this.packageIdx = indexer.getPrimaryKeyIndex(IndexSpec.PACKAGES);
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, indexer);

        this.soundTypesMap = getSoundTypesMap();
        this.trickPlayTypeMap = getTrickPlayTypeMap();
        FILTERED_VIDEO_PACKAGE_INFO = getFilteredVideoPackageInfo();
        EMPTY_CUP_TOKENS = new LinkedHashSetOfStrings();
        EMPTY_CUP_TOKENS.ordinals = Collections.emptyList();
    }


    public void setTransformedPackageData(Map<Integer, List<PackageData>> data) {
        this.transformedPackageData = data;
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

    private Map<Integer, Strings> getSoundTypesMap() {
        Map<Integer, Strings> map = new HashMap<Integer, Strings>();

        map.put(1, new Strings("1.0"));
        map.put(2, new Strings("2.0"));
        map.put(6, new Strings("5.1"));
        map.put(8, new Strings("8.1"));

        return map;
    }



    public void populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, VideoRightsHollow videoRights, CountrySpecificRollupValues rollup) {
        boolean isGoLive = isGoLive(videoRights);

        if(videoId == 70286188 && "MQ".equals(country))
            System.out.println("asdf");

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

            WindowPackageContractInfo videoImagesContractInfo = createEmptyContractInfoForRollup(outputWindow);
            WindowPackageContractInfo videoMediaContractInfo = createEmptyContractInfoForRollup(outputWindow);

            VMSAvailabilityWindow videoImagesAvailabilityWindow = outputWindow.clone();
            VMSAvailabilityWindow videoMediaAvailabilityWindow = outputWindow.clone();

            videoImagesAvailabilityWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();
            videoMediaAvailabilityWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();

            videoImagesAvailabilityWindow.windowInfosByPackageId.put(ZERO, videoImagesContractInfo);
            videoMediaAvailabilityWindow.windowInfosByPackageId.put(ZERO, videoMediaContractInfo);

            videoMediaContractInfo.videoContractInfo.assetBcp47Codes = rollup.getAssetBcp47Codes();
            videoMediaContractInfo.videoContractInfo.prePromotionDays = rollup.getPrePromoDays();
            videoMediaContractInfo.videoContractInfo.postPromotionDays = 0;
            videoMediaContractInfo.videoContractInfo.cupTokens = rollup.getCupTokens() != null ? rollup.getCupTokens() : EMPTY_CUP_TOKENS;
            videoMediaContractInfo.videoPackageInfo.formats = rollup.getVideoFormatDescriptors();

            videoImagesContractInfo.videoPackageInfo.stillImagesMap = rollup.getVideoImageMap();

            data.mediaAvailabilityWindows = Collections.singletonList(videoMediaAvailabilityWindow);
            data.imagesAvailabilityWindows = Collections.singletonList(videoImagesAvailabilityWindow);
        } else {
            List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<VMSAvailabilityWindow>();

            long minWindowStartDate = Long.MAX_VALUE;
            VMSAvailabilityWindow currentOrFirstFutureWindow = null;
            boolean isInWindow = false;

            int maxPackageId = 0;
            int bundledAssetsGroupId = 0; /// the contract ID for the highest package ID across all windows;
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
                                        windowPackageContractInfo = buildWindowPackageContractInfo(packageData, contract, country);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                        if(packageData.id >= maxPackageId) {
                                            maxPackageId = packageData.id;
                                            bundledAssetsGroupId = (int)contractId;
                                        }

                                        if(window._getEndDate()._getValue() > System.currentTimeMillis() && window._getStartDate()._getValue() < minWindowStartDate) {
                                            minWindowStartDate = window._getStartDate()._getValue();
                                            currentOrFirstFutureWindow = outputWindow;

                                            if(isGoLive && window._getStartDate()._getValue() < System.currentTimeMillis())
                                                isInWindow = true;
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

                if(currentOrFirstFutureWindow != null) {
                    maxPackageId = 0;
                    Set<Strings> assetBcp47CodesFromMaxPackageId = null;
                    Set<VideoFormatDescriptor> videoFormatDescriptorsFromMaxPackageId = null;
                    int prePromoDays = 0;
                    LinkedHashSetOfStrings cupTokens = null;
                    Map<Strings, List<VideoImage>> stillImagesByTypeMap = Collections.emptyMap();

                    for(Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : currentOrFirstFutureWindow.windowInfosByPackageId.entrySet()) {
                        if(entry.getKey().val > maxPackageId) {
                            maxPackageId = entry.getKey().val;
                            assetBcp47CodesFromMaxPackageId = entry.getValue().videoContractInfo.assetBcp47Codes;
                            videoFormatDescriptorsFromMaxPackageId = entry.getValue().videoPackageInfo.formats;
                            prePromoDays = entry.getValue().videoContractInfo.prePromotionDays;
                            cupTokens = entry.getValue().videoContractInfo.cupTokens;
                            if(isGoLive && isInWindow)
                                stillImagesByTypeMap = entry.getValue().videoPackageInfo.stillImagesMap;
                        }
                    }

                    rollup.newAssetBcp47Codes(assetBcp47CodesFromMaxPackageId);
                    rollup.newVideoFormatDescriptors(videoFormatDescriptorsFromMaxPackageId);
                    rollup.newPrePromoDays(prePromoDays);
                    rollup.newCupTokens(cupTokens);
                    rollup.newEpisodeStillImagesByTypeMap(stillImagesByTypeMap);
                }


                availabilityWindows.add(outputWindow);
                rollup.newEpisodeData(isGoLive, bundledAssetsGroupId);
            }

            Collections.sort(availabilityWindows, new Comparator<VMSAvailabilityWindow>() {
                public int compare(VMSAvailabilityWindow o1, VMSAvailabilityWindow o2) {
                    return Long.compare(o1.startDate.val, o2.startDate.val);
                }
            });

            data.mediaAvailabilityWindows = availabilityWindows;
            data.imagesAvailabilityWindows = availabilityWindows;
        }

    }

    private WindowPackageContractInfo createEmptyContractInfoForRollup(VMSAvailabilityWindow outputWindow) {
        WindowPackageContractInfo contractInfo = new WindowPackageContractInfo();
        contractInfo.videoContractInfo = new VideoContractInfo();
        contractInfo.videoContractInfo.contractId = outputWindow.bundledAssetsGroupId;
        contractInfo.videoContractInfo.primaryPackageId = 0;

        contractInfo.videoPackageInfo = new VideoPackageInfo();
        contractInfo.videoPackageInfo.packageId = 0;
        contractInfo.videoPackageInfo.runtimeInSeconds = 0;
        contractInfo.videoPackageInfo.soundTypes = Collections.emptyList();
        contractInfo.videoPackageInfo.screenFormats = Collections.emptyList();
        contractInfo.videoPackageInfo.phoneSnacks = Collections.emptyList();
        contractInfo.videoPackageInfo.videoClipMap = Collections.emptyMap();
        contractInfo.videoPackageInfo.trickPlayMap = Collections.emptyMap();
        return contractInfo;
    }

    private WindowPackageContractInfo buildWindowPackageContractInfo(PackageData packageData, VideoRightsContractHollow contract, String country) {
        PackagesHollow inputPackage = api.getPackagesHollow(packageIdx.getMatchingOrdinal((long)packageData.id));

        Map<Long, VideoMoment> downloadableIdsToVideoMoments = buildDownloadableIdsToVideoMomentsMap(packageData, inputPackage);

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

        IndexedPackageImageResult indexedPackageImageStuff = buildIndexedPackageImageResult(inputPackage, downloadableIdsToVideoMoments);

        info.videoPackageInfo.stillImagesMap = buildStillImagesMap(packageData, indexedPackageImageStuff);

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

        info.videoPackageInfo.trickPlayMap = indexedPackageImageStuff.trickPlayItemMap;

        info.videoPackageInfo.runtimeInSeconds = (int) longestRuntimeInSeconds;

        return info;
    }


    private Map<Strings, List<VideoImage>> buildStillImagesMap(PackageData packageData, IndexedPackageImageResult indexedPackageImageStuff) {
        Map<Strings, List<VideoImage>> stillImagesMap = new HashMap<Strings, List<VideoImage>>();
        for(Map.Entry<VideoMoment, List<ImageDownloadable>> entry : indexedPackageImageStuff.videoMomentToDownloadableListMap.entrySet()) {
            VideoMoment moment = entry.getKey();

            List<VideoImage> list = stillImagesMap.get(moment.videoMomentTypeName);
            if(list == null) {
                list = new ArrayList<VideoImage>();
                stillImagesMap.put(moment.videoMomentTypeName, list);
            }

            VideoImage image = new VideoImage();
            image.videoId = packageData.video;
            image.videoMoment = moment;
            image.downloadableList = entry.getValue();

            list.add(image);
        }
        return stillImagesMap;
    }


    private Map<Long, VideoMoment> buildDownloadableIdsToVideoMomentsMap(PackageData packageData, PackagesHollow inputPackage) {
        Map<Long, VideoMoment> downloadableIdsToVideoMoments = new HashMap<Long, VideoMoment>();
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
                    if(!downloadableIdsToVideoMoments.containsKey(downloadableId)) {
                        downloadableIdsToVideoMoments.put(downloadableId, videoMoment);
                    }
                    //list.add(videoMoment);
                }
            }
        }
        return downloadableIdsToVideoMoments;
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

    private IndexedPackageImageResult buildIndexedPackageImageResult(PackagesHollow inputPackage, Map<Long, VideoMoment> downloadableIdsToVideoMoments) {
        IndexedPackageImageResult result = new IndexedPackageImageResult();

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

                VideoMoment moment = downloadableIdsToVideoMoments.get(stream._getDownloadableId());
                if(moment != null) {
                    //VideoMoment moment = videoMomentList.get(0);
                    //for(VideoMoment moment : videoMomentList) {
                        List<ImageDownloadable> list = result.videoMomentToDownloadableListMap.get(moment);
                        if(list == null) {
                            list = new ArrayList<ImageDownloadable>();
                            result.videoMomentToDownloadableListMap.put(moment, list);
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

                Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
                // trickplay.trickPlayDownloadable.baseDownloadable.envBasedDirectory
                // trickplay.trickPlayDownloadable.baseDownloadable.originServerNames = stream



                result.trickPlayItemMap.put(trickPlayTypeMap.get((int) streamProfileId), trickplay);
            }
        }

        return result;
    }


    private void convertCdnDeploymentsAndAddToList(PackageStreamHollow stream, List<Strings> originServerNames) {
        Set<CdnDeploymentHollow> cdnDeployments = stream._getDeployment()._getDeploymentInfo()._getCdnDeployments();
        for(CdnDeploymentHollow deployment : cdnDeployments) {
            originServerNames.add(new Strings(deployment._getOriginServer()._getValue()));
        }
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
