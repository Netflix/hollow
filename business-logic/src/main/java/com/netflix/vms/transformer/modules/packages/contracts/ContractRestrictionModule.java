package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.ContractIdHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodeHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodesListHollow;
import com.netflix.vms.transformer.hollowinput.FlagsHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.AvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
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

/// Documentation of this logic available at: https://docs.google.com/document/d/15eGhbVPcEK_ARZA8OrtXpPAzrTalVqmZnKuzK_hrZAA/edit
public class ContractRestrictionModule {

    private final HollowHashIndex videoStatusIdx;
    // private final HollowPrimaryKeyIndex bcp47CodeIdx;

    private final VMSHollowInputAPI api;
    private final VMSTransformerIndexer indexer;

    private final Map<String, CupKey> cupKeysMap;
    private final Map<String, Strings> bcp47Codes;

    private final StreamContractAssetTypeDeterminer assetTypeDeterminer;
    
    private final boolean isOfflineViewingEnabled;

    public ContractRestrictionModule(VMSHollowInputAPI api, TransformerContext ctx, VMSTransformerIndexer indexer) {
        this.api = api;
        this.indexer = indexer;
        this.videoStatusIdx = indexer.getHashIndex(IndexSpec.ALL_VIDEO_STATUS);
        // this.bcp47CodeIdx = indexer.getPrimaryKeyIndex(IndexSpec.BCP47_CODE);
        this.cupKeysMap = new HashMap<String, CupKey>();
        this.bcp47Codes = new HashMap<String, Strings>();
        this.assetTypeDeterminer = new StreamContractAssetTypeDeterminer(api, indexer);
        
        this.isOfflineViewingEnabled = ctx.getConfig().isOfflineViewingEnabled();
    }

