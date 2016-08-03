package com.netflix.vms.transformer.modules.countryspecific;

import static com.netflix.vms.transformer.util.OutputUtil.minValueToZero;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.RightsHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.VideoPackageData;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.RightsWindowContract;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.VideoContractUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VMSAvailabilityWindowModule {

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final VMSTransformerIndexer indexer;
    private final HollowPrimaryKeyIndex videoGeneralIdx;

    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);

    private final Strings DEFAULT_CUP_TOKEN = new Strings(CupKey.DEFAULT);
    private final LinkedHashSetOfStrings EMPTY_CUP_TOKENS;
    private final LinkedHashSetOfStrings DEFAULT_CUP_TOKENS;

    private Map<Integer, VideoPackageData> transformedPackageData;

    private final WindowPackageContractInfoModule windowPackageContractInfoModule;

    public VMSAvailabilityWindowModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        this.api = api;
        this.ctx = ctx;
        this.indexer = indexer;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);

        this.windowPackageContractInfoModule = new WindowPackageContractInfoModule(api, ctx, cycleConstants, indexer);

        EMPTY_CUP_TOKENS = new LinkedHashSetOfStrings();
        EMPTY_CUP_TOKENS.ordinals = Collections.emptyList();

        DEFAULT_CUP_TOKENS = new LinkedHashSetOfStrings();
        DEFAULT_CUP_TOKENS.ordinals = Collections.singletonList(DEFAULT_CUP_TOKEN);
    }


    public void setTransformedPackageData(Map<Integer, VideoPackageData> data) {
        this.transformedPackageData = data;
    }

    public List<VMSAvailabilityWindow> populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, StatusHollow videoRights, CountrySpecificRollupValues rollup) {
        boolean isGoLive = isGoLive(videoRights);

        RightsHollow rights = videoRights._getRights();
        if((rollup.doShow() && rollup.wasShowEpisodeFound()) || (rollup.doSeason() && rollup.wasSeasonEpisodeFound())) {
            populateRolledUpWindowData(videoId, data, rollup, rights, isGoLive);
        } else {
            populateEpisodeOrStandaloneWindowData(videoId, country, data, rollup, isGoLive, rights);
        }

        return data.mediaAvailabilityWindows;
    }

    private void populateEpisodeOrStandaloneWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup, boolean isGoLive, RightsHollow rights) {
        List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<VMSAvailabilityWindow>();

        long minWindowStartDate = Long.MAX_VALUE;
        VMSAvailabilityWindow currentOrFirstFutureWindow = null;
        boolean isInWindow = false;

        int includedPackageDataCount = 0;

        int maxPackageId = 0;
        int bundledAssetsGroupId = 0; /// the contract ID for the highest package ID across all windows;

        List<RightsWindowHollow> sortedWindows = new ArrayList<RightsWindowHollow>(rights._getWindows());
        Collections.sort(sortedWindows, new Comparator<RightsWindowHollow>() {
            @Override
            public int compare(RightsWindowHollow o1, RightsWindowHollow o2) {
                return Long.compare(o1._getStartDate(), o2._getStartDate());
            }
        });

        //Map<Long, ContractHollow> fullContractMap = VideoContractUtil.getContractMap(api, indexer, videoId, country);

        ///TODO: Find some way to simplify this logic.
        for (RightsWindowHollow window : sortedWindows) {
            boolean includedWindowPackageData = false;
            int thisWindowMaxPackageId = 0;
            int thisWindowBundledAssetsGroupId = 0;

            VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
            outputWindow.startDate = OutputUtil.getRoundedDate(window._getStartDate());
            outputWindow.endDate = OutputUtil.getRoundedDate(window._getEndDate());
            outputWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();

            if(isGoLive && rollup.doEpisode()) {
                rollup.newSeasonWindow(window._getStartDate(), window._getEndDate(), rollup.getSeasonSequenceNumber());
            }

            LinkedHashMap<Long, RightsWindowContract> rightsContractMap = getRightsContractMap(rights, window);
            boolean shouldFilterOutWindowInfo = shouldFilterOutWindowInfo(videoId, country, isGoLive, rightsContractMap.keySet(), includedPackageDataCount, outputWindow.startDate.val, outputWindow.endDate.val);

            for (Map.Entry<Long, RightsWindowContract> entry : rightsContractMap.entrySet()) {
                long contractId = entry.getKey();
                RightsContractHollow rightsContract = entry.getValue().contract;
                ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, country, contractId);
                boolean isAvailableForDownload = entry.getValue().isAvailableForDownload;

                if(rightsContract != null) {
                    ListOfRightsContractPackageHollow packageIdList = rightsContract._getPackages();

                    if(packageIdList != null && !packageIdList.isEmpty()) {

                        for (RightsContractPackageHollow pkg : packageIdList) {
                            com.netflix.vms.transformer.hollowoutput.Integer packageId = new com.netflix.vms.transformer.hollowoutput.Integer((int)pkg._getPackageId());

                            WindowPackageContractInfo windowPackageContractInfo = outputWindow.windowInfosByPackageId.get(packageId);
                            if(windowPackageContractInfo != null) {
                                // MERGE MULTIPLE CONTRACTS

                                if(shouldFilterOutWindowInfo) {
                                    if(contractId > windowPackageContractInfo.videoContractInfo.contractId) {
                                        windowPackageContractInfo.videoContractInfo.contractId = (int)contractId;
                                        if(packageId.val == maxPackageId)
                                            bundledAssetsGroupId = Math.max((int)contractId, bundledAssetsGroupId);
                                        if(packageId.val == thisWindowMaxPackageId)
                                            thisWindowBundledAssetsGroupId = Math.max((int)contractId, thisWindowBundledAssetsGroupId);
                                    }

                                } else {
                                    ///merge cup tokens
                                    List<Strings> cupTokens = new ArrayList<>();
                                    Strings contractCupToken = contract == null ? DEFAULT_CUP_TOKEN : new Strings(contract._getCupToken()._getValue());
                                    if(windowPackageContractInfo.videoContractInfo.contractId > contractId) {
                                        cupTokens.addAll(windowPackageContractInfo.videoContractInfo.cupTokens.ordinals);
                                        if(!cupTokens.contains(contractCupToken))
                                            cupTokens.add(contractCupToken);
                                    } else {
                                        cupTokens.add(contractCupToken);
                                        for(Strings cupToken : windowPackageContractInfo.videoContractInfo.cupTokens.ordinals) {
                                            if(!cupToken.equals(contractCupToken))
                                                cupTokens.add(cupToken);
                                        }
                                    }

                                    ///merge bcp47 codes
                                    Set<Strings> bcp47Codes = new HashSet<Strings>(windowPackageContractInfo.videoContractInfo.assetBcp47Codes);
                                    for (RightsContractAssetHollow asset : rightsContract._getAssets()) {
                                        bcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
                                    }

                                    windowPackageContractInfo = windowPackageContractInfo.clone();
                                    windowPackageContractInfo.videoContractInfo = windowPackageContractInfo.videoContractInfo.clone();
                                    windowPackageContractInfo.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(cupTokens);
                                    windowPackageContractInfo.videoContractInfo.assetBcp47Codes = bcp47Codes;
                                    windowPackageContractInfo.videoContractInfo.contractId = Math.max(windowPackageContractInfo.videoContractInfo.contractId, (int)contractId);
                                    windowPackageContractInfo.videoContractInfo.isAvailableForDownload = windowPackageContractInfo.videoContractInfo.isAvailableForDownload || isAvailableForDownload;
                                    windowPackageContractInfo.videoContractInfo.primaryPackageId = (int) Math.max(windowPackageContractInfo.videoContractInfo.primaryPackageId, rightsContract._getPackageId());

                                    outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                    if(packageId.val == maxPackageId)
                                        bundledAssetsGroupId = Math.max((int)contractId, bundledAssetsGroupId);
                                    if(packageId.val == thisWindowMaxPackageId)
                                        thisWindowBundledAssetsGroupId = Math.max((int)contractId, thisWindowBundledAssetsGroupId);
                                }
                            } else {
                                if(shouldFilterOutWindowInfo) {
                                    WindowPackageContractInfo alreadyFilteredWindowPackageContractInfo = outputWindow.windowInfosByPackageId.get(ZERO);
                                    if(alreadyFilteredWindowPackageContractInfo != null) {
                                        if(alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId < (int)contractId)
                                            alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId = (int)contractId;
                                    } else {
                                        outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractId, videoId));
                                    }

                                    if(maxPackageId == 0)
                                        bundledAssetsGroupId = Math.max(bundledAssetsGroupId, (int)contractId);

                                    if(thisWindowMaxPackageId == 0)
                                        thisWindowBundledAssetsGroupId = Math.max(thisWindowBundledAssetsGroupId, (int)contractId);
                                } else {
                                    includedWindowPackageData = true;
                                    PackageData packageData = getPackageData(videoId, pkg._getPackageId());
                                    if(packageData != null) {
                                        /// package data is available
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfo(packageData, rightsContract, contract, country, isAvailableForDownload);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                        if(packageData.id > maxPackageId) {
                                            maxPackageId = packageData.id;
                                            bundledAssetsGroupId = (int)contractId;
                                        }

                                        if(packageData.id > thisWindowMaxPackageId) {
                                            thisWindowMaxPackageId = packageData.id;
                                            thisWindowBundledAssetsGroupId = (int)contractId;
                                        }

                                    } else {
                                        /// packagedata not available -- use the contract only
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(packageId.val, rightsContract, contract, country, videoId);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                        if(thisWindowMaxPackageId == 0)
                                            thisWindowBundledAssetsGroupId = Math.max((int)contractId, thisWindowBundledAssetsGroupId);
                                        if(maxPackageId == 0)
                                            bundledAssetsGroupId = Math.max((int)contractId, bundledAssetsGroupId);
                                    }
                                }

                                if (window._getEndDate() > ctx.getNowMillis() && window._getStartDate() < minWindowStartDate) {
                                    minWindowStartDate = window._getStartDate();
                                    currentOrFirstFutureWindow = outputWindow;

                                    if (isGoLive && window._getStartDate() < ctx.getNowMillis())
                                        isInWindow = true;
                                }
                            }

                        }

                    } else {
                        /// packageIdList was empty -- packagedata not available -- use the contract only
                        WindowPackageContractInfo windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(0, rightsContract, contract, country, videoId);
                        outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfo);

                        if(thisWindowMaxPackageId == 0)
                            thisWindowBundledAssetsGroupId = Math.max((int)contractId, thisWindowBundledAssetsGroupId);
                        if(maxPackageId == 0)
                            bundledAssetsGroupId = Math.max((int)contractId, bundledAssetsGroupId);
                    }
                } else {
                    outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractId, videoId));

                    if(maxPackageId == 0) {
                        bundledAssetsGroupId = (int)contractId;
                        thisWindowBundledAssetsGroupId = (int) contractId;
                    }
                }
            }

            outputWindow.bundledAssetsGroupId = thisWindowBundledAssetsGroupId;

            availabilityWindows.add(outputWindow);

            if(includedWindowPackageData)
                includedPackageDataCount++;

        }


        if(currentOrFirstFutureWindow != null) {
            maxPackageId = Integer.MIN_VALUE;
            Set<Strings> assetBcp47CodesFromMaxPackageId = null;
            Set<VideoFormatDescriptor> videoFormatDescriptorsFromMaxPackageId = null;
            int prePromoDays = 0;
            boolean hasRollingEpisodes = false;
            boolean isAvailableForDownload = false;
            LinkedHashSetOfStrings cupTokens = null;
            Map<Strings, List<VideoImage>> stillImagesByTypeMap = null;
            Map<Strings, List<VideoImage>> stillImagesByTypeMapForShowLevelExtraction = null;

            for(Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : currentOrFirstFutureWindow.windowInfosByPackageId.entrySet()) {
                if(entry.getKey().val > maxPackageId) {
                    maxPackageId = entry.getKey().val;
                    assetBcp47CodesFromMaxPackageId = entry.getValue().videoContractInfo.assetBcp47Codes;
                    videoFormatDescriptorsFromMaxPackageId = entry.getValue().videoPackageInfo.formats;
                    prePromoDays = minValueToZero(entry.getValue().videoContractInfo.prePromotionDays);
                    hasRollingEpisodes = entry.getValue().videoContractInfo.hasRollingEpisodes;
                    isAvailableForDownload = entry.getValue().videoContractInfo.isAvailableForDownload;
                    cupTokens = entry.getValue().videoContractInfo.cupTokens;
                    if(isGoLive && isInWindow)
                        stillImagesByTypeMap = entry.getValue().videoPackageInfo.stillImagesMap;
                    else
                        stillImagesByTypeMapForShowLevelExtraction = entry.getValue().videoPackageInfo.stillImagesMap;
                }
            }

            rollup.newAssetBcp47Codes(assetBcp47CodesFromMaxPackageId);
            rollup.newPrePromoDays(prePromoDays);

            if(hasRollingEpisodes)
                rollup.foundRollingEpisodes();
            if(isAvailableForDownload)
                rollup.foundAvailableForDownload();

            if(isGoLive && isInWindow) {
                rollup.newVideoFormatDescriptors(videoFormatDescriptorsFromMaxPackageId);
                rollup.newCupTokens(cupTokens);
            }

            if(stillImagesByTypeMap != null)
                rollup.newEpisodeStillImagesByTypeMap(stillImagesByTypeMap);
            else if (stillImagesByTypeMapForShowLevelExtraction != null)
                rollup.newEpisodeStillImagesByTypeMapForShowLevelExtraction(stillImagesByTypeMapForShowLevelExtraction);

            rollup.newEpisodeData(isGoLive, currentOrFirstFutureWindow.bundledAssetsGroupId);
        } else {
            rollup.newEpisodeData(isGoLive, bundledAssetsGroupId);
            if(rollup.doEpisode())
                rollup.newPrePromoDays(0);
        }

        data.mediaAvailabilityWindows = availabilityWindows;
        data.imagesAvailabilityWindows = availabilityWindows;
    }

    private final LinkedHashMap<Long, RightsWindowContract> theRightsContractMap = new LinkedHashMap<>();
    private LinkedHashMap<Long, RightsWindowContract> getRightsContractMap(RightsHollow rights, RightsWindowHollow window) {
        theRightsContractMap.clear();
        for (RightsWindowContractHollow rightsWindowContract : window._getContractIdsExt()) {
            long contractId = rightsWindowContract._getContractId();
            RightsContractHollow contract = getRightContract(rights, contractId);
            theRightsContractMap.put(contractId, new RightsWindowContract(contractId, contract, rightsWindowContract._getDownload()));
        }

        return theRightsContractMap;
    }

    private static RightsContractHollow getRightContract(RightsHollow rights, long contractId) {
        for (RightsContractHollow contract : rights._getContracts()) {
            if (contract._getContractId() == contractId)
                return contract;
        }
        return null;
    }

    // Return AvailabilityWindow from MediaData
    private void populateRolledUpWindowData(Integer videoId, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup, RightsHollow rights, boolean isGoLive) {
        ListOfRightsWindowHollow windows = rights._getWindows();

        if(windows.isEmpty()) {
            data.mediaAvailabilityWindows = Collections.emptyList();
            data.imagesAvailabilityWindows = Collections.emptyList();
        } else {

            long minStartDate = Long.MAX_VALUE;
            long maxEndDate = 0;
            boolean isInWindow = false;

            int maxContractId = Integer.MIN_VALUE;

            for (RightsWindowHollow window : windows) {
                long startDate = window._getStartDate();
                long endDate = window._getEndDate();
                if(startDate < minStartDate)
                    minStartDate = startDate;
                if(endDate > maxEndDate)
                    maxEndDate = endDate;

                if(startDate < ctx.getNowMillis() && endDate > ctx.getNowMillis())
                    isInWindow = true;

                for (RightsWindowContractHollow rightsWindowContract : window._getContractIdsExt()) {
                    if((int)rightsWindowContract._getContractId() > maxContractId)
                        maxContractId = (int)rightsWindowContract._getContractId();
                }
            }

            VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
            outputWindow.startDate = OutputUtil.getRoundedDate(minStartDate);
            outputWindow.endDate = OutputUtil.getRoundedDate(maxEndDate);
            outputWindow.bundledAssetsGroupId = maxContractId; //rollup.getFirstEpisodeBundledAssetId();

            WindowPackageContractInfo videoImagesContractInfo = createEmptyContractInfoForRollup(outputWindow);
            WindowPackageContractInfo videoMediaContractInfo = createEmptyContractInfoForRollup(outputWindow);

            VMSAvailabilityWindow videoImagesAvailabilityWindow = outputWindow.clone();
            VMSAvailabilityWindow videoMediaAvailabilityWindow = outputWindow.clone();


            videoImagesAvailabilityWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();
            videoMediaAvailabilityWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();

            videoImagesAvailabilityWindow.windowInfosByPackageId.put(ZERO, videoImagesContractInfo);
            videoMediaAvailabilityWindow.windowInfosByPackageId.put(ZERO, videoMediaContractInfo);

            videoImagesContractInfo.videoContractInfo.cupTokens = EMPTY_CUP_TOKENS;
            videoImagesContractInfo.videoContractInfo.isAvailableForDownload = rollup.isAvailableForDownload();
            videoMediaContractInfo.videoContractInfo.assetBcp47Codes = rollup.getAssetBcp47Codes();
            videoMediaContractInfo.videoContractInfo.prePromotionDays = rollup.getPrePromoDays();
            videoMediaContractInfo.videoContractInfo.isDayAfterBroadcast = rollup.hasRollingEpisodes();
            videoMediaContractInfo.videoContractInfo.hasRollingEpisodes = rollup.hasRollingEpisodes();
            videoMediaContractInfo.videoContractInfo.isAvailableForDownload = rollup.isAvailableForDownload();
            videoMediaContractInfo.videoContractInfo.postPromotionDays = 0;
            videoMediaContractInfo.videoContractInfo.cupTokens = rollup.getCupTokens() != null ? rollup.getCupTokens() : DEFAULT_CUP_TOKENS;
            videoMediaContractInfo.videoPackageInfo.formats = rollup.getVideoFormatDescriptors();

            if(rollup.getFirstEpisodeBundledAssetId() != 0) {
                videoMediaAvailabilityWindow.bundledAssetsGroupId = rollup.getFirstEpisodeBundledAssetId();
                videoMediaContractInfo.videoContractInfo.contractId = rollup.getFirstEpisodeBundledAssetId();
            }

            if(isGoLive && isInWindow)
                videoImagesContractInfo.videoPackageInfo.stillImagesMap = rollup.getVideoImageMap();
            else
                videoMediaContractInfo.videoPackageInfo.formats = Collections.emptySet();  ///TODO: This seems totally unnecessary.  We should remove this line after parity testing.

            int videoGeneralOrdinal = videoGeneralIdx.getMatchingOrdinal(Long.valueOf(videoId.intValue()));
            if(videoGeneralOrdinal != -1) {
                VideoGeneralHollow general = api.getVideoGeneralHollow(videoGeneralOrdinal);
                long runtime = general._getRuntime();
                if(runtime != Long.MIN_VALUE)
                    videoImagesContractInfo.videoPackageInfo.runtimeInSeconds = (int)runtime;
            }

            data.mediaAvailabilityWindows = Collections.singletonList(videoMediaAvailabilityWindow);
            data.imagesAvailabilityWindows = Collections.singletonList(videoImagesAvailabilityWindow);
        }
    }

    private WindowPackageContractInfo createEmptyContractInfoForRollup(VMSAvailabilityWindow outputWindow) {
        WindowPackageContractInfo contractInfo = new WindowPackageContractInfo();
        contractInfo.videoContractInfo = new VideoContractInfo();
        contractInfo.videoContractInfo.contractId = outputWindow.bundledAssetsGroupId;
        contractInfo.videoContractInfo.primaryPackageId = 0;
        contractInfo.videoContractInfo.assetBcp47Codes = Collections.emptySet();
        contractInfo.videoContractInfo.cupTokens = EMPTY_CUP_TOKENS;

        contractInfo.videoPackageInfo = WindowPackageContractInfoModule.newEmptyVideoPackageInfo();
        return contractInfo;
    }


    private PackageData getPackageData(Integer videoId, long packageId) {
        VideoPackageData vpData = transformedPackageData.get(videoId);
        if (vpData == null) return null;

        Set<PackageData> set = vpData.packages;
        if(set == null)
            return null;

        for(PackageData packageData : set) {
            if(packageData.id == packageId)
                return packageData;
        }

        return null;
    }

    private static final long FUTURE_CUTOFF_IN_MILLIS = 360L * 24L * 60L * 60L * 1000L;

    private boolean shouldFilterOutWindowInfo(long videoId, String countryCode, boolean isGoLive, Collection<Long> contractIds, int unfilteredCount, long startDate, long endDate) {
        if(endDate < ctx.getNowMillis())
            return true;

        if(!isGoLive) {
            boolean isWindowDataNeeded = false;
            for (Long contractId : contractIds) {
                ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, contractId);
                if(contract != null && (contract._getDayAfterBroadcast() || contract._getPrePromotionDays() > 0)) {
                    isWindowDataNeeded = true;
                }
            }

            if(!isWindowDataNeeded)
                return true;
        }

        if(unfilteredCount < 3 && endDate > ctx.getNowMillis())
            return false;

        if(startDate > ctx.getNowMillis() + FUTURE_CUTOFF_IN_MILLIS)
            return true;

        return false;
    }


    private boolean isGoLive(StatusHollow status) {
        FlagsHollow flags = status._getFlags();
        return flags != null && flags._getGoLive();
    }

    public void reset() {
        this.transformedPackageData = null;
        this.windowPackageContractInfoModule.reset();
    }
}
