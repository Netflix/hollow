package com.netflix.vms.transformer.modules.packages.contracts;

import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;
import static com.netflix.vms.transformer.modules.packages.contracts.DownloadableAssetTypeIndex.Viewing.DOWNLOAD;
import static com.netflix.vms.transformer.modules.packages.contracts.DownloadableAssetTypeIndex.Viewing.STREAM;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.contract.ContractAsset;
import com.netflix.vms.transformer.contract.ContractAssetType;
import com.netflix.vms.transformer.data.CupTokenFetcher;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleEntryHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.hollowoutput.AvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.OfflineViewingRestrictions;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.contracts.DownloadableAssetTypeIndex.Viewing;
import com.netflix.vms.transformer.util.OutputUtil;
import com.netflix.vms.transformer.util.VideoContractUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/// Documentation of this logic available at: https://docs.google.com/document/d/15eGhbVPcEK_ARZA8OrtXpPAzrTalVqmZnKuzK_hrZAA/edit
public class ContractRestrictionModule {

    private final HollowHashIndex videoStatusIdx;

    private final VMSHollowInputAPI api;
    private final TransformerContext ctx;
    private final VMSTransformerIndexer indexer;
    private final CycleConstants cycleConstants;

    private final Map<String, CupKey> cupKeysMap;
    private final Map<String, Strings> bcp47Codes;

    private final StreamContractAssetTypeDeterminer assetTypeDeterminer;
    private final CupTokenFetcher cupTokenFetcher;