    public Map<ISOCountry, Set<ContractRestriction>> getContractRestrictions(PackageHollow packages) {
        assetTypeDeterminer.clearCache();

        Map<ISOCountry, Set<ContractRestriction>> restrictions = new HashMap<ISOCountry, Set<ContractRestriction>>();

        // build an asset type index to look up excluded downloadables
        DownloadableAssetTypeIndex assetTypeIdx = new DownloadableAssetTypeIndex();

        for (PackageStreamHollow stream : packages._getDownloadables()) {
            String assetType = assetTypeDeterminer.getAssetType(stream);

            if (assetType == null)
                continue;

            String language = getLanguageForAsset(stream, assetType);

            assetTypeIdx.addDownloadableId(new ContractAssetType(assetType, language), stream._getDownloadableId());
        }

        // iterate over the VideoStatus of every country
        HollowHashIndexResult statusResult = videoStatusIdx.findMatches(packages._getMovieId());
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

                int videoId = (int) packages._getMovieId();
                String countryCode = status._getCountryCode()._getValue();

                Set<ContractRestriction> contractRestrictions = new HashSet<ContractRestriction>();

                ListOfRightsWindowHollow windows = status._getRights()._getWindows();
                ListOfRightsContractHollow rightsContracts = status._getRights()._getContracts();
                for (RightsWindowHollow window : windows) {
                    Map<Integer, Boolean> contractIds = new HashMap<>();

                    if(isOfflineViewingEnabled) {
                    	for(RightsWindowContractHollow contract : window._getContractIdsExt()) {
                    		contractIds.put(Integer.valueOf((int)contract._getContractId()), Boolean.valueOf(contract._getDownload()));
                    	}
                    } else {
	                    for (ContractIdHollow contractId : window._getContractIds()) {
	                        contractIds.put(Integer.valueOf((int) contractId._getValue()), Boolean.FALSE);
	                    }
                    }

                    ContractRestriction restriction = new ContractRestriction();

                    restriction.availabilityWindow = new AvailabilityWindow();
                    restriction.availabilityWindow.startDate = OutputUtil.getRoundedDate(window._getStartDate());
                    restriction.availabilityWindow.endDate = OutputUtil.getRoundedDate(window._getEndDate());

                    restriction.cupKeys = new ArrayList<CupKey>();
                    restriction.languageBcp47RestrictionsMap = new HashMap<Strings, LanguageRestrictions>();
                    restriction.excludedDownloadables = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

                    assetTypeIdx.resetMarks();

                    List<RightsWindowContract> applicableRightsContracts = filterToApplicableContracts(packages, rightsContracts, contractIds);

                    if (applicableRightsContracts.size() > 0) {
                        if (applicableRightsContracts.size() == 1)
                            buildRestrictionBasedOnSingleApplicableContract(assetTypeIdx, restriction, applicableRightsContracts.get(0), videoId, countryCode);
                        else
                            buildRestrictionBasedOnMultipleApplicableContracts(assetTypeIdx, restriction, applicableRightsContracts, videoId, countryCode);
                        if (restriction.cupKeys.isEmpty()) {
                            restriction.cupKeys.add(getCupKey(CupKey.DEFAULT));
                        }
                        contractRestrictions.add(restriction);
                    }
                }

                if (!contractRestrictions.isEmpty())
                    restrictions.put(new ISOCountry(status._getCountryCode()._getValue()), contractRestrictions);

                statusOrdinal = iter.next();
            }
        }

        return restrictions;
    }

    private List<RightsWindowContract> filterToApplicableContracts(PackageHollow packages, ListOfRightsContractHollow contracts, Map<Integer, Boolean> contractIds) {
        List<RightsWindowContract> applicableContracts = new ArrayList<>(contracts.size());
        for (RightsContractHollow contract : contracts) {
        	Integer contractId = Integer.valueOf((int)contract._getContractId());
			Boolean isDownloadable = contractIds.get(contractId);
            if (isDownloadable != null && contractIsApplicableForPackage(contract, packages._getPackageId())) {
                applicableContracts.add(new RightsWindowContract(contractId.intValue(), contract, isDownloadable));
            }
        }
        return applicableContracts;
    }

    private boolean contractIsApplicableForPackage(RightsContractHollow contract, long packageId) {
        if (contract._getPackageId() == packageId)
            return true;

        for (RightsContractPackageHollow pkg : contract._getPackages()) {
            if (pkg._getPackageId() == packageId)
                return true;
        }

        return false;
    }

    private void buildRestrictionBasedOnSingleApplicableContract(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, RightsWindowContract rightsContract, long videoId, String countryCode) {
        ListOfRightsContractAssetHollow contractAssets = rightsContract.contract._getAssets();
        if(!markAllAssetsIfNoAssetsPresent(assetTypeIdx, contractAssets))
            markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, contractAssets);

        ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, rightsContract.contractId);
        if (contract!=null) {
            List<DisallowedAssetBundleHollow> disallowedAssetBundles = contract._getDisallowedAssetBundles();
            for (DisallowedAssetBundleHollow disallowedAssetBundle : disallowedAssetBundles) {
                LanguageRestrictions langRestriction = new LanguageRestrictions();
                String audioLangStr = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                Strings audioLanguage = getBcp47Code(audioLangStr);
                langRestriction.audioLanguage = audioLanguage;
                langRestriction.audioLanguageId = LanguageIdMapping.getLanguageId(audioLangStr);
                langRestriction.requiresForcedSubtitles = disallowedAssetBundle._getForceSubtitle();

                Set<Strings> disallowedTimedTextCodes = new HashSet<Strings>();
                Set<com.netflix.vms.transformer.hollowoutput.Integer> disallowedTimedTextIds = new HashSet<>();
                List<DisallowedSubtitleLangCodeHollow> disallowedSubtitles = disallowedAssetBundle._getDisallowedSubtitleLangCodes();

                for (DisallowedSubtitleLangCodeHollow sub : disallowedSubtitles) {
                    String subLang = sub._getValue()._getValue();
                    disallowedTimedTextCodes.add(getBcp47Code(subLang));
                    disallowedTimedTextIds.add(new com.netflix.vms.transformer.hollowoutput.Integer(LanguageIdMapping.getLanguageId(subLang)));
                }

                langRestriction.disallowedTimedText = Collections.emptySet();
                langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;
                langRestriction.disallowedTimedText = disallowedTimedTextIds;

                restriction.languageBcp47RestrictionsMap.put(audioLanguage, langRestriction);
            }

            String cupToken = contract._getCupToken()._getValue();
            restriction.cupKeys.add(getCupKey(cupToken));
        }

        restriction.isDownloadable = rightsContract.isDownloadable;
        
        finalizeContractRestriction(assetTypeIdx, restriction, contract);
    }

    // we need to merge both the allowed asset types (for excluded downloadable calculations) and the language bundle restrictions
    // the language bundle restrictions logic is complicated and broken down into steps indicated in the comments.
    private void buildRestrictionBasedOnMultipleApplicableContracts(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, List<RightsWindowContract> applicableRightsContracts, long videoId, String countryCode) {
        RightsWindowContract selectedRightsContract = null;

        // Step 1: gather all of the audio languages which have language bundle restrictions
        Set<String> audioLanguagesWithDisallowedAssetBundles = new HashSet<String>();
        Set<String> audioLanguagesWhichRequireForcedSubtitles = new HashSet<String>();
        Map<Integer, String> orderedContractIdCupKeyMap = new TreeMap<Integer, String>();

        for (RightsWindowContract rightsContract : applicableRightsContracts) {
            // // for unmerged fields, select the contract with the highest ID.
            if (selectedRightsContract == null || rightsContract.contractId > selectedRightsContract.contractId)
                selectedRightsContract = rightsContract;

            markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, rightsContract.contract._getAssets());

            ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, rightsContract.contract._getContractId());
            if (contract != null) {
                for (DisallowedAssetBundleHollow disallowedAssetBundle : contract._getDisallowedAssetBundles()) {
                    String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();

                    if (!disallowedAssetBundle._getDisallowedSubtitleLangCodes().isEmpty()) {
                        audioLanguagesWithDisallowedAssetBundles.add(audioLang);
                    }

                    audioLanguagesWhichRequireForcedSubtitles.add(audioLang);
                }
            }

            /// if any rights contract is downloadable, then the package is downloadable.
            if(rightsContract.isDownloadable)
            	restriction.isDownloadable = true;
            
            StringHollow cupKey = contract == null ? null : contract._getCupToken();
            orderedContractIdCupKeyMap.put((int) rightsContract.contract._getContractId(), cupKey == null ? CupKey.DEFAULT : cupKey._getValue());
        }

        // Step 2: if any audio languages have language bundle restrictions, then determine the merged set of disallowed text languages for each
        // for the purposes of merging, we aim to minimize the number of restrictions -- if an asset combination is disallowed via one contract,
        // but allowed via another contract, then we ultimately want to *allow* that combination.
        Map<String, MergeableTextLanguageBundleRestriction> mergedTextLangaugeRestrictions = Collections.emptyMap();

        if (!audioLanguagesWithDisallowedAssetBundles.isEmpty()) {
            Map<String, MergeableTextLanguageBundleRestriction> mergedTextLanguageRestrictions = new HashMap<String, MergeableTextLanguageBundleRestriction>();
            for (RightsWindowContract rightsContract : applicableRightsContracts) {
                // find the set of allowed text languages from this contract.
                Set<String> overallContractAllowedTextLanguages = new HashSet<String>();
                for (RightsContractAssetHollow asset : rightsContract.contract._getAssets()) {
                    String contractAssetType = asset._getAssetType()._getValue();

                    if (StreamContractAssetTypeDeterminer.CLOSEDCAPTIONING.equals(contractAssetType) || StreamContractAssetTypeDeterminer.SUBTITLES.equals(contractAssetType)) {
                        overallContractAllowedTextLanguages.add(asset._getBcp47Code()._getValue());
                    }
                }

                // for each audio language where there is a disallowed asset bundle,
                // merge the disallowed text languages, AND all available text languages
                // which were *not* disallowed, are allowed.
                Set<String> bundleRestrictedAudioLanguagesFromThisContract = new HashSet<String>();
                ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, rightsContract.contractId);
                if (contract != null) {
                    for (DisallowedAssetBundleHollow disallowedAssetBundle : contract._getDisallowedAssetBundles()) {
                        String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                        MergeableTextLanguageBundleRestriction textRestriction = getMergeableTextRestrictionsByAudioLang(mergedTextLanguageRestrictions, audioLang);

                        DisallowedSubtitleLangCodesListHollow disallowedSubtitleLangCodes = disallowedAssetBundle._getDisallowedSubtitleLangCodes();
                        if (!disallowedSubtitleLangCodes.isEmpty()) {
                            // For this audio language, we need to modify the text languages allowed for this contract by removing each
                            // disallowed text language from the set.
                            Set<String> thisAudioLanguageAllowedTextLanguages = new HashSet<String>(overallContractAllowedTextLanguages);
                            for (DisallowedSubtitleLangCodeHollow lang : disallowedSubtitleLangCodes) {
                                String textLang = lang._getValue()._getValue();
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
            for (RightsWindowContract rightsContract : applicableRightsContracts) {
                Set<String> forcedSubtitleLanguagesForThisContract = new HashSet<String>();

                ContractHollow contract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, rightsContract.contractId);
                if (contract != null) {
                    for (DisallowedAssetBundleHollow assetBundle : contract._getDisallowedAssetBundles()) {
                        if (assetBundle._getForceSubtitle())
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

        ContractHollow selectedContract = VideoContractUtil.getContract(api, indexer, videoId, countryCode, selectedRightsContract.contractId);
        finalizeContractRestriction(assetTypeIdx, restriction, selectedContract);
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
        if(emptyAssets)
            assetTypeIdx.markAll();
        return emptyAssets;
    }

    private void markAssetTypeIndexForExcludedDownloadablesCalculation(DownloadableAssetTypeIndex assetTypeIdx, ListOfRightsContractAssetHollow contractAssets) {
        for (RightsContractAssetHollow asset : contractAssets) {
            String contractAssetType = asset._getAssetType()._getValue();

            if (StreamContractAssetTypeDeterminer.CLOSEDCAPTIONING.equals(contractAssetType))
                contractAssetType = StreamContractAssetTypeDeterminer.SUBTITLES;
            if (StreamContractAssetTypeDeterminer.SECONDARY_AUDIO.equals(contractAssetType))
                contractAssetType = StreamContractAssetTypeDeterminer.PRIMARYVIDEO_AUDIOMUXED;

            assetTypeIdx.mark(new ContractAssetType(contractAssetType, asset._getBcp47Code()._getValue()));
        }
    }

    private void finalizeContractRestriction(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, ContractHollow selectedContract) {
        if (selectedContract != null && selectedContract._getPrePromotionDays() != Long.MIN_VALUE)
            restriction.prePromotionDays = (int) selectedContract._getPrePromotionDays();

        restriction.excludedDownloadables = assetTypeIdx.getAllUnmarked();
    }

    private String getLanguageForAsset(PackageStreamHollow stream, String assetType) {
        StreamNonImageInfoHollow nonImageInfo = stream._getNonImageInfo();
        if (StreamContractAssetTypeDeterminer.SUBTITLES.equals(assetType)) {
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
    
    private static class RightsWindowContract {
    	private final int contractId;
    	private final RightsContractHollow contract;
    	private final boolean isDownloadable;
    	
		public RightsWindowContract(int contractId, RightsContractHollow contract, boolean isDownloadable) {
			this.contractId = contractId;
			this.contract = contract;
			this.isDownloadable = isDownloadable;
		}
    }

}
