package com.netflix.vms.transformer.modules.countryspecific;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InteractivePackage;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_Catalog_Title_Availability;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_NoWindows;
import static com.netflix.vms.transformer.util.OutputUtil.minValueToZero;
//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.DatasetIdentifier.OSCAR;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.CycleDataAggregator;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.contract.ContractAssetType;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.data.TransformedVideoData;
import com.netflix.vms.transformer.hollowinput.FeedMovieCountryLanguagesHollow;
import com.netflix.vms.transformer.hollowinput.LongHollow;
import com.netflix.vms.transformer.hollowinput.MapOfStringToLongHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
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
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Flags;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Rights;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsContractAsset;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsContractPackage;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindowContract;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.modules.packages.PackageDataCollection;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.VideoContractUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
//    private final OscarDataset oscarDataset;

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
            CupTokenFetcher cupTokenFetcher, UpstreamDatasetDefinition upstream) {
        this.api = api;
        this.ctx = ctx;
        this.indexer = indexer;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
        this.merchLanguageDateIdx = indexer.getPrimaryKeyIndex(IndexSpec.MERCH_LANGUAGE_DATE);
        this.cupTokenFetcher = cupTokenFetcher;
//        this.oscarDataset = upstream.getHashIndex(OSCAR);

        this.windowPackageContractInfoModule = new WindowPackageContractInfoModule(api, indexer, cupTokenFetcher, ctx, upstream);
        this.multilanguageCountryWindowFilter = new MultilanguageCountryWindowFilter(api, cycleConstants);
        this.cycleDataAggregator = cycleDataAggregator;

        EMPTY_CUP_TOKENS = new LinkedHashSetOfStrings();
        EMPTY_CUP_TOKENS.ordinals = Collections.emptyList();

        DEFAULT_CUP_TOKENS = new LinkedHashSetOfStrings();
        DEFAULT_CUP_TOKENS.ordinals = Collections.singletonList(new Strings(CupKey.DEFAULT));
    }


    public void setTransformedVideoData(TransformedVideoData transformedVideoData) {
        this.transformedVideoData = transformedVideoData;
    }

    public List<VMSAvailabilityWindow> populateWindowData(Integer videoId, String country, CompleteVideoCountrySpecificData data, Status videoRights, CountrySpecificRollupValues rollup) {
        boolean isGoLive = isGoLive(videoRights);

        List<VMSAvailabilityWindow> windows = calculateWindowData(videoId, country, null, videoRights, rollup, isGoLive);

        data.availabilityWindows = windows;

        return windows;
    }

    List<VMSAvailabilityWindow> calculateWindowData(Integer videoId, String country, String language, Status videoRights, CountrySpecificRollupValues rollup, boolean isGoLive) {
        List<VMSAvailabilityWindow> windows = null;

        Rights rights = videoRights.getRights();
        if ((rollup.doShow() && rollup.wasShowEpisodeFound()) || (rollup.doSeason() && rollup.wasSeasonEpisodeFound())) {
            windows = populateRolledUpWindowData(videoId, rollup, rights, isGoLive, language != null);
        } else {
            // todo evaluate languageOverride versus alternate language set
            if (isLanguageOverride(videoRights))
                windows = populateEpisodeOrStandaloneWindowData(videoId, country, null, rollup, isGoLive, rights, language != null, videoRights);
            else windows = populateEpisodeOrStandaloneWindowData(videoId, country, language, rollup, isGoLive, rights, language != null, videoRights);
        }
        return windows;
    }

    private VMSAvailabilityWindow newVMSAvailabilityWindow(RightsWindow rightsWindowHollow) {
        VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
        outputWindow.windowInfosByPackageId = new HashMap<>();
        outputWindow.startDate = OutputUtil.getRoundedDate(rightsWindowHollow.getStartDate());
        outputWindow.endDate = OutputUtil.getRoundedDate(rightsWindowHollow.getEndDate());
        if (rightsWindowHollow.getOnHold()) {
            outputWindow.startDate.val += ONE_THOUSAND_YEARS;
            outputWindow.endDate.val += ONE_THOUSAND_YEARS;
            outputWindow.onHold = true;
        }
        return outputWindow;
    }

    WindowPackageContractInfo cloneWindowPackageContractInfo(int videoId, WindowPackageContractInfo existingInfo, long contractId, VmsAttributeFeedEntryHollow contractAttributes, List<RightsContractAsset> contractAssets, boolean isAvailableForDownload, int packageId) {

        // 1. merge cup token values
        List<Strings> cupTokens = new ArrayList<>();
        Strings contractCupToken = cupTokenFetcher.getCupToken(videoId, contractAttributes);
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
        for (RightsContractAsset asset : contractAssets) {
            bcp47Codes.add(new Strings(asset.getBcp47Code()));
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
    private List<VMSAvailabilityWindow> populateEpisodeOrStandaloneWindowData(Integer videoId, String country, String language, CountrySpecificRollupValues rollup, boolean isGoLive, Rights rights, boolean isMulticatalogRollup, Status status) {

        List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<>();
        VMSAvailabilityWindow currentOrFirstFutureWindow = null;

        String originalLanguageBCPCodeForVideo = "";
        int videoGeneralOrdinal = videoGeneralIdx.getMatchingOrdinal(Long.valueOf(videoId.intValue()));
        if (videoGeneralOrdinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(videoGeneralOrdinal);
            if (general._getOriginalLanguageBcpCode() != null)
                originalLanguageBCPCodeForVideo = general._getOriginalLanguageBcpCode()._getValue();
        }

//        if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//            originalLanguageBCPCodeForVideo = oscarDataset.mapWithMovieIfExists(videoId,(movie)->movie.getOriginalLanguageBcpCode()).orElse("");
//        } else {
//            int videoGeneralOrdinal = videoGeneralIdx.getMatchingOrdinal(Long.valueOf(videoId.intValue()));
//            if (videoGeneralOrdinal != -1) {
//                VideoGeneralHollow general = api.getVideoGeneralHollow(videoGeneralOrdinal);
//                if (general._getOriginalLanguageBcpCode() != null)
//                    originalLanguageBCPCodeForVideo = general._getOriginalLanguageBcpCode()._getValue();
//            }
//        }

        long minWindowStartDate = Long.MAX_VALUE;

        boolean currentOrFirstFutureWindowFoundLocalAudio = false;
        boolean currentOrFirstFutureWindowFoundLocalText = false;
        boolean isInWindow = false;

        int maxPackageId = 0;
        int dealIdForMaxPackageId = 0;

        List<RightsWindow> sortedWindows = new ArrayList<>(rights.getWindows());
        Collections.sort(sortedWindows, RIGHTS_WINDOW_COMPARATOR);

        boolean grandfatherEnabled = ctx.getConfig().isGrandfatherEnabled();
        Set<String> grandfatherLanguages = Collections.EMPTY_SET;
        if (language != null && status.getFlags()!= null && status.getFlags().getGrandfatheredLanguages() != null)
            grandfatherLanguages = status.getFlags().getGrandfatheredLanguages().stream().map(l -> l.getValue()).collect(Collectors.toSet());

        for (RightsWindow window : sortedWindows) {

            int packageIdForWindow = 0;
            int thisWindowBundledAssetsGroupId = 0;

            boolean includedWindowPackageData = false;
            boolean thisWindowFoundLocalAudio = false;
            boolean thisWindowFoundLocalText = false;

            // create new window
            VMSAvailabilityWindow outputWindow = newVMSAvailabilityWindow(window);
            boolean isOpenWindow = window.getStartDate() <= ctx.getNowMillis() && window.getEndDate() > ctx.getNowMillis();
            boolean isFutureWindow = window.getStartDate() > ctx.getNowMillis();
            boolean hasMerchIntentForLanguage = language != null ?
                    getEarliestWindowStartDateForTheLanguage(videoId, country, language, originalLanguageBCPCodeForVideo) != null : false;
            List<RightsWindowContract> windowContracts = window.getContractIdsExt() != null ?  window.getContractIdsExt().stream().collect(Collectors.toList()) : Collections.EMPTY_LIST;
            boolean shouldFilterWindowPackageContractData = shouldFilterWindowPackageContractData(window.getStartDate(), window.getEndDate());

            for (RightsWindowContract windowContract : windowContracts) {

                // get contract ID, contract data, and contract for download availability, contract packages -> A contract could have multiple packages & contract assets
                long dealId = windowContract.getDealId();

                VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, country, dealId);
                boolean isAvailableForDownload = windowContract.getDownload();
                List<RightsContractPackage> contractPackages = windowContract.getPackages() != null ? windowContract.getPackages().stream().collect(Collectors.toList()) : Collections.EMPTY_LIST;
                List<RightsContractAsset> contractAssetRights = windowContract.getAssets() != null ? windowContract.getAssets().stream().collect(Collectors.toList()) : Collections.EMPTY_LIST;

                // CASE 1: NO packages and Assets in the contract -> Build info using only the videoID and contractID
                if (windowContract.getPackageIdBoxed() == null && contractAssetRights.isEmpty() && contractPackages.isEmpty()) {

                    outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) dealId, videoId));
                    if (maxPackageId == 0) {
                        dealIdForMaxPackageId = (int) dealId;
                        thisWindowBundledAssetsGroupId = (int) dealId;
                    }

                } else if (contractPackages.isEmpty()) {
                    // CASE 2: Packages not available -> Build the info using assets, contractID and videoID and use "0" for package Id

                    //Do not lose sight of the fact that the rollingEpisode flag could be set even if the packages are not present
                    if (contractAttributes != null && contractAttributes._getDayAfterBroadcast() != null && contractAttributes._getDayAfterBroadcast()._getValue()) 
                    	rollup.foundRollingEpisodes();

                    // build info without package data, Use the assets and contract data though
                    WindowPackageContractInfo windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(0, windowContract, contractAttributes, videoId);
                    outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfo);

                    if (packageIdForWindow == 0) thisWindowBundledAssetsGroupId = Math.max((int) dealId, thisWindowBundledAssetsGroupId);
                    if (maxPackageId == 0) dealIdForMaxPackageId = Math.max((int) dealId, dealIdForMaxPackageId);

                } else {

                        // CASE 3: packages and assets are present for the contract.
                        for (RightsContractPackage contractPackage : contractPackages) {

                            // create packageId, get packageDataCollection and packageData for the given package in the contract
                            com.netflix.vms.transformer.hollowoutput.Integer packageId = new com.netflix.vms.transformer.hollowoutput.Integer((int) contractPackage.getPackageId());
                            PackageDataCollection packageDataCollection = getPackageDataCollection(videoId, contractPackage.getPackageId());
                            PackageData packageData = null;
                            if (packageDataCollection != null)
                                packageData = packageDataCollection.getPackageData();

                            // merch filtering logic begins here
                            if (language != null) {
                                long packageAvailability = 0L;
                                if (country != null && ctx.getConfig().isLanguageVariantsForAssetCheckEnabled()) {

                                    OctoberSkyData osData = ctx.getOctoberSkyData();
                                    Set<String> variants = osData.getLanguageVariants(country, language);

                                    if (osData.getOtherLanguageVariants(country, language).contains(originalLanguageBCPCodeForVideo))
                                        variants.add(originalLanguageBCPCodeForVideo);
                                    for (String locale : variants) {
                                        long contractAssetAvailability = multilanguageCountryWindowFilter.contractAvailabilityForLocale(locale, contractAssetRights);
                                        packageAvailability |= multilanguageCountryWindowFilter.packageIsAvailableForLocale(locale, packageData, contractAssetAvailability);
                                    }
                                } else {
                                    long contractAssetAvailability = multilanguageCountryWindowFilter.contractAvailabilityForLanguage(language, contractAssetRights);
                                    packageAvailability = multilanguageCountryWindowFilter.packageIsAvailableForLanguage(language, packageData, contractAssetAvailability);
                                }

                                // package filtering
                                if (packageAvailability == 0) {

                                    // check for grandfathering of existing titles in country catalog to continue to be available in new language catalog in that country.
                                    boolean skipPackage = true;
                                    if (grandfatherEnabled && grandfatherLanguages.contains(language)) {
                                        skipPackage = false;
                                    } else if(isFutureWindow && hasMerchIntentForLanguage) {
                                        skipPackage = false;
                                    }
                                    if (skipPackage) {
                                        reportMissingAssets(videoId, country, language, packageId.val, dealId, window.getStartDate(), window.getEndDate(), thisWindowFoundLocalText, thisWindowFoundLocalAudio);
                                        continue;
                                    }
                                }

                                if ((packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) != 0) thisWindowFoundLocalAudio = true;
                                if ((packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) != 0) thisWindowFoundLocalText = true;

                                if (thisWindowFoundLocalAudio == true && currentOrFirstFutureWindow == outputWindow) currentOrFirstFutureWindowFoundLocalAudio = true;
                                if (thisWindowFoundLocalText == true && currentOrFirstFutureWindow == outputWindow) currentOrFirstFutureWindowFoundLocalText = true;
                            }

                            // get windowPackageContractInfo for the given packageId
                            WindowPackageContractInfo windowPackageContractInfo = outputWindow.windowInfosByPackageId.get(packageId);
                            if (windowPackageContractInfo != null) {

                                // For existing windowPackageContractInfo object
                                // check if this window data should be filtered
                                if (shouldFilterWindowPackageContractData) {
                                    // if contract is greater than previous contract for this package id then update the windowContractInfo.contractId to use higher value for contractId
                                    if (dealId > windowPackageContractInfo.videoContractInfo.contractId) {
                                        // update contract id, since it is higher value.
                                        windowPackageContractInfo.videoContractInfo.contractId = (int) dealId;

                                        // if the current package id is the highest package id, then update the contract id for max package id.
                                        if (packageId.val == maxPackageId)
                                            dealIdForMaxPackageId = Math.max((int) dealId, dealIdForMaxPackageId);
                                        // if current package equals package id for this window, then update the window assets group id, max contract id for the current window.
                                        if (packageId.val == packageIdForWindow)
                                            thisWindowBundledAssetsGroupId = Math.max((int) dealId, thisWindowBundledAssetsGroupId);
                                    }

                                } else {

                                    // if existing windowPackageContractInfo is present and window data is NOT TO BE filtered,
                                    // then clone and update window package contract info.
                                    WindowPackageContractInfo updatedClone = cloneWindowPackageContractInfo(videoId, windowPackageContractInfo,
                                            dealId, contractAttributes, contractAssetRights, isAvailableForDownload, packageId.val);

                                    // update the package window package contract info
                                    outputWindow.windowInfosByPackageId.put(packageId, updatedClone);

                                    // if the current package id is the highest package id, then update the contract id for max package id.
                                    if (packageId.val == maxPackageId)
                                        dealIdForMaxPackageId = Math.max((int) dealId, dealIdForMaxPackageId);
                                    // if current package equals package id for this window, then update the window assets group id, max contract id for the current window.
                                    if (packageId.val == packageIdForWindow)
                                        thisWindowBundledAssetsGroupId = Math.max((int) dealId, thisWindowBundledAssetsGroupId);

                                }

                            } else {

                                // if windowPackageContractInfo is not present in outputWindow.windowInfosByPackageId for the given packageId
                                // then create a new one

                                if (shouldFilterWindowPackageContractData) {
                                    // if previously filtered window package contract info exists the update the contract id in that to use higher value
                                    WindowPackageContractInfo alreadyFilteredWindowPackageContractInfo = outputWindow.windowInfosByPackageId.get(ZERO);
                                    if (alreadyFilteredWindowPackageContractInfo != null) {
                                        if (alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId < (int) dealId)
                                            alreadyFilteredWindowPackageContractInfo.videoContractInfo.contractId = (int) dealId;
                                    } else {
                                        // if previously filtered window package contract info does not exists then create new filtered window package contract info.
                                        outputWindow.windowInfosByPackageId.put(ZERO, windowPackageContractInfoModule.buildFilteredWindowPackageContractInfo((int) dealId, videoId));
                                    }

                                    // current package id is the first one and window data needs to be filtered, then update contract id for max package id.
                                    if (maxPackageId == 0)
                                        dealIdForMaxPackageId = Math.max(dealIdForMaxPackageId, (int) dealId);

                                    // current package id is the fist one and window data needs to be filtered, then update the window assets group id, max contract id for the current window.
                                    if (packageIdForWindow == 0)
                                        thisWindowBundledAssetsGroupId = Math.max(thisWindowBundledAssetsGroupId, (int) dealId);
                                } else {

                                    if (packageData != null) {

                                        // yay found a valid package to include for the given window
                                        includedWindowPackageData = true;

                                        // package data is available
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfo(videoId, packageData, windowContract, contractAttributes, country, isAvailableForDownload, packageDataCollection);
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
                                                dealIdForMaxPackageId = (int) dealId;
                                            }

                                            // update max package Id for current window and max contract id for the current window.
                                            if (packageData.id > packageIdForWindow) {
                                                packageIdForWindow = packageData.id;
                                                thisWindowBundledAssetsGroupId = (int) dealId;
                                            }
                                        }

                                    } else {
                                        // package data not available -- use the contract only
                                        windowPackageContractInfo = windowPackageContractInfoModule.buildWindowPackageContractInfoWithoutPackage(packageId.val, windowContract, contractAttributes, videoId);
                                        outputWindow.windowInfosByPackageId.put(packageId, windowPackageContractInfo);

                                        // if fist package, then update contract id for the current window
                                        if (packageIdForWindow == 0)
                                            thisWindowBundledAssetsGroupId = Math.max((int) dealId, thisWindowBundledAssetsGroupId);
                                        // update contract id for the max package id
                                        if (maxPackageId == 0)
                                            dealIdForMaxPackageId = Math.max((int) dealId, dealIdForMaxPackageId);
                                    }
                                }

                                // if window is open then rollup the start window availability date and update the flag isInWindow
                                if (isGoLive && isOpenWindow) {
                                    rollup.newInWindowAvailabilityDate(window.getStartDate());
                                    isInWindow = true;
                                }

                                // keep track if minimum window start date, only for windows where end date is greater than now
                                // also update currentOrFutureWindow and local audio/text values
                                if (window.getEndDate() > ctx.getNowMillis() && window.getStartDate() < minWindowStartDate) {
                                    minWindowStartDate = window.getStartDate();
                                    currentOrFirstFutureWindow = outputWindow;
                                    currentOrFirstFutureWindowFoundLocalAudio = thisWindowFoundLocalAudio;
                                    currentOrFirstFutureWindowFoundLocalText = thisWindowFoundLocalText;
                                }
                            }

                        } // end of for loop for packages in contract

                    } // end of if-else checking if packages or assets are present.

            } // end of for loop for iterating through contracts in a window

            // assign the highest contract Id recorded for the highest package Id in the current window
            outputWindow.bundledAssetsGroupId = thisWindowBundledAssetsGroupId;
            // Skip the window logic
            boolean skipWindow = false;
            if (language != null) {
                // if window is open and no package data was included then skip the window
                if (isOpenWindow && !includedWindowPackageData) skipWindow = true;

                // if window is future, and no package data was included and also no intention to get assets then skip the window.
                if (isFutureWindow && !includedWindowPackageData && !hasMerchIntentForLanguage) {
                    skipWindow = true;
                }
            }

            if (!skipWindow) {
                availabilityWindows.add(outputWindow);

                // if evaluating episode and isGoLive is true, then roll up the windows to season.
                if (rollup.doEpisode()) {

                    if (isMulticatalogRollup)
                        rollup.windowFound(outputWindow.startDate.val, outputWindow.endDate.val);
                    if (isGoLive)
                        rollup.newSeasonWindow(outputWindow.startDate.val, outputWindow.endDate.val, outputWindow.onHold, rollup.getSeasonSequenceNumber());
                }
            }

        } // end of for loop for iterating through windows for this video


        if (currentOrFirstFutureWindow != null) {

            WindowPackageContractInfo maxPackageContractInfo = getMaxPackageContractInfo(ctx, videoId, country, currentOrFirstFutureWindow.windowInfosByPackageId.values());
            if (maxPackageContractInfo == null || maxPackageContractInfo.videoContractInfo == null || maxPackageContractInfo.videoPackageInfo == null) {
                throw new RuntimeException("Invalid maxPackageContractInfo for video=" + videoId + " country= " + country + " info=" + maxPackageContractInfo);
            }

            rollup.newAssetBcp47Codes(maxPackageContractInfo.videoContractInfo.assetBcp47Codes);
            rollup.newPrePromoDays(minValueToZero(maxPackageContractInfo.videoContractInfo.prePromotionDays));

            if (maxPackageContractInfo.videoContractInfo.isDayOfBroadcast) rollup.foundDayOfBroadcast();
            if (maxPackageContractInfo.videoContractInfo.hasRollingEpisodes) rollup.foundRollingEpisodes();
            if (maxPackageContractInfo.videoContractInfo.isAvailableForDownload) rollup.foundAvailableForDownload();
            if (isGoLive && isInWindow) {
                rollup.newVideoFormatDescriptors(maxPackageContractInfo.videoPackageInfo.formats);
                rollup.newCupTokens(maxPackageContractInfo.videoContractInfo.cupTokens);
            }

            rollup.newEpisodeData(isGoLive, currentOrFirstFutureWindow.bundledAssetsGroupId);

            // for multi-catalog country, rollup the local audio and text values
            if (language != null) {
                if (currentOrFirstFutureWindowFoundLocalAudio) rollup.foundLocalAudio();
                if (currentOrFirstFutureWindowFoundLocalText) rollup.foundLocalText();
            }

        } else if (language == null) {
            // if no current or future window found, then do this, but why?
            rollup.newEpisodeData(isGoLive, dealIdForMaxPackageId);
            if (rollup.doEpisode()) rollup.newPrePromoDays(0);
        }

        if (language != null && (availabilityWindows == null || availabilityWindows.isEmpty())) {
            // collect no windows
            cycleDataAggregator.collect(country, language, videoId, Language_catalog_NoWindows);
        }
        return availabilityWindows;
    }

    // Return AvailabilityWindow from MediaData
    private List<VMSAvailabilityWindow> populateRolledUpWindowData(Integer videoId, CountrySpecificRollupValues
            rollup, Rights rights, boolean isGoLive, boolean isMulticatalogRollup) {

        List<RightsWindow> rightsWindows = new ArrayList<>(rights.getWindows());
        if (!rightsWindows.isEmpty()) {

            boolean isInWindow = false;
            int maxDealId = Integer.MIN_VALUE;

            Collections.sort(rightsWindows, RIGHTS_WINDOW_COMPARATOR);
            List<VMSAvailabilityWindow> windowList = new ArrayList<>(rightsWindows.size());

            for (RightsWindow window : rightsWindows) {
                long startDate = window.getStartDate();
                long endDate = window.getEndDate();
                boolean isOnHold = window.getOnHold();

                if (window.getOnHold()) {
                    startDate += ONE_THOUSAND_YEARS;
                    endDate += ONE_THOUSAND_YEARS;
                }

                if (startDate < ctx.getNowMillis() && endDate > ctx.getNowMillis())
                    isInWindow = true;

                if (window.getContractIdsExt() != null) {
                    for (RightsWindowContract rightsWindowContract : window.getContractIdsExt()) {
                        if ((int) rightsWindowContract.getDealId() > maxDealId)
                            maxDealId = (int) rightsWindowContract.getDealId();
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
                outputWindow.bundledAssetsGroupId = maxDealId; //rollup.getFirstEpisodeBundledAssetId();

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

//                if (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) {
//                    oscarDataset.execWithMovieIfExists(videoId,(movie) -> {
//                        long runtime = movie.getRunLenth();
//                        if (runtime != Long.MIN_VALUE) {
//                            outputContractInfo.videoPackageInfo.runtimeInSeconds = (int) runtime;
//                        }
//                    });
//                } else {
//                    int videoGeneralOrdinal = videoGeneralIdx.getMatchingOrdinal(Long.valueOf(videoId.intValue()));
//                    if (videoGeneralOrdinal != -1) {
//                        VideoGeneralHollow general = api.getVideoGeneralHollow(videoGeneralOrdinal);
//                        long runtime = general._getRuntime();
//                        if (runtime != Long.MIN_VALUE)
//                            outputContractInfo.videoPackageInfo.runtimeInSeconds = (int) runtime;
//                    }
//                }

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
    private void reportMissingAssets(int videoId, String country, String language, long packageId, long contractId, long windowStart, long windowEnd, boolean thisWindowFoundLocalText, boolean thisWindowFoundLocalAudio) {

        // if anyone asset if present then return null
        if (thisWindowFoundLocalAudio || thisWindowFoundLocalText) return;

        boolean isLiveWindow = windowStart < ctx.getNowMillis() && windowEnd > ctx.getNowMillis();
        boolean isFuture = windowStart > ctx.getNowMillis() && windowStart < (ctx.getNowMillis() + FUTURE_CUT_OFF_FOR_REPORT);

        if (isLiveWindow || isFuture) {

            List<String> assetsMissing = new ArrayList<>();
            if (!thisWindowFoundLocalAudio) assetsMissing.add(LocalizedAssets.DUBS.toString());
            if (!thisWindowFoundLocalText) assetsMissing.add(LocalizedAssets.SUBS.toString());
            TitleAvailabilityForMultiCatalog titleMissingAssets = new TitleAvailabilityForMultiCatalog(videoId, windowStart, windowEnd, packageId, contractId, assetsMissing);
            cycleDataAggregator.collect(country, language, titleMissingAssets, Language_Catalog_Title_Availability);
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

    private PackageDataCollection getPackageDataCollection(Integer videoId, long packageId) {
        return transformedVideoData.getTransformedPackageData(videoId).getPackageDataCollection((int) packageId);
    }

    // do include package contract data for the window in output for windows that have passed/ended or will be open after a year
    private boolean shouldFilterWindowPackageContractData(long startDate, long endDate) {
        if (endDate < ctx.getNowMillis()) return true;
        if (startDate > (ctx.getNowMillis() + FUTURE_CUTOFF_IN_MILLIS)) return true;
        return false;
    }

    private Long getEarliestWindowStartDateForTheLanguage(long videoId, String country, String language, String originalLanguageBCPCodeForVideo) {

        // todo use originalLanguageBCPCodeForVideo

        boolean shouldCheckLanguageVariants = ctx.getConfig().isLanguageVariantsForMerchIntentEnabled();
        int ordinal = merchLanguageDateIdx.getMatchingOrdinal(videoId, country);
        if (ordinal != -1) {

            // here means we have asset rights for that language and corresponding earliest asset availability date
            FeedMovieCountryLanguagesHollow feedMovieCountryLanguagesHollow = api.getFeedMovieCountryLanguagesHollow(ordinal);
            MapOfStringToLongHollow mapOfStringToLongHollow = feedMovieCountryLanguagesHollow._getLanguageToEarliestWindowStartDateMap();
            LongHollow longHollow = mapOfStringToLongHollow.get(language);
            if (longHollow != null) {
                return longHollow._getValue();
            }

            if (shouldCheckLanguageVariants) {
                Set<String> languageVariants = ctx.getOctoberSkyData().getLanguageVariants(country, language);
                if(ctx.getOctoberSkyData().getOtherLanguageVariants(country, language).contains(originalLanguageBCPCodeForVideo)) {
                    languageVariants.add(originalLanguageBCPCodeForVideo);
                }
                for (String variant : languageVariants) {
                    longHollow = mapOfStringToLongHollow.get(variant);
                    if (longHollow != null) return longHollow._getValue();
                }
            }
            return null;
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


    boolean isGoLive(Status status) {
        Flags flags = status.getFlags();
        return flags != null && flags.getGoLive();
    }

    boolean isLanguageOverride(Status status) {
        Flags flags = status.getFlags();
        return flags != null && flags.getLanguageOverride();
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

    private static final Comparator<RightsWindow> RIGHTS_WINDOW_COMPARATOR = (o1, o2) -> {
        long t1 = o1.getOnHold() ? o1.getStartDate() + ONE_THOUSAND_YEARS : o1.getStartDate();
        long t2 = o2.getOnHold() ? o2.getStartDate() + ONE_THOUSAND_YEARS : o2.getStartDate();
        return t1 < t2 ? -1 : (t1 == t2 ? 0 : 1);
    };
}