    public ContractRestrictionModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, VMSTransformerIndexer indexer,
            CupTokenFetcher cupTokenFetcher) {
        this.api = api;
        this.ctx = ctx;
        this.indexer = indexer;
        this.cycleConstants = cycleConstants;
        this.cupTokenFetcher = cupTokenFetcher;
        this.videoStatusIdx = indexer.getHashIndex(IndexSpec.ALL_VIDEO_STATUS);
        this.cupKeysMap = new HashMap<>();
        this.bcp47Codes = new HashMap<>();
        this.assetTypeDeterminer = new StreamContractAssetTypeDeterminer(api, indexer);
    }

    /**
     * For a given package in MPL feed, find a set of ContractRestriction for each country.
     * Note package data is not available per country.
     *
     * @param packageHollow
     * @return
     */
    public Map<ISOCountry, Set<ContractRestriction>> getContractRestrictions(PackageHollow packageHollow) {
        Map<ISOCountry, Set<ContractRestriction>> restrictions = new HashMap<>();

        // build an asset type index to look up excluded downloadables
        // this index is from downloadableId -> ContractAsset (assetType + language)
        DownloadableAssetTypeIndex assetTypeIdx = new DownloadableAssetTypeIndex();
        for (PackageStreamHollow stream : packageHollow._getDownloadables()) {
            ContractAssetType assetType = assetTypeDeterminer.getAssetType(stream);
            if (assetType == null)
                continue;
            String language = getLanguageForAsset(stream, assetType);
            assetTypeIdx.addDownloadableId(new ContractAsset(assetType, language), stream._getDownloadableId());
        }


        // iterate over the VideoStatus of every country
        HollowHashIndexResult statusResult = videoStatusIdx.findMatches(packageHollow._getMovieId());
        if (statusResult != null) {

            HollowOrdinalIterator iter = statusResult.iterator();
            int statusOrdinal = iter.next();
            while (statusOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {

                StatusHollow status = api.getStatusHollow(statusOrdinal);
                FlagsHollow rightsFlags = status._getFlags();
                if (rightsFlags == null || !rightsFlags._getGoLive()) {
                    statusOrdinal = iter.next();
                    continue;
                }

                int videoId = (int) packageHollow._getMovieId();
                String countryCode = status._getCountryCode()._getValue();
                Set<ContractRestriction> contractRestrictions = new HashSet<>();

                ListOfRightsWindowHollow windows = status._getRights()._getWindows();
                for (RightsWindowHollow window : windows) {

                    // create map of contractId to isAvailableForDownload
                    Map<Integer, Boolean> dealIds = new HashMap<>();
                    if (window._getContractIdsExt() != null) {
                        for (RightsWindowContractHollow contract : window._getContractIdsExt()) {
                        	dealIds.put(Integer.valueOf((int) contract._getDealId()), Boolean.valueOf(contract._getDownload()));                        		
                        }
                    }

                    assetTypeIdx.resetMarks();

                    List<RightsWindowContractHollow> applicableRightsContracts = new ArrayList<>();
                    if (window._getContractIdsExt() != null && !window._getContractIdsExt().isEmpty()) {
                        // make sure that package id in contract matches with the packageId under consideration
                        applicableRightsContracts = filterToApplicableContracts(packageHollow, window._getContractIdsExt(), dealIds);
                    }

                    if (applicableRightsContracts.size() > 0) {
                        ContractRestriction restriction;

                        if (applicableRightsContracts.size() == 1)
                            restriction = buildRestrictionBasedOnSingleApplicableContract(assetTypeIdx, applicableRightsContracts.get(0), videoId, countryCode);
                        else
                            restriction = buildRestrictionBasedOnMultipleApplicableContracts(assetTypeIdx, applicableRightsContracts, videoId, countryCode);
                        if (restriction.cupKeys.isEmpty()) {
                            restriction.cupKeys.add(getCupKey(CupKey.DEFAULT));
                        }

                        restriction.availabilityWindow = new AvailabilityWindow();
                        restriction.availabilityWindow.startDate = OutputUtil.getRoundedDate(window._getStartDate());
                        restriction.availabilityWindow.endDate = OutputUtil.getRoundedDate(window._getEndDate());
                        if (window._getOnHold()) {
                            restriction.availabilityWindow.startDate.val += ONE_THOUSAND_YEARS;
                            restriction.availabilityWindow.endDate.val += ONE_THOUSAND_YEARS;
                            restriction.availabilityWindow.onHold = true;
                        }

                        contractRestrictions.add(restriction);
                    }
                }

                if (!contractRestrictions.isEmpty())
                    restrictions.put(cycleConstants.getISOCountry(status._getCountryCode()._getValue()), contractRestrictions);

                statusOrdinal = iter.next();
            }
        }

        return restrictions;
    }

    private ContractRestriction newContractRestriction() {
        ContractRestriction restriction = new ContractRestriction();

        restriction.cupKeys = new ArrayList<>();
        restriction.languageBcp47RestrictionsMap = new HashMap<>();
        restriction.excludedDownloadables = new HashSet<>();

        return restriction;
    }

    private List<RightsWindowContractHollow> filterToApplicableContracts(PackageHollow packages, List<RightsWindowContractHollow> windowContracts, Map<Integer, Boolean> dealIds) {
        List<RightsWindowContractHollow> applicableContracts = new ArrayList<>(windowContracts.size());

        for (RightsWindowContractHollow windowContract : windowContracts) {
            Integer dealId = Integer.valueOf((int) windowContract._getDealId());
            Boolean isAvailableForDownload = dealIds.get(dealId);
            if (isAvailableForDownload != null && contractIsApplicableForPackage(windowContract, packages._getPackageId())) {
                applicableContracts.add(windowContract);
            }
        }
        return applicableContracts;
    }

    private boolean contractIsApplicableForPackage(RightsWindowContractHollow contract, long packageId) {
        if (contract._getPackageId() == packageId)
            return true;

        if (contract._getPackages() != null) {
            for (RightsContractPackageHollow pkg : contract._getPackages()) {
                if (pkg._getPackageId() == packageId)
                    return true;
            }
        }

        return false;
    }

    /**
     * Build a contract restriction based on single contract.
     *
     * @param assetTypeIdx
     * @param windowContractHollow
     * @param videoId
     * @param countryCode
     * @return
     */
    private ContractRestriction buildRestrictionBasedOnSingleApplicableContract(DownloadableAssetTypeIndex assetTypeIdx, RightsWindowContractHollow windowContractHollow, long videoId, String countryCode) {
        ContractRestriction restriction = newContractRestriction();

        ListOfRightsContractAssetHollow contractAssets = windowContractHollow._getAssets();
        if (!markAllAssetsIfNoAssetsPresent(assetTypeIdx, contractAssets))
            markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, contractAssets, STREAM);

        // get contract data from Contract feed published by beehive. This contract data has information on title, country, prepromotion and cup tokens and others.
        long dealId = windowContractHollow._getDealId();
        VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, countryCode, dealId);

        
        if (contractAttributes != null) {
            // Contract feed also has data on disAllowedBundles
            Set<DisallowedAssetBundleEntryHollow> disallowedAssetBundles = contractAttributes._getDisallowedAssetBundles();


            // for each disAllowedBundles, build a language restriction
            for (DisallowedAssetBundleEntryHollow disallowedAssetBundle : disallowedAssetBundles) {
                LanguageRestrictions langRestriction = new LanguageRestrictions();
                String audioLangStr = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                Strings audioLanguage = getBcp47Code(audioLangStr);
                langRestriction.audioLanguage = audioLanguage;
                langRestriction.audioLanguageId = LanguageIdMapping.getLanguageId(audioLangStr);
                if(disallowedAssetBundle._getForceSubtitle() != null)
                	langRestriction.requiresForcedSubtitles = disallowedAssetBundle._getForceSubtitle()._getValue();

                Set<Strings> disallowedTimedTextCodes = new HashSet<Strings>();
                Set<com.netflix.vms.transformer.hollowoutput.Integer> disallowedTimedTextIds = new HashSet<>();
                Set<StringHollow> disallowedSubtitles = disallowedAssetBundle._getDisallowedSubtitleLangCodes();

                for (StringHollow sub : disallowedSubtitles) {
                	String subLang = sub._getValue();
                    disallowedTimedTextCodes.add(getBcp47Code(subLang));
                    disallowedTimedTextIds.add(new com.netflix.vms.transformer.hollowoutput.Integer(LanguageIdMapping.getLanguageId(subLang)));
                }

                langRestriction.disallowedTimedText = Collections.emptySet();
                langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;
                langRestriction.disallowedTimedText = disallowedTimedTextIds;

                // create a restriction for each language in the current contract restriction.
                restriction.languageBcp47RestrictionsMap.put(audioLanguage, langRestriction);
            }

            String cupToken = cupTokenFetcher.getCupTokenString(videoId, contractAttributes);
            restriction.cupKeys.add(getCupKey(cupToken));
        }

        restriction.isAvailableForDownload = windowContractHollow._getDownload();

        finalizeContractRestriction(assetTypeIdx, restriction, contractAttributes);

        return restriction;
    }

    // we need to merge both the allowed asset types (for excluded downloadable calculations) and the language bundle restrictions
    // the language bundle restrictions logic is complicated and broken down into steps indicated in the comments.
    private ContractRestriction buildRestrictionBasedOnMultipleApplicableContracts(DownloadableAssetTypeIndex assetTypeIdx, List<RightsWindowContractHollow> applicableRightsContracts, long videoId, String countryCode) {

        // Whether some contract has different download rights than other.
        boolean downloadRightsDifferentForContracts = false;
        RightsWindowContractHollow firstContract = applicableRightsContracts.get(0);

        RightsWindowContractHollow selectedRightsContract = firstContract;
        for (RightsWindowContractHollow thisRightsContract : applicableRightsContracts) {
            // for unmerged fields, select the contract with the highest ID.
            if (thisRightsContract._getDealId() > selectedRightsContract._getDealId())
                selectedRightsContract = thisRightsContract;
            if (thisRightsContract._getDownload() != firstContract._getDownload())
                downloadRightsDifferentForContracts = true;
        }
        ContractRestriction restriction = buildIntermediateRestrictionBasedOnMultipleApplicableContracts(assetTypeIdx, applicableRightsContracts, videoId, countryCode, STREAM);

        // If all contracts have offline viewing rights then
        // no need to capture extra information for offline viewing restrictions.
        // Offline viewing restrictions can be answered using streaming restrictions
        // data on client side.
        // Refer to com.netflix.vms.type.hollow.stream.ContractRestrictionHollowImpl for more details.
        if (downloadRightsDifferentForContracts) {
            List<RightsWindowContractHollow> offlineViewingRightsContracts = applicableRightsContracts.stream()
                                                                                                      .filter(rc -> rc._getDownload())
                                                                                                      .collect(Collectors.toList());
            ContractRestriction offlineViewingContractRestriction = buildIntermediateRestrictionBasedOnMultipleApplicableContracts(assetTypeIdx, offlineViewingRightsContracts, videoId, countryCode, DOWNLOAD);

            restriction.offlineViewingRestrictions = new OfflineViewingRestrictions();
            restriction.offlineViewingRestrictions.downloadOnlyCupKeys = offlineViewingContractRestriction.cupKeys;
            restriction.offlineViewingRestrictions.downloadLanguageBcp47RestrictionsMap = offlineViewingContractRestriction.languageBcp47RestrictionsMap;
        }

        long dealId = selectedRightsContract._getDealId();
        VmsAttributeFeedEntryHollow selectedContractAttribute = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, countryCode, dealId);
        finalizeContractRestriction(assetTypeIdx, restriction, selectedContractAttribute);

        return restriction;
    }

    private ContractRestriction buildIntermediateRestrictionBasedOnMultipleApplicableContracts(
            DownloadableAssetTypeIndex assetTypeIdx,
            List<RightsWindowContractHollow> applicableRightsContracts, long videoId, String countryCode,
            Viewing viewing) {
        ContractRestriction restriction = newContractRestriction();

        // Step 1: gather all of the audio languages which have language bundle restrictions
        Set<String> audioLanguagesWithDisallowedAssetBundles = new HashSet<String>();
        Set<String> audioLanguagesWhichRequireForcedSubtitles = new HashSet<String>();
        Map<Integer, String> orderedContractIdCupKeyMap = new TreeMap<Integer, String>();

        for (RightsWindowContractHollow thisRightsContract : applicableRightsContracts) {
            markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, thisRightsContract._getAssets(), viewing);

            long dealId = thisRightsContract._getDealId();
            VmsAttributeFeedEntryHollow contractAttribute = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, countryCode, dealId);
            if (contractAttribute != null) {
                for (DisallowedAssetBundleEntryHollow disallowedAssetBundle : contractAttribute._getDisallowedAssetBundles()) {
                    String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();

                    if (!disallowedAssetBundle._getDisallowedSubtitleLangCodes().isEmpty()) {
                        audioLanguagesWithDisallowedAssetBundles.add(audioLang);
                    }

                    audioLanguagesWhichRequireForcedSubtitles.add(audioLang);
                }
            }

            String cupKey = cupTokenFetcher.getCupTokenString(videoId, contractAttribute);
            orderedContractIdCupKeyMap.put((int) dealId, cupKey);

            // if any rights contract is downloadable, then the package is downloadable.
            restriction.isAvailableForDownload |= thisRightsContract._getDownload();
        }

        // Step 2: if any audio languages have language bundle restrictions, then determine the merged set of disallowed text languages for each
        // for the purposes of merging, we aim to minimize the number of restrictions -- if an asset combination is disallowed via one contract,
        // but allowed via another contract, then we ultimately want to *allow* that combination.
        Map<String, MergeableTextLanguageBundleRestriction> mergedTextLangaugeRestrictions = Collections.emptyMap();

        if (!audioLanguagesWithDisallowedAssetBundles.isEmpty()) {
            Map<String, MergeableTextLanguageBundleRestriction> mergedTextLanguageRestrictions = new HashMap<String, MergeableTextLanguageBundleRestriction>();
            for (RightsWindowContractHollow rightsContract : applicableRightsContracts) {
                // find the set of allowed text languages from this contract.
                Set<String> overallContractAllowedTextLanguages = new HashSet<String>();
                for (RightsContractAssetHollow assetInput : rightsContract._getAssets()) {
                    try {
                        ContractAsset asset = cycleConstants.rightsContractAssetCache.getResult(assetInput.getOrdinal());
                        if (asset == null) {
                            asset = new ContractAsset(assetInput);
                            cycleConstants.rightsContractAssetCache.setResult(assetInput.getOrdinal(), asset);
                        }

                        if (asset.getType() == ContractAssetType.SUBTITLES) {
                            overallContractAllowedTextLanguages.add(asset.getLanguage());
                        }
                    } catch (RuntimeException e) {
                        throw new RuntimeException("Error getting assets for videoId " + videoId
                                + " and rightsContract " + rightsContract, e);
                    }
                }

                // for each audio language where there is a disallowed asset bundle,
                // merge the disallowed text languages, AND all available text languages
                // which were *not* disallowed, are allowed.
                Set<String> bundleRestrictedAudioLanguagesFromThisContract = new HashSet<String>();
                long dealId = rightsContract._getDealId();
                VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, countryCode, dealId);
                if (contractAttributes != null) {
                    for (DisallowedAssetBundleEntryHollow disallowedAssetBundle : contractAttributes._getDisallowedAssetBundles()) {
                        String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                        MergeableTextLanguageBundleRestriction textRestriction = getMergeableTextRestrictionsByAudioLang(mergedTextLanguageRestrictions, audioLang);

                        Set<StringHollow> disallowedSubtitleLangCodes = disallowedAssetBundle._getDisallowedSubtitleLangCodes();
                        if (!disallowedSubtitleLangCodes.isEmpty()) {
                            // For this audio language, we need to modify the text languages allowed for this contract by removing each
                            // disallowed text language from the set.
                            Set<String> thisAudioLanguageAllowedTextLanguages = new HashSet<String>(overallContractAllowedTextLanguages);
                            for (StringHollow lang : disallowedSubtitleLangCodes) {
                                String textLang = lang._getValue();
                                thisAudioLanguageAllowedTextLanguages.remove(textLang);
                                textRestriction.addDisallowedTextLanguage(textLang);
                            }

                            textRestriction.addAllowedTextLanguages(thisAudioLanguageAllowedTextLanguages);
                            // don't process this audio language for this contract again.
                            bundleRestrictedAudioLanguagesFromThisContract.add(disallowedAssetBundle._getAudioLanguageCode()._getValue());
                        }
                    }
                }

                // for each audio language where there was *not* a disallowed asset bundle, all available text
                // languages for the contract are allowed.
                for (String audioLanguage : audioLanguagesWithDisallowedAssetBundles) {
                    if (!bundleRestrictedAudioLanguagesFromThisContract.contains(audioLanguage)) {
                        MergeableTextLanguageBundleRestriction textRestriction = getMergeableTextRestrictionsByAudioLang(mergedTextLanguageRestrictions, audioLanguage);
                        textRestriction.addAllowedTextLanguages(overallContractAllowedTextLanguages);
                    }
                }
            }
        }

        // Step 3: If any contract doesn't require forced subtitles for a particular language, then don't
        // require forced subtitles for that language
        if (!audioLanguagesWhichRequireForcedSubtitles.isEmpty()) {
            for (RightsWindowContractHollow rightsContract : applicableRightsContracts) {
                Set<String> forcedSubtitleLanguagesForThisContract = new HashSet<String>();

                long dealId = rightsContract._getDealId();
                VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, videoId, countryCode, dealId);
                if (contractAttributes != null) {
                    for (DisallowedAssetBundleEntryHollow assetBundle : contractAttributes._getDisallowedAssetBundles()) {
                        if (assetBundle._getForceSubtitle() != null && assetBundle._getForceSubtitle()._getValue())
                            forcedSubtitleLanguagesForThisContract.add(assetBundle._getAudioLanguageCode()._getValue());
                    }
                }

                audioLanguagesWhichRequireForcedSubtitles.retainAll(forcedSubtitleLanguagesForThisContract);
            }
        }

        Set<String> restrictedAudioLanguages = audioLanguagesWithDisallowedAssetBundles;
        restrictedAudioLanguages.addAll(audioLanguagesWhichRequireForcedSubtitles);

        for (String audioLang : restrictedAudioLanguages) {
            Set<String> disallowedTextLangauges = Collections.emptySet();

            MergeableTextLanguageBundleRestriction mergeableTextLanguageBundleRestriction = mergedTextLangaugeRestrictions.get(audioLang);
            if (mergeableTextLanguageBundleRestriction != null) {
                disallowedTextLangauges = mergeableTextLanguageBundleRestriction.getFinalDisallowedTextLanguages();
            }

            boolean requiresForcedSubtitles = audioLanguagesWhichRequireForcedSubtitles.contains(audioLang);

            if (requiresForcedSubtitles || !disallowedTextLangauges.isEmpty()) {
                LanguageRestrictions langRestriction = new LanguageRestrictions();
                langRestriction.audioLanguage = getBcp47Code(audioLang);
                langRestriction.audioLanguageId = LanguageIdMapping.getLanguageId(audioLang);
                langRestriction.requiresForcedSubtitles = requiresForcedSubtitles;

                Set<Strings> disallowedTimedTextCodes = new HashSet<>();
                Set<com.netflix.vms.transformer.hollowoutput.Integer> disallowedTimedTextIds = new HashSet<>();

                for (String textLang : disallowedTextLangauges) {
                    disallowedTimedTextCodes.add(getBcp47Code(textLang));
                    disallowedTimedTextIds.add(new com.netflix.vms.transformer.hollowoutput.Integer(LanguageIdMapping.getLanguageId(textLang)));
                }

                langRestriction.disallowedTimedText = Collections.emptySet();
                langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;
                langRestriction.disallowedTimedText = disallowedTimedTextIds;

                restriction.languageBcp47RestrictionsMap.put(langRestriction.audioLanguage, langRestriction);
            }

        }

        for (String cupToken : new LinkedHashSet<String>(orderedContractIdCupKeyMap.values()))
            restriction.cupKeys.add(getCupKey(cupToken));

        return restriction;
    }

    private MergeableTextLanguageBundleRestriction getMergeableTextRestrictionsByAudioLang(Map<String, MergeableTextLanguageBundleRestriction> mergedLanguageBundleRestrictions,
                                                                                           String audioLanguageCode) {
        MergeableTextLanguageBundleRestriction mergeableRestriction = mergedLanguageBundleRestrictions.get(audioLanguageCode);
        if (mergeableRestriction == null) {
            mergeableRestriction = new MergeableTextLanguageBundleRestriction();
            mergedLanguageBundleRestrictions.put(audioLanguageCode, mergeableRestriction);
        }
        return mergeableRestriction;
    }

    private CupKey getCupKey(String cupToken) {
        CupKey cupKey = cupKeysMap.get(cupToken);
        if (cupKey == null) {
            cupKey = new CupKey(new Strings(cupToken));
            cupKeysMap.put(cupToken, cupKey);
        }
        return cupKey;
    }

    // If there are no assets present for a single-contract window, don't list any excluded downloadables.
    // This seems wrong -- If there *were* asset(s) present, but none of them matched
    // available streams, then we would have indicated instead that all downloadable ids were excluded.
    private boolean markAllAssetsIfNoAssetsPresent(DownloadableAssetTypeIndex assetTypeIdx, ListOfRightsContractAssetHollow contractAssets) {
        boolean emptyAssets = contractAssets.isEmpty();
        if (emptyAssets)
            assetTypeIdx.markAll(STREAM);
        return emptyAssets;
    }

    private void markAssetTypeIndexForExcludedDownloadablesCalculation(DownloadableAssetTypeIndex assetTypeIdx, ListOfRightsContractAssetHollow contractAssets, Viewing viewing) {
        for (RightsContractAssetHollow assetInput : contractAssets) {
            ContractAsset asset = cycleConstants.rightsContractAssetCache.getResult(assetInput.getOrdinal());
            if (asset == null) {
                asset = new ContractAsset(assetInput);
                cycleConstants.rightsContractAssetCache.setResult(assetInput.getOrdinal(), asset);
            }

            assetTypeIdx.mark(asset, viewing);
        }
    }

    /**
     * Finalize the restriction by using the assetTypeIndex to get all unmarked assets (excludedDownloadables), availableAssets (getAllMarkedForStreamingAssets)
     *
     * @param assetTypeIdx
     * @param restriction
     * @param selectedContract
     */
    private void finalizeContractRestriction(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, VmsAttributeFeedEntryHollow selectedContractAttribute) {
        if (selectedContractAttribute != null &&  
        		selectedContractAttribute._getPrePromotionDays() != null && 
        		selectedContractAttribute._getPrePromotionDays()._getValue() != Long.MIN_VALUE)
            restriction.prePromotionDays = (int) selectedContractAttribute._getPrePromotionDays()._getValue();

        restriction.excludedDownloadables = assetTypeIdx.getAllUnmarked();
        restriction.availableAssets = assetTypeIdx.getAllMarkedForStreamingAssets();

        // Offline viewing rights
        if (restriction.offlineViewingRestrictions != null) {
            restriction.offlineViewingRestrictions.streamOnlyDownloadables = assetTypeIdx.getAllUnmarkedForDownloadAndMarkedForStreaming();
            restriction.offlineViewingRestrictions.downloadableAssets = assetTypeIdx.getAllMarkedForDownloadAssets();
            if (isNoExtraOfflineViewingRestrictions(restriction)) restriction.offlineViewingRestrictions = null;
        }
    }

    private boolean isNoExtraOfflineViewingRestrictions(ContractRestriction restriction) {
        // Offline viewing rights: optimization. 
        // If all contracts have same download rights OR
        // cupkeys, excluded downloadables and language restrictions are all same as streaming ones
        // set offline restrictions to null. 
        // Assumption: Client side logic will have enough information to return appropriate values
        // in the cases where offline restriction is null.
        return restriction.offlineViewingRestrictions != null &&
                restriction.offlineViewingRestrictions.streamOnlyDownloadables.isEmpty() &&
                restriction.cupKeys.equals(restriction.offlineViewingRestrictions.downloadOnlyCupKeys) &&
                restriction.languageBcp47RestrictionsMap.equals(restriction.offlineViewingRestrictions.downloadLanguageBcp47RestrictionsMap);
    }

    private String getLanguageForAsset(PackageStreamHollow stream, ContractAssetType assetType) {
        StreamNonImageInfoHollow nonImageInfo = stream._getNonImageInfo();
        if (assetType == ContractAssetType.SUBTITLES) {
            return nonImageInfo._getTextInfo()._getTextLanguageCode()._getValue();
        }

        if (nonImageInfo != null) {
            AudioStreamInfoHollow audioInfo = nonImageInfo._getAudioInfo();
            if (audioInfo != null) {
                StringHollow audioLangCode = audioInfo._getAudioLanguageCode();
                if (audioLangCode != null) {
                    return audioLangCode._getValue();
                }
            }
        }
        return null;
    }

    private Strings getBcp47Code(String code) {
        Strings bcp47Code = bcp47Codes.get(code);
        if (bcp47Code == null) {
            bcp47Code = new Strings(code);
            bcp47Codes.put(code, bcp47Code);
        }
        return bcp47Code;
    }
}
