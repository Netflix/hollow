package com.netflix.vms.transformer.modules.countryspecific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractIdHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

public class VMSAvailabilityWindowModule {

    private final TransformerContext ctx;

    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);

    private final LinkedHashSetOfStrings EMPTY_CUP_TOKENS;
    private final LinkedHashSetOfStrings DEFAULT_CUP_TOKENS;

    private Map<Integer, List<PackageData>> transformedPackageData;

    private final WindowPackageContractInfoModule windowPackageContractInfoModule;

    public VMSAvailabilityWindowModule(VMSHollowInputAPI api, TransformerContext ctx, VMSTransformerIndexer indexer) {
        this.ctx = ctx;

        this.windowPackageContractInfoModule = new WindowPackageContractInfoModule(api, ctx, indexer);

        EMPTY_CUP_TOKENS = new LinkedHashSetOfStrings();
        EMPTY_CUP_TOKENS.ordinals = Collections.emptyList();

        DEFAULT_CUP_TOKENS = new LinkedHashSetOfStrings();
        DEFAULT_CUP_TOKENS.ordinals = Collections.singletonList(new Strings("default"));
    }


    public void setTransformedPackageData(Map<Integer, List<PackageData>> data) {
        this.transformedPackageData = data;
    }

    public void populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, VideoRightsHollow videoRights, CountrySpecificRollupValues rollup) {
        boolean isGoLive = isGoLive(videoRights);

        VideoRightsRightsHollow rights = videoRights._getRights();
        if((rollup.doShow() && rollup.wasShowEpisodeFound()) || (rollup.doSeason() && rollup.wasSeasonEpisodeFound())) {
            populateRolledUpWindowData(data, rollup, rights, isGoLive);
        } else {
            populateEpisodeOrStandaloneWindowData(videoId, country, data, rollup, isGoLive, rights);
        }

    }

    private void populateEpisodeOrStandaloneWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup, boolean isGoLive, VideoRightsRightsHollow rights) {
        List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<VMSAvailabilityWindow>();

        long minWindowStartDate = Long.MAX_VALUE;
        VMSAvailabilityWindow currentOrFirstFutureWindow = null;
        boolean isInWindow = false;

        int includedPackageDataCount = 0;

        int maxPackageId = 0;
        int bundledAssetsGroupId = 0; /// the contract ID for the highest package ID across all windows;

        List<VideoRightsWindowHollow> sortedWindows = new ArrayList<VideoRightsWindowHollow>(rights._getWindows());
        Collections.sort(sortedWindows, new Comparator<VideoRightsWindowHollow>() {
            public int compare(VideoRightsWindowHollow o1, VideoRightsWindowHollow o2) {
                return Long.compare(o1._getStartDate()._getValue(), o2._getStartDate()._getValue());
            }
        });

        for(VideoRightsWindowHollow window : sortedWindows) {
            boolean includedWindowPackageData = false;
            int thisWindowMaxPackageId = 0;
            int thisWindowBundledAssetsGroupId = 0;

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
                            // MERGE MULTIPLE CONTRACTS

                            if(!shouldFilterOutWindowInfo(isGoLive, contract, includedPackageDataCount, outputWindow.startDate.val, outputWindow.endDate.val)) {
                                ///merge cup tokens
                                List<Strings> cupTokens = new ArrayList<>();
                                Strings contractCupToken = new Strings(contract._getCupToken()._getValue());
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
                                for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
                                    bcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
                                }


                                windowPackageContractInfo = windowPackageContractInfo.clone();
                                windowPackageContractInfo.videoContractInfo = windowPackageContractInfo.videoContractInfo.clone();
                                windowPackageContractInfo.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(cupTokens);
                                windowPackageContractInfo.videoContractInfo.assetBcp47Codes = bcp47Codes;
                                windowPackageContractInfo.videoContractInfo.contractId = Math.max(windowPackageContractInfo.videoContractInfo.contractId, (int)contractId);
                                windowPackageContractInfo.videoContractInfo.primaryPackageId = (int) Math.max(windowPackageContractInfo.videoContractInfo.primaryPackageId, contract._getPackageId());

                                outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                if(packageId.val == maxPackageId)
                                    bundledAssetsGroupId = Math.max((int)contractId, bundledAssetsGroupId);
                                if(packageId.val == thisWindowMaxPackageId)
                                    thisWindowBundledAssetsGroupId = Math.max((int)contractId, thisWindowBundledAssetsGroupId);
                            }
                        } else {
                            if(shouldFilterOutWindowInfo(isGoLive, contract, includedPackageDataCount, outputWindow.startDate.val, outputWindow.endDate.val)) {
                                outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractId));

                                if(maxPackageId == 0) {
                                    bundledAssetsGroupId = (int)contractId;
                                    thisWindowBundledAssetsGroupId = (int) contractId;
                                }
                            } else {
                                includedWindowPackageData = true;
                                PackageData packageData = getPackageData(videoId, pkg._getPackageId());
                                if(packageData != null) {
                                    /// package data is available
                                    windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfo(packageData, contract, country);
                                    outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                    if(packageData.id >= maxPackageId) {
                                        maxPackageId = packageData.id;
                                        bundledAssetsGroupId = (int)contractId;
                                    }

                                    if(packageData.id >= thisWindowMaxPackageId) {
                                        thisWindowMaxPackageId = packageData.id;
                                        thisWindowBundledAssetsGroupId = (int)contractId;
                                    }

                                } else {
                                    /// packagedata not available -- use the contract only
                                    windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(contract, country);
                                    outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfo);

                                    if(maxPackageId == 0) {
                                        bundledAssetsGroupId = (int)contractId;
                                        thisWindowBundledAssetsGroupId = (int) contractId;
                                    }
                                }


                                if(window._getEndDate()._getValue() > ctx.getNowMillis() && window._getStartDate()._getValue() < minWindowStartDate) {
                                    minWindowStartDate = window._getStartDate()._getValue();
                                    currentOrFirstFutureWindow = outputWindow;

                                    if(isGoLive && window._getStartDate()._getValue() < ctx.getNowMillis())
                                        isInWindow = true;
                                }
                            }
                        }

                    }
                } else {
                    outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractIdHollow._getValue()));

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
            LinkedHashSetOfStrings cupTokens = null;
            Map<Strings, List<VideoImage>> stillImagesByTypeMap = null;
            Map<Strings, List<VideoImage>> stillImagesByTypeMapForShowLevelExtraction = null;

            for(Map.Entry<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo> entry : currentOrFirstFutureWindow.windowInfosByPackageId.entrySet()) {
                if(entry.getKey().val > maxPackageId) {
                    maxPackageId = entry.getKey().val;
                    assetBcp47CodesFromMaxPackageId = entry.getValue().videoContractInfo.assetBcp47Codes;
                    videoFormatDescriptorsFromMaxPackageId = entry.getValue().videoPackageInfo.formats;
                    prePromoDays = entry.getValue().videoContractInfo.prePromotionDays;
                    cupTokens = entry.getValue().videoContractInfo.cupTokens;
                    if(isGoLive) {
                    	if(isInWindow)
                    		stillImagesByTypeMap = entry.getValue().videoPackageInfo.stillImagesMap;
                    	else
                    		stillImagesByTypeMapForShowLevelExtraction = entry.getValue().videoPackageInfo.stillImagesMap;
                    }
                }
            }

            rollup.newAssetBcp47Codes(assetBcp47CodesFromMaxPackageId);
            rollup.newVideoFormatDescriptors(videoFormatDescriptorsFromMaxPackageId);
            rollup.newPrePromoDays(prePromoDays);
            if(isGoLive && isInWindow)
                rollup.newCupTokens(cupTokens);
            if(stillImagesByTypeMap != null)
            	rollup.newEpisodeStillImagesByTypeMap(stillImagesByTypeMap);
            else if (stillImagesByTypeMapForShowLevelExtraction != null)
            	rollup.newEpisodeStillImagesByTypeMapForShowLevelExtraction(stillImagesByTypeMapForShowLevelExtraction);
        }

        rollup.newEpisodeData(isGoLive, bundledAssetsGroupId);

        data.mediaAvailabilityWindows = availabilityWindows;
        data.imagesAvailabilityWindows = availabilityWindows;
    }


	private void populateRolledUpWindowData(CompleteVideoCountrySpecificData data, CountrySpecificRollupValues rollup, VideoRightsRightsHollow rights, boolean isGoLive) {
		Set<VideoRightsWindowHollow> windows = rights._getWindows();

		if(windows.isEmpty()) {
		    data.mediaAvailabilityWindows = Collections.emptyList();
		    data.imagesAvailabilityWindows = Collections.emptyList();
		} else {

		    long minStartDate = Long.MAX_VALUE;
		    long maxEndDate = 0;
		    boolean isInWindow = false;

		    for(VideoRightsWindowHollow window : windows) {
		        long startDate = window._getStartDate()._getValue();
		        long endDate = window._getEndDate()._getValue();
		        if(startDate < minStartDate)
		            minStartDate = startDate;
		        if(endDate > maxEndDate)
		            maxEndDate = endDate;

		        if(startDate < ctx.getNowMillis() && endDate > ctx.getNowMillis())
		        	isInWindow = true;

		        ////TODO: What was the logic for this before?
		        if(rollup.doSeason() && startDate < (ctx.getNowMillis() + (7 * 24 * 60 * 60 * 1000))) {
		            rollup.newSeasonWindow(startDate, endDate, rollup.getSeasonSequenceNumber());
		        }
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

		    videoImagesContractInfo.videoContractInfo.cupTokens = EMPTY_CUP_TOKENS;
		    videoMediaContractInfo.videoContractInfo.assetBcp47Codes = rollup.getAssetBcp47Codes();
		    if(rollup.getPrePromoDays() != 0)
		        videoMediaContractInfo.videoContractInfo.prePromotionDays = rollup.getPrePromoDays();
		    videoMediaContractInfo.videoContractInfo.postPromotionDays = 0;
		    videoMediaContractInfo.videoContractInfo.cupTokens = rollup.getCupTokens() != null ? rollup.getCupTokens() : DEFAULT_CUP_TOKENS;
		    videoMediaContractInfo.videoPackageInfo.formats = rollup.getVideoFormatDescriptors();

		    if(isGoLive && isInWindow)
		    	videoImagesContractInfo.videoPackageInfo.stillImagesMap = rollup.getVideoImageMap();

		    data.mediaAvailabilityWindows = Collections.singletonList(videoMediaAvailabilityWindow);
		    data.imagesAvailabilityWindows = Collections.singletonList(videoImagesAvailabilityWindow);
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

    private static final long FUTURE_CUTOFF_IN_MILLIS = 360L * 24L * 60L * 60L * 1000L;

    private boolean shouldFilterOutWindowInfo(boolean isGoLive, VideoRightsContractHollow contract, int unfilteredCount, long startDate, long endDate) {
        if(endDate < ctx.getNowMillis())
        	return true;

        if(!isGoLive) {
        	if(!contract._getDayAfterBroadcast() && contract._getPrePromotionDays() <= 0)
        		return true;
        }

        if(unfilteredCount < 3 && endDate > ctx.getNowMillis())
        	return false;

        if(startDate > ctx.getNowMillis() + FUTURE_CUTOFF_IN_MILLIS)
        	return true;

    	return false;
    }


    private boolean isGoLive(VideoRightsHollow rights) {
        VideoRightsFlagsHollow flags = rights._getFlags();
        return flags != null && flags._getGoLive();
    }

    public void reset() {
        this.transformedPackageData = null;
        this.windowPackageContractInfoModule.reset();
    }

}
