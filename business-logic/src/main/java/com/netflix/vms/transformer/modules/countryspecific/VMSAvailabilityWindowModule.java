package com.netflix.vms.transformer.modules.countryspecific;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InteractivePackage;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_Catalog_Title_Availability;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_NoWindows;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_PrePromote;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_Skip_Contract_No_Assets;
import static com.netflix.vms.transformer.util.OutputUtil.minValueToZero;

import com.google.common.collect.Sets;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.CycleDataAggregator;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.contract.ContractAssetType;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.data.DeployablePackagesFetcher;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.FeedMovieCountryLanguagesHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.LongHollow;
import com.netflix.vms.transformer.hollowinput.MapOfStringToLongHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.RightsHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.DateWindow;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.PackageDataCollection;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.VideoContractUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class VMSAvailabilityWindowModule {

    public static final long ONE_THOUSAND_YEARS = TimeUnit.DAYS.toMillis(1000L * 365L);
    public static final long MS_IN_DAY = TimeUnit.DAYS.toMillis(1);
    public static final long FUTURE_CUT_OFF_FOR_REPORT = TimeUnit.DAYS.toMillis(30);
    private static final long FUTURE_CUTOFF_IN_MILLIS = TimeUnit.DAYS.toMillis(365L);

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final VMSTransformerIndexer indexer;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowPrimaryKeyIndex merchLanguageDateIdx;

    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);

    private final LinkedHashSetOfStrings EMPTY_CUP_TOKENS;
    private final LinkedHashSetOfStrings DEFAULT_CUP_TOKENS;

    private TransformedVideoData transformedVideoData;

    private final WindowPackageContractInfoModule windowPackageContractInfoModule;
    private final MultilanguageCountryWindowFilter multilanguageCountryWindowFilter;
    private final CycleDataAggregator cycleDataAggregator;
    private final CupTokenFetcher cupTokenFetcher;

    public VMSAvailabilityWindowModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants,
            VMSTransformerIndexer indexer, CycleDataAggregator cycleDataAggregator,
            CupTokenFetcher cupTokenFetcher, DeployablePackagesFetcher deployablePackagesFetcher) {
        this.api = api;
        this.ctx = ctx;
        this.indexer = indexer;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.merchLanguageDateIdx = indexer.getPrimaryKeyIndex(IndexSpec.MERCH_LANGUAGE_DATE);
        this.cupTokenFetcher = cupTokenFetcher;

        this.windowPackageContractInfoModule = new WindowPackageContractInfoModule(api, indexer, cupTokenFetcher,
                deployablePackagesFetcher, ctx);
        this.multilanguageCountryWindowFilter = new MultilanguageCountryWindowFilter(cycleConstants);
        this.cycleDataAggregator = cycleDataAggregator;

        EMPTY_CUP_TOKENS = new LinkedHashSetOfStrings();
        EMPTY_CUP_TOKENS.ordinals = Collections.emptyList();

        DEFAULT_CUP_TOKENS = new LinkedHashSetOfStrings();
        DEFAULT_CUP_TOKENS.ordinals = Collections.singletonList(new Strings(CupKey.DEFAULT));
    }


    public void setTransformedVideoData(TransformedVideoData transformedVideoData) {
        this.transformedVideoData = transformedVideoData;
    }

    public List<VMSAvailabilityWindow> populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, StatusHollow videoRights, CountrySpecificRollupValues rollup) {
        boolean isGoLive = isGoLive(videoRights);

        List<VMSAvailabilityWindow> windows = calculateWindowData(videoId, country, null, videoRights, rollup, isGoLive);

        data.availabilityWindows = windows;

        return windows;
    }

    List<VMSAvailabilityWindow> calculateWindowData(Integer videoId, String country, String language, StatusHollow videoRights, CountrySpecificRollupValues rollup, boolean isGoLive) {
        List<VMSAvailabilityWindow> windows = null;

        RightsHollow rights = videoRights._getRights();
        if ((rollup.doShow() && rollup.wasShowEpisodeFound()) || (rollup.doSeason() && rollup.wasSeasonEpisodeFound())) {
            windows = populateRolledUpWindowData(videoId, rollup, rights, isGoLive, language != null);
        } else {
            windows = populateEpisodeOrStandaloneWindowData(videoId, country, language, rollup, isGoLive, rights, language != null, videoRights);
            if (language != null && windows.isEmpty() && isLanguageOverride(videoRights))
                windows = populateEpisodeOrStandaloneWindowData(videoId, country, null, rollup, isGoLive, rights, language != null, videoRights);
        }
        return windows;
    }

    private VMSAvailabilityWindow newVMSAvailabilityWindow(RightsWindowHollow rightsWindowHollow) {
        VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
        outputWindow.windowInfosByPackageId = new HashMap<>();
        outputWindow.startDate = OutputUtil.getRoundedDate(rightsWindowHollow._getStartDate());
        outputWindow.endDate = OutputUtil.getRoundedDate(rightsWindowHollow._getEndDate());
        if (rightsWindowHollow._getOnHold()) {
            outputWindow.startDate.val += ONE_THOUSAND_YEARS;
            outputWindow.endDate.val += ONE_THOUSAND_YEARS;
            outputWindow.onHold = true;
        }
        return outputWindow;
    }

    WindowPackageContractInfo cloneWindowPackageContractInfo(int videoId, WindowPackageContractInfo existingInfo, long contractId, ContractHollow contractData, List<RightsContractAssetHollow> contractAssets, boolean isAvailableForDownload, int packageId) {

        // 1. merge cup token values
        List<Strings> cupTokens = new ArrayList<>();
        Strings contractCupToken = cupTokenFetcher.getCupToken(videoId, contractData);
        if (existingInfo.videoContractInfo.contractId > contractId) {
            cupTokens.addAll(existingInfo.videoContractInfo.cupTokens.ordinals);
            if (!cupTokens.contains(contractCupToken)) cupTokens.add(contractCupToken);
        } else {
            cupTokens.add(contractCupToken);
            for (Strings cupToken : existingInfo.videoContractInfo.cupTokens.ordinals) {
            	if (!cupToken.equals(contractCupToken)) cupTokens.add(cupToken);
            }
        }
        // 2. merge bcp47 code values
        Set<Strings> bcp47Codes = new HashSet<>(existingInfo.videoContractInfo.assetBcp47Codes);
        for (RightsContractAssetHollow asset : contractAssets) {
            bcp47Codes.add(new Strings(asset._getBcp47Code()._getValue()));
        }

        // 3. clone the exiting contract info, and assign all new values
        WindowPackageContractInfo clone = existingInfo.clone();
        clone.videoContractInfo = existingInfo.videoContractInfo.clone();
        clone.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(cupTokens);
        clone.videoContractInfo.assetBcp47Codes = bcp47Codes;

        // update the rest
        // 4. update the map windowInfoByPackageId with current packageId with cloned windowPackageContractInfo
        // 5. the primary package id for the contractInfo is the max packageId for previous and current packageId
        // 6. the contract id for the contractInfo, is the max contractId from the previous and current contractId
        clone.videoContractInfo.contractId = Math.max(existingInfo.videoContractInfo.contractId, (int) contractId);
        clone.videoContractInfo.isAvailableForDownload = existingInfo.videoContractInfo.isAvailableForDownload || isAvailableForDownload;
        clone.videoContractInfo.primaryPackageId = (int) Math.max(existingInfo.videoContractInfo.primaryPackageId, packageId);

        return clone;
    }

    // todo need to split this out in a separate class. It's humongous. Not good. I promise to improve this one day.
    private List<VMSAvailabilityWindow> populateEpisodeOrStandaloneWindowData(Integer videoId, String country, String language, CountrySpecificRollupValues rollup, boolean isGoLive, RightsHollow rights, boolean isMulticatalogRollup, StatusHollow statusHollow) {

        List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<>();
        VMSAvailabilityWindow currentOrFirstFutureWindow = null;

        long minWindowStartDate = Long.MAX_VALUE;

        boolean currentOrFirstFutureWindowFoundLocalAudio = false;
        boolean currentOrFirstFutureWindowFoundLocalText = false;
        boolean isInWindow = false;

        int includedPackageDataCount = 0;
        int maxPackageId = 0;
        int contractIdForMaxPackageId = 0;

        List<RightsWindowHollow> sortedWindows = new ArrayList<>(rights._getWindows());
        Collections.sort(sortedWindows, RIGHTS_WINDOW_COMPARATOR);

        Set<String> grandfatherLanguages = Sets.newHashSet();
        if (language != null && statusHollow._getFlags()!= null && statusHollow._getFlags()._getGrandfatheredLanguages() != null) {
            Iterator<StringHollow> it = statusHollow._getFlags()._getGrandfatheredLanguages().iterator();
            while (it.hasNext()) grandfatherLanguages.add(it.next()._getValue());
        }
        boolean grandfatherEnabled = ctx.getConfig().isGrandfatherEnabled();

        for (RightsWindowHollow window : sortedWindows) {

            int packageIdForWindow = 0;
            int thisWindowBundledAssetsGroupId = 0;

            boolean includedWindowPackageData = false;
            boolean thisWindowFoundLocalAudio = false;
            boolean thisWindowFoundLocalText = false;

            // create new window
            VMSAvailabilityWindow outputWindow = newVMSAvailabilityWindow(window);

            // collect all contracts for the window
            List<RightsWindowContractHollow> windowContracts = new ArrayList<>();
            if (window._getContractIdsExt() != null)
                windowContracts = window._getContractIdsExt().stream().collect(Collectors.toList());
            List<Long> contractIds = new ArrayList<>();
            if(ctx.getConfig().isUseContractIdInsteadOfDealId()) {
                contractIds = windowContracts.stream().map(c -> c._getContractId()).collect(Collectors.toList());            	
            } else {
                contractIds = windowContracts.stream().map(c -> c._getDealId()).collect(Collectors.toList());            	
            }

            // should use window data? Checks isGoLive flag, start/end dates and if video is ready for pre-promotion (is locale aware)
            boolean shouldFilterOutWindowInfo = shouldFilterOutWindowInfo(videoId, country, language, isGoLive, contractIds, includedPackageDataCount,
                                                                          outputWindow.startDate.val, outputWindow.endDate.val);
            

            for (RightsWindowContractHollow windowContractHollow : windowContracts) {

                // get contract id from window contract and contract data from VideoContract feed.
                long contractId = (ctx.getConfig().isUseContractIdInsteadOfDealId()) ? windowContractHollow._getContractId() : windowContractHollow._getDealId();
                ContractHollow contractData = VideoContractUtil.getContract(api, indexer, ctx, videoId, country, contractId);
                
                boolean isAvailableForDownload = windowContractHollow._getDownload();


                // check if there are any assets & packages associated with this contract
                if (windowContractHollow._getPackageIdBoxed() != null || (windowContractHollow._getAssets() != null && !windowContractHollow._getAssets().isEmpty()) || (windowContractHollow._getPackages() != null && !windowContractHollow._getPackages().isEmpty())) {

                    // get contract assets rights & and check the availability of assets in the given locale
                    List<RightsContractAssetHollow> contractAssets = new ArrayList<>();
                    if (windowContractHollow._getAssets() != null)
                        contractAssets = windowContractHollow._getAssets().stream().collect(Collectors.toList());
                    long contractAssetAvailability = language == null ? -1 : multilanguageCountryWindowFilter.contractAvailabilityForLanguage(language, contractAssets);

                    // check if package list in the contract is not null or empty
                    if (windowContractHollow._getPackages() != null && !windowContractHollow._getPackages().isEmpty()) {

                        // collect all the contract packages -> A contract could have multiple packages
                        List<RightsContractPackageHollow> contractPackages = windowContractHollow._getPackages().stream().collect(Collectors.toList());
                        for (RightsContractPackageHollow contractPackageHollow : contractPackages) {

                            // create packageId, get packageDataCollection and packageData for the given package in the contract
                            com.netflix.vms.transformer.hollowoutput.Integer packageId = new com.netflix.vms.transformer.hollowoutput.Integer((int) contractPackageHollow._getPackageId());
                            PackageDataCollection packageDataCollection = getPackageDataCollection(videoId, contractPackageHollow._getPackageId());
                            PackageData packageData = null;
                            if (packageDataCollection != null)
                                packageData = packageDataCollection.getPackageData();


                            // Language filtering rules starts here..
                            if (language != null) {
                                long packageAvailability = multilanguageCountryWindowFilter.packageIsAvailableForLanguage(language, packageData, contractAssetAvailability);
                                boolean readyForPrePromotion = readyForPrePromotionInLanguageCatalog(videoId, country, language, contractIds, outputWindow.startDate.val);

                                // if no localized assets available and title is not ready for promotion -> skip the contract
                                if (packageAvailability == 0 && !readyForPrePromotion) {

                                    // check for grandfathering of existing titles in country catalog to continue to be available in new language catalog in that country.
                                    boolean skipContract = true;
                                    if (grandfatherEnabled && grandfatherLanguages.contains(language)) {
                                        skipContract = false;
                                    }

                                    // skip contract, if assets are missing, and no override needed (no grandfathering/back-filling of existing tiles) and title not ready for pre-promotion.
                                    if (skipContract) {
                                        cycleDataAggregator.collect(country, language, videoId, Language_catalog_Skip_Contract_No_Assets);
                                        TitleAvailabilityForMultiCatalog titleMissingAssets = shouldReportMissingAssets(videoId, packageId.val, contractId, window._getStartDate(), window._getEndDate(), thisWindowFoundLocalText, thisWindowFoundLocalAudio);
                                        if (titleMissingAssets != null) {
                                            cycleDataAggregator.collect(country, language, titleMissingAssets, Language_Catalog_Title_Availability);
                                        }
                                        continue;
                                    } else {
                                        // should we collect this info?
                                        //cycleDataAggregator.collect(country, language, videoId, Language_Catalog_Grandfather);
                                    }
                                }

                                boolean considerPackageForLang = packageData == null ? true : packageData.isDefaultPackage;
                                if (!considerPackageForLang && contractPackages.size() == 1) {
                                    considerPackageForLang = true;
                                }

                                if (considerPackageForLang && (packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) != 0)
                                    thisWindowFoundLocalAudio = true;

                                if (considerPackageForLang && (packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) != 0)
                                    thisWindowFoundLocalText = true;


                                if (thisWindowFoundLocalAudio == true && currentOrFirstFutureWindow == outputWindow)
                                    currentOrFirstFutureWindowFoundLocalAudio = true;

                                if (thisWindowFoundLocalText == true && currentOrFirstFutureWindow == outputWindow)
                                    currentOrFirstFutureWindowFoundLocalText = true;
                            }

                            // get windowPackageContractInfo for the given packageId
                            WindowPackageContractInfo windowPackageContractInfo = outputWindow.windowInfosByPackageId.get(packageId);
                            if (windowPackageContractInfo != null) {

                                // For existing windowPackageContractInfo object
                                // check if this window data should be filtered
                                if (shouldFilterOutWindowInfo) {
                                    // if contract is greater than previous contract for this package id then update the windowContractInfo.contractId to use higher value for contractId
                                    if (contractId > windowPackageContractInfo.videoContractInfo.contractId) {
                                        // update contract id, since it is higher value.
                                        windowPackageContractInfo.videoContractInfo.contractId = (int) contractId;

                                        // if the current package id is the highest package id, then update the contract id for max package id.
                                        if (packageId.val == maxPackageId)
                                            contractIdForMaxPackageId = Math.max((int) contractId, contractIdForMaxPackageId);
                                        // if current package equals package id for this window, then update the window assets group id, max contract id for the current window.
                                        if (packageId.val == packageIdForWindow)
                                            thisWindowBundledAssetsGroupId = Math.max((int) contractId, thisWindowBundledAssetsGroupId);
                                    }

                                } else {

                                    // if existing windowPackageContractInfo is present and window data is NOT TO BE filtered,
                                    // then clone and update window package contract info.
                                    WindowPackageContractInfo updatedClone = cloneWindowPackageContractInfo(videoId, windowPackageContractInfo,
                                            contractId, contractData, contractAssets, isAvailableForDownload, packageId.val);

                                    // update the package window package contract info
                                    outputWindow.windowInfosByPackageId.put(packageId, updatedClone);

                                    // if the current package id is the highest package id, then update the contract id for max package id.
                                    if (packageId.val == maxPackageId)
                                        contractIdForMaxPackageId = Math.max((int) contractId, contractIdForMaxPackageId);
                                    // if current package equals package id for this window, then update the window assets group id, max contract id for the current window.
                                    if (packageId.val == packageIdForWindow)
                                        thisWindowBundledAssetsGroupId = Math.max((int) contractId, thisWindowBundledAssetsGroupId);

                                }

                            } else {

                                // if windowPackageContractInfo is not present in outputWindow.windowInfosByPackageId for the given packageId
                                // then create a new one

                                if (shouldFilterOutWindowInfo) {
                                    // if previously filtered window package contract info exists the update the contract id in that to use higher value
                                    WindowPackageContractInfo alreadyFilteredWindowPackageContractInfo = outputWindow.windowInfosByPackageId.get(ZERO);
                                    if (alreadyFilteredWindowPackageContractInfo != null) {
                                        if (alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId < (int) contractId)
                                            alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId = (int) contractId;
                                    } else {
                                        // if previously filtered window package contract info does not exists then create new filtered window package contract info.
                                        outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractId, videoId));
                                    }

                                    // current package id is the first one and window data needs to be filtered, then update contract id for max package id.
                                    if (maxPackageId == 0)
                                        contractIdForMaxPackageId = Math.max(contractIdForMaxPackageId, (int) contractId);

                                    // current package id is the fist one and window data needs to be filtered, then update the window assets group id, max contract id for the current window.
                                    if (packageIdForWindow == 0)
                                        thisWindowBundledAssetsGroupId = Math.max(thisWindowBundledAssetsGroupId, (int) contractId);
                                } else {

                                    // if windowPackageContractInfo is not present in outputWindow.windowInfosByPackageId for the given packageId and window data is not to be filtered
                                    includedWindowPackageData = true;

                                    if (packageData != null) {
                                        // package data is available
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfo(
                                                videoId, packageData, windowContractHollow, contractData, country,
                                                isAvailableForDownload, packageDataCollection);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);
                                        boolean considerForPackageSelection = contractPackages == null ? true : packageData.isDefaultPackage;
                                        if (!considerForPackageSelection) {
                                            if (contractPackages.size() == 1)
                                                considerForPackageSelection = true;
                                        }
                                        if (considerForPackageSelection) {

                                            // if package is default package or the only package in contract packages list
                                            // update max package Id and contract id for max package id.
                                            if (packageData.id > maxPackageId) {
                                                maxPackageId = packageData.id;
                                                contractIdForMaxPackageId = (int) contractId;
                                            }

                                            // update max package Id for current window and max contract id for the current window.
                                            if (packageData.id > packageIdForWindow) {
                                                packageIdForWindow = packageData.id;
                                                thisWindowBundledAssetsGroupId = (int) contractId;
                                            }
                                        }

                                    } else {
                                        // package data not available -- use the contract only
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(packageId.val, windowContractHollow, contractData, videoId);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                        // if fist package, then update contract id for the current window
                                        if (packageIdForWindow == 0)
                                            thisWindowBundledAssetsGroupId = Math.max((int) contractId, thisWindowBundledAssetsGroupId);
                                        // update contract id for the max package id
                                        if (maxPackageId == 0)
                                            contractIdForMaxPackageId = Math.max((int) contractId, contractIdForMaxPackageId);
                                    }
                                }

                                long windowEndDate = window._getEndDate();
                                long windowStartDate = window._getStartDate();
                                // if window is open then rollup the start window availability date and update the flag isInWindow
                                if (isGoLive && windowEndDate > ctx.getNowMillis() && windowStartDate < ctx.getNowMillis()) {
                                    rollup.newInWindowAvailabilityDate(windowStartDate);
                                    isInWindow = true;
                                }

                                // keep track if minimum window start date, only for windows where end date is greater than now
                                // also update currentOrFutureWindow and local audio/text values
                                if (windowEndDate > ctx.getNowMillis() && windowStartDate < minWindowStartDate) {
                                    minWindowStartDate = windowStartDate;
                                    currentOrFirstFutureWindow = outputWindow;
                                    currentOrFirstFutureWindowFoundLocalAudio = thisWindowFoundLocalAudio;
                                    currentOrFirstFutureWindowFoundLocalText = thisWindowFoundLocalText;
                                }
                            }

                        } // end of for loop for packages in contract


                    } else {

                        //Do not lose sight of the fact that the rollingEpisode flag could be set even if the packages are not present
                        if (contractData != null && contractData._getDayAfterBroadcast()) {
                            rollup.foundRollingEpisodes();
                        }

                        // package list is empty for the given contract -- use the contract only. Applicable only for non multi-locale country that is if locale is not passed

                        if (language == null) {
                            // build info without package data, Use the assets and contract data though
                            WindowPackageContractInfo windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(0, windowContractHollow, contractData, videoId);
                            outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfo);

                            if (packageIdForWindow == 0)
                                thisWindowBundledAssetsGroupId = Math.max((int) contractId, thisWindowBundledAssetsGroupId);
                            if (maxPackageId == 0)
                                contractIdForMaxPackageId = Math.max((int) contractId, contractIdForMaxPackageId);
                        }
                    }
                } else {
                    if (language == null) {
                        // if no assets and no associated package available then build info using just contract ID and video ID
                        outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) contractId, videoId));

                        if (maxPackageId == 0) {
                            contractIdForMaxPackageId = (int) contractId;
                            thisWindowBundledAssetsGroupId = (int) contractId;
                        }
                    }
                }

            } // end of for loop for iterating through contracts in a window

            // assign the highest contract Id recorded for the highest package Id in the current window
            outputWindow.bundledAssetsGroupId = thisWindowBundledAssetsGroupId;

            // if locale is not null and windowInfosByPackageId is empty then the code in if block is not executed.
            // Basically - Do not add: outputWindow to availabilityWindows list if multi-language catalog and empty info is present.
            if (language == null || !outputWindow.windowInfosByPackageId.isEmpty()) {
                availabilityWindows.add(outputWindow);

                // if evaluating episode and isGoLive is true, then roll up the windows to season.
                if (rollup.doEpisode()) {

                    if (isMulticatalogRollup)
                        rollup.windowFound(outputWindow.startDate.val, outputWindow.endDate.val);
                    if (isGoLive)
                        rollup.newSeasonWindow(outputWindow.startDate.val, outputWindow.endDate.val, outputWindow.onHold, rollup.getSeasonSequenceNumber());
                }
            }

            if (includedWindowPackageData)
                includedPackageDataCount++;

        } // end of for loop for iterating through windows for this video


        if (currentOrFirstFutureWindow != null) {
            WindowPackageContractInfo maxPackageContractInfo =
                    getMaxPackageContractInfo(ctx, videoId, country,
                            currentOrFirstFutureWindow.windowInfosByPackageId.values());
            if (maxPackageContractInfo == null || maxPackageContractInfo.videoContractInfo == null
                    || maxPackageContractInfo.videoPackageInfo == null) {
                throw new RuntimeException("Invalid maxPackageContractInfo for video=" + videoId
                        + " country= " + country + " info=" + maxPackageContractInfo);
            }
            rollup.newAssetBcp47Codes(maxPackageContractInfo.videoContractInfo.assetBcp47Codes);
            rollup.newPrePromoDays(minValueToZero(maxPackageContractInfo.videoContractInfo.prePromotionDays));
            if (maxPackageContractInfo.videoContractInfo.isDayOfBroadcast)
                rollup.foundDayOfBroadcast();
            if (maxPackageContractInfo.videoContractInfo.hasRollingEpisodes)
                rollup.foundRollingEpisodes();
            if (maxPackageContractInfo.videoContractInfo.isAvailableForDownload)
                rollup.foundAvailableForDownload();
            if (isGoLive && isInWindow) {
                rollup.newVideoFormatDescriptors(maxPackageContractInfo.videoPackageInfo.formats);
                rollup.newCupTokens(maxPackageContractInfo.videoContractInfo.cupTokens);
            }

            rollup.newEpisodeData(isGoLive, currentOrFirstFutureWindow.bundledAssetsGroupId);

            // for multi-catalog country, rollup the local audio and text values
            if (language != null) {
                if (currentOrFirstFutureWindowFoundLocalAudio)
                    rollup.foundLocalAudio();
                if (currentOrFirstFutureWindowFoundLocalText)
                    rollup.foundLocalText();
            }

        } else if (language == null) {
            // if no current or future window found, then do this, but why?
            rollup.newEpisodeData(isGoLive, contractIdForMaxPackageId);
            if (rollup.doEpisode())
                rollup.newPrePromoDays(0);
        }

        if (language != null && (availabilityWindows == null || availabilityWindows.isEmpty())) {
            // collect no windows
            cycleDataAggregator.collect(country, language, videoId, Language_catalog_NoWindows);
        }
        return availabilityWindows;
    }

    // Return AvailabilityWindow from MediaData
    private List<VMSAvailabilityWindow> populateRolledUpWindowData(Integer videoId, CountrySpecificRollupValues
            rollup, RightsHollow rights, boolean isGoLive, boolean isMulticatalogRollup) {

        List<RightsWindowHollow> rightsWindowHollows = new ArrayList<>(rights._getWindows());
        if (!rightsWindowHollows.isEmpty()) {

            boolean isInWindow = false;
            int maxContractId = Integer.MIN_VALUE;

            Collections.sort(rightsWindowHollows, RIGHTS_WINDOW_COMPARATOR);
            List<VMSAvailabilityWindow> windowList = new ArrayList<>(rightsWindowHollows.size());

            for (RightsWindowHollow window : rightsWindowHollows) {
                long startDate = window._getStartDate();
                long endDate = window._getEndDate();
                boolean isOnHold = window._getOnHold();

                if (window._getOnHold()) {
                    startDate += ONE_THOUSAND_YEARS;
                    endDate += ONE_THOUSAND_YEARS;
                }

                if (startDate < ctx.getNowMillis() && endDate > ctx.getNowMillis())
                    isInWindow = true;

                if (window._getContractIdsExt() != null) {
                    for (RightsWindowContractHollow rightsWindowContract : window._getContractIdsExt()) {
                    	if(ctx.getConfig().isUseContractIdInsteadOfDealId()) {
                            if ((int) rightsWindowContract._getContractId() > maxContractId)
                                maxContractId = (int) rightsWindowContract._getContractId();
                    	} else {
                            if ((int) rightsWindowContract._getDealId() > maxContractId)
                                maxContractId = (int) rightsWindowContract._getDealId();                    		
                    	}
                    }
                }

                if (isMulticatalogRollup) {
                    DateWindow windowWithEpisodes = rollup.doShow() ? rollup.getValidShowWindow(startDate, endDate) : rollup.getValidSeasonWindow(startDate, endDate);

                    if (windowWithEpisodes != null) {
                        startDate = windowWithEpisodes.startDateTimestamp;
                        endDate = windowWithEpisodes.endDateTimestamp;
                    } else continue;
                }

                VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
                outputWindow.startDate = OutputUtil.getRoundedDate(startDate);
                outputWindow.endDate = OutputUtil.getRoundedDate(endDate);
                outputWindow.onHold = isOnHold;
                outputWindow.bundledAssetsGroupId = maxContractId; //rollup.getFirstEpisodeBundledAssetId();

                WindowPackageContractInfo outputContractInfo = createEmptyContractInfoForRollup(outputWindow);


                outputWindow.windowInfosByPackageId = new HashMap<>();
                outputWindow.windowInfosByPackageId.put(ZERO, outputContractInfo);

                outputContractInfo.videoContractInfo.assetBcp47Codes = rollup.getAssetBcp47Codes();
                outputContractInfo.videoContractInfo.prePromotionDays = rollup.getPrePromoDays();
                outputContractInfo.videoContractInfo.isDayOfBroadcast = rollup.isDayOfBroadcast();
                outputContractInfo.videoContractInfo.isDayAfterBroadcast = rollup.hasRollingEpisodes();
                outputContractInfo.videoContractInfo.hasRollingEpisodes = rollup.hasRollingEpisodes(); // NOTE: DAB and hasRollingEpisodes means the same
                outputContractInfo.videoContractInfo.isAvailableForDownload = rollup.isAvailableForDownload();
                outputContractInfo.videoContractInfo.postPromotionDays = 0;
                outputContractInfo.videoContractInfo.cupTokens = rollup.getCupTokens() != null ? rollup.getCupTokens() : DEFAULT_CUP_TOKENS;
                outputContractInfo.videoPackageInfo.formats = rollup.getVideoFormatDescriptors();

                if (rollup.getFirstEpisodeBundledAssetId() != 0) {
                    outputWindow.bundledAssetsGroupId = rollup.getFirstEpisodeBundledAssetId();
                    outputContractInfo.videoContractInfo.contractId = rollup.getFirstEpisodeBundledAssetId();
                }

                if (!(isGoLive && isInWindow))
                    outputContractInfo.videoPackageInfo.formats = Collections.emptySet();  ///TODO: This seems totally unnecessary.  We should remove this line after parity testing.

                int videoGeneralOrdinal = videoGeneralIdx.getMatchingOrdinal(Long.valueOf(videoId.intValue()));
                if (videoGeneralOrdinal != -1) {
                    VideoGeneralHollow general = api.getVideoGeneralHollow(videoGeneralOrdinal);
                    long runtime = general._getRuntime();
                    if (runtime != Long.MIN_VALUE)
                        outputContractInfo.videoPackageInfo.runtimeInSeconds = (int) runtime;
                }

                windowList.add(outputWindow);
            }

            return windowList;
        }

        return Collections.emptyList();
    }

    /**
     * Use this criteria for reporting titles that do not have localized assets or rather they fail asset availability check.
     * Return object only if the title has a future window in next 30 days or is live current window and assets are missing.
     */
    private TitleAvailabilityForMultiCatalog shouldReportMissingAssets(int videoId, long packageId, long contractId, long windowStart, long windowEnd, boolean thisWindowFoundLocalText, boolean thisWindowFoundLocalAudio) {

        // if anyone asset if present then return null
        if (thisWindowFoundLocalAudio || thisWindowFoundLocalText) return null;

        boolean isLiveWindow = windowStart < ctx.getNowMillis() && windowEnd > ctx.getNowMillis();
        boolean isFuture = windowStart > ctx.getNowMillis() && windowStart < (ctx.getNowMillis() + FUTURE_CUT_OFF_FOR_REPORT);

        if (isLiveWindow || isFuture) {

            List<String> assetsMissing = new ArrayList<>();
            if (!thisWindowFoundLocalAudio) assetsMissing.add(LocalizedAssets.DUBS.toString());
            if (!thisWindowFoundLocalText) assetsMissing.add(LocalizedAssets.SUBS.toString());
            TitleAvailabilityForMultiCatalog notAvailable = new TitleAvailabilityForMultiCatalog(videoId, windowStart, windowEnd, packageId, contractId, assetsMissing);
            return notAvailable;
        }
        return null;
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

    private PackageDataCollection getPackageDataCollection(Integer videoId, long packageId) {
        return transformedVideoData.getTransformedPackageData(videoId).getPackageDataCollection((int) packageId);
    }

    // old logic for checking if window should be filtered out in country catalog
    private boolean shouldFilterOutWindowInfo(long videoId, String countryCode, boolean isGoLive, Collection<Long> contractIds, int unfilteredCount, long startDate, long endDate) {

        // window has ended, then filter it
        if (endDate < ctx.getNowMillis())
            return true;

        if (!isGoLive) {
            boolean isWindowDataNeeded = false;
            for (Long contractId : contractIds) {
                ContractHollow contract = VideoContractUtil.getContract(api, indexer, ctx, videoId, countryCode, contractId);
                if (contract != null && (contract._getDayOfBroadcast() || contract._getDayAfterBroadcast() || contract._getPrePromotionDays() > 0)) {
                    isWindowDataNeeded = true;
                }
            }

            if (!isWindowDataNeeded)
                return true;
        }

        if (unfilteredCount < 3 && endDate > ctx.getNowMillis())
            return false;

        if (startDate > ctx.getNowMillis() + FUTURE_CUTOFF_IN_MILLIS)
            return true;

        return false;
    }

    /**
     * This function checks if the given window should be filtered.
     * - If the window end data is less than now, meaning window has already passed, then answer is yes.
     * - if isGoLive flag is false, then check if window data is needed. This is mainly for video (check all contracts) that are in pre-promotion stage or in days after broadcast.
     * - if isGoLive is true, then check if window is open, endDate is greater than now
     * - if isGoLive is true and endDate is > now, then check is window start is not in far future.
     *
     * @return false means do not filter out the window
     */
    private boolean shouldFilterOutWindowInfo(long videoId, String countryCode, String language, boolean isGoLive, Collection<Long> contractIds, int unfilteredCount, long startDate, long endDate) {

        if (language == null) {
            // use old logic
            return shouldFilterOutWindowInfo(videoId, countryCode, isGoLive, contractIds, unfilteredCount, startDate, endDate);
        }

        if (!ctx.getConfig().isAssetAvailabilityIntentForPrePromoEnabled()) {
            // if checking of assets availability intended date for a locale feature is not enabled. use old logic where anything is in pre-promo window, we pre-promo
            return shouldFilterOutWindowInfo(videoId, countryCode, isGoLive, contractIds, unfilteredCount, startDate, endDate);
        }

        // language is not null -> use earliest window start date for that language (best estimate the assets will be available)

        // window has ended, then filter it
        if (endDate < ctx.getNowMillis()) return true;

        if (!isGoLive) {
            Long earliestWindowStartDateForTheLanguageWithAssets = getEarliestWindowStartDateForTheLanguage(videoId, countryCode, language);
            boolean isWindowDataNeeded = false;
            if (startDate > ctx.getNowMillis()) {
                boolean shouldPrePromote = shouldPrePromote(videoId, countryCode, contractIds, earliestWindowStartDateForTheLanguageWithAssets);
                if (shouldPrePromote) {
                    cycleDataAggregator.collect(countryCode, language, (int) videoId, Language_catalog_PrePromote);
                    isWindowDataNeeded = true;
                }
            }

            if (!isWindowDataNeeded) return true;
        }

        if (unfilteredCount < 3 && endDate > ctx.getNowMillis()) return false;

        if (startDate > ctx.getNowMillis() + FUTURE_CUTOFF_IN_MILLIS) return true;

        return false;
    }

    private boolean shouldPrePromote(long videoId, String countryCode, Collection<Long> contractIds, Long earliestWindowStartDateForTheLanguageWithAssets) {

        // enabled by default
        boolean assetDeliveryIntentEnabled = ctx.getConfig().isAssetAvailabilityIntentForPrePromoEnabled();

        if (assetDeliveryIntentEnabled && earliestWindowStartDateForTheLanguageWithAssets == null) return false;

        int daysBeforeEarliestWindowWithAssetsAvailability = 0;
        if (assetDeliveryIntentEnabled)
            daysBeforeEarliestWindowWithAssetsAvailability = (int) ((earliestWindowStartDateForTheLanguageWithAssets - ctx.getNowMillis()) / MS_IN_DAY);

        boolean daysBeforeToPromoteCheck = false;
        for (long contractId : contractIds) {
            ContractHollow contract = VideoContractUtil.getContract(api, indexer, ctx, videoId, countryCode, contractId);
            if (contract != null && (contract._getDayOfBroadcast() || contract._getDayAfterBroadcast() || contract._getPrePromotionDays() > 0 )) {

                // if using asset delivery intent feature is not enabled then pre-promote in all language catalogs if start date is in pre-promo window
                if (!assetDeliveryIntentEnabled) return true;

                if (daysBeforeEarliestWindowWithAssetsAvailability <= contract._getPrePromotionDays())
                    daysBeforeToPromoteCheck = true;
            }
        }

        // should check if window start is greater than now? basically pre-promote only works for future windows??

        // return true only if pre-promote check passes and window start is less than next year
        return daysBeforeToPromoteCheck;
    }

    private boolean readyForPrePromotionInLanguageCatalog(long videoId, String country, String language, Collection<Long> contractIds, long startDate) {

        if (startDate > ctx.getNowMillis()) {
            // pre-promotion check only for evaluating future windows
            // get the earliest window start date for the languages
            Long earliestWindowStartDateForTheLanguageWithAssets = getEarliestWindowStartDateForTheLanguage(videoId, country, language);
            if (earliestWindowStartDateForTheLanguageWithAssets != null) {
                boolean isFutureDate = ctx.getNowMillis() < earliestWindowStartDateForTheLanguageWithAssets;
                if (isFutureDate) {
                    return shouldPrePromote(videoId, country, contractIds, earliestWindowStartDateForTheLanguageWithAssets);
                } else return false;
            }
        }

        return false;
    }

    private Long getEarliestWindowStartDateForTheLanguage(long videoId, String country, String language) {
        int ordinal = merchLanguageDateIdx.getMatchingOrdinal(videoId, country);
        if (ordinal != -1) {

            // here means we have asset rights for that language and corresponding earliest asset availability date
            FeedMovieCountryLanguagesHollow feedMovieCountryLanguagesHollow = api.getFeedMovieCountryLanguagesHollow(ordinal);
            MapOfStringToLongHollow mapOfStringToLongHollow = feedMovieCountryLanguagesHollow._getLanguageToEarliestWindowStartDateMap();
            LongHollow longHollow = mapOfStringToLongHollow.get(language);
            if (longHollow != null) {
                return longHollow._getValue();
            } else { return null; }

        }
        return null;
    }

    /**
     * Return the max WindowPackageContractInfo (by package ID) from a collection of them. Note that
     * the WindowPackageContractInfo#videoContractInfo may be null, but
     * WindowPackageContractInfo#videoPackageInfo cannot be null.
     */
    protected static WindowPackageContractInfo getMaxPackageContractInfo(TransformerContext ctx,
            Integer videoId, String country, Collection<WindowPackageContractInfo> windowInfos) {
        Optional<WindowPackageContractInfo> maxDefaultPackage = windowInfos.stream()
                .filter(info -> info.videoPackageInfo.isDefaultPackage)
                .max(Comparator.comparing(info -> info.videoPackageInfo.packageId));
        if (maxDefaultPackage.isPresent()) {
            return maxDefaultPackage.get();
        } else {
            ctx.getLogger().warn(InteractivePackage, "Only non-default packages found for video={}, country={}",
                    videoId, country);
            return windowInfos.stream()
                    .max(Comparator.comparing(info -> info.videoPackageInfo.packageId)).orElse(null);
        }
    }


    boolean isGoLive(StatusHollow status) {
        FlagsHollow flags = status._getFlags();
        return flags != null && flags._getGoLive();
    }

    boolean isLanguageOverride(StatusHollow status) {
        FlagsHollow flags = status._getFlags();
        return flags != null && flags._getLanguageOverride();
    }

    public void reset() {
        this.transformedVideoData = null;
        this.windowPackageContractInfoModule.reset();
    }

    /**
     * Structure to collect missing/not merched title availability data in a language catalog.
     */
    public static class TitleAvailabilityForMultiCatalog implements CycleDataAggregator.JSONMessage {

        private ObjectNode objectNode;

        private TitleAvailabilityForMultiCatalog(int videoId, long start, long end, long packageId, long contractId, List<String> assetsMissing) {
            JsonNodeFactory factory = JsonNodeFactory.instance;
            objectNode = factory.objectNode();
            objectNode.put("videoId", factory.numberNode(videoId));
            objectNode.put("contractId", factory.numberNode(contractId));
            objectNode.put("packageId", factory.numberNode(packageId));
            objectNode.put("windowStart", factory.numberNode(start));
            objectNode.put("windowEnd", factory.numberNode(end));

            ArrayNode arrayNode = factory.arrayNode();
            for (String assetMissing : assetsMissing)
                arrayNode.add(factory.textNode(assetMissing));

            objectNode.put("assetsMissing", arrayNode);
        }

        @Override
        public ObjectNode getObjectNode() {
            return objectNode;
        }
    }

    private enum LocalizedAssets {

        SUBS("subs"), DUBS("dubs"), SYNOPSIS("synopsis");

        private String value;

        LocalizedAssets(String value) {
            this.value = value;
        }
    }

    private static final Comparator<RightsWindowHollow> RIGHTS_WINDOW_COMPARATOR = (o1, o2) -> {
        long t1 = o1._getOnHold() ? o1._getStartDate() + ONE_THOUSAND_YEARS : o1._getStartDate();
        long t2 = o2._getOnHold() ? o2._getStartDate() + ONE_THOUSAND_YEARS : o2._getStartDate();
        return t1 < t2 ? -1 : (t1 == t2 ? 0 : 1);
    };
}
