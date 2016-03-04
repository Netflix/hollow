package com.netflix.vms.transformer.modules.packages;

import java.util.Collections;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodesListHollow;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.hollowinput.AudioStreamInfoHollow;
import com.netflix.vms.transformer.hollowinput.Bcp47CodeHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodeHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractIdHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowoutput.AvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.ContractRestriction;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// Documentation of this logic available at: https://docs.google.com/document/d/15eGhbVPcEK_ARZA8OrtXpPAzrTalVqmZnKuzK_hrZAA/edit
public class ContractRestrictionModule {

    private final HollowHashIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex bcp47CodeIdx;

    private final VMSHollowVideoInputAPI api;

    private final Map<String, CupKey> cupKeysMap;
    private final Map<String, Strings> bcp47Codes;
    private final Map<String, com.netflix.vms.transformer.hollowoutput.Integer> bcp47Ids;

    private final StreamContractAssetTypeDeterminer assetTypeDeterminer;

    public ContractRestrictionModule(VMSHollowVideoInputAPI api , VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getHashIndex(IndexSpec.ALL_VIDEO_RIGHTS);
        this.bcp47CodeIdx = indexer.getPrimaryKeyIndex(IndexSpec.BCP47_CODE);
        this.cupKeysMap = new HashMap<String, CupKey>();
        this.bcp47Codes = new HashMap<String, Strings>();
        this.bcp47Ids = new HashMap<String, com.netflix.vms.transformer.hollowoutput.Integer>();
        this.assetTypeDeterminer = new StreamContractAssetTypeDeterminer(api, indexer);
    }

    public Map<ISOCountry, Set<ContractRestriction>> getContractRestrictions(PackagesHollow packages) {
        assetTypeDeterminer.clearCache();

        Map<ISOCountry, Set<ContractRestriction>> restrictions = new HashMap<ISOCountry, Set<ContractRestriction>>();

        //// build an asset type index to look up excluded downloadables
        DownloadableAssetTypeIndex assetTypeIdx = new DownloadableAssetTypeIndex();

        for(PackageStreamHollow stream : packages._getDownloadables()) {
            String assetType = assetTypeDeterminer.getAssetType(stream);

            if(assetType == null)
                continue;

            String language = getLanguageForAsset(stream, assetType);

            assetTypeIdx.addDownloadableId(new ContractAssetType(assetType, language), stream._getDownloadableId());
        }

        //// iterate over the VideoRights of every country
        HollowHashIndexResult videoRightsResult = videoRightsIdx.findMatches(packages._getMovieId());
        HollowOrdinalIterator iter = videoRightsResult.iterator();
        int videoRightsOrdinal = iter.next();

        while(videoRightsOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            VideoRightsHollow rights = api.getVideoRightsHollow(videoRightsOrdinal);

            VideoRightsFlagsHollow rightsFlags = rights._getFlags();
            if(rightsFlags == null || !rightsFlags._getGoLive()) {
                videoRightsOrdinal = iter.next();
                continue;
            }

            Set<ContractRestriction> contractRestrictions = new HashSet<ContractRestriction>();

            Set<VideoRightsWindowHollow> windows = rights._getRights()._getWindows();
            Set<VideoRightsContractHollow> contracts = rights._getRights()._getContracts();
            for(VideoRightsWindowHollow window : windows) {
                Set<Integer> contractIds = new HashSet<Integer>();

                for(VideoRightsContractIdHollow contractId : window._getContractIds()) {
                    contractIds.add(Integer.valueOf((int)contractId._getValue()));
                }

                ContractRestriction restriction = new ContractRestriction();

                restriction.availabilityWindow = new AvailabilityWindow();
                restriction.availabilityWindow.startDate = new Date(window._getStartDate()._getValue());
                restriction.availabilityWindow.endDate = new Date(window._getEndDate()._getValue());

                restriction.cupKeys = new ArrayList<CupKey>();
                restriction.languageBcp47RestrictionsMap = new HashMap<Strings, LanguageRestrictions>();
                restriction.excludedDownloadables = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

                assetTypeIdx.resetMarks();

                List<VideoRightsContractHollow> applicableContracts = filterToApplicableContracts(packages, contracts, contractIds);

                if(packages._getPackageId() == 334836 && rights._getCountryCode()._isValueEqual("MQ"))
                    System.out.println("asdf");

                if(applicableContracts.size() > 0) {
                    if(applicableContracts.size() == 1)
                        buildRestrictionBasedOnSingleApplicableContract(assetTypeIdx, restriction, applicableContracts.get(0));
                    else
                        buildRestrictionBasedOnMultipleApplicableContracts(assetTypeIdx, restriction, applicableContracts);
                    contractRestrictions.add(restriction);
                }
            }

            if(!contractRestrictions.isEmpty())
                restrictions.put(new ISOCountry(rights._getCountryCode()._getValue()), contractRestrictions);

            videoRightsOrdinal = iter.next();
        }

        return restrictions;
    }

    private List<VideoRightsContractHollow> filterToApplicableContracts(PackagesHollow packages, Set<VideoRightsContractHollow> contracts, Set<Integer> contractIds) {
        List<VideoRightsContractHollow> applicableContracts = new ArrayList<VideoRightsContractHollow>(contracts.size());
        for(VideoRightsContractHollow contract : contracts) {
            if(contractIds.contains((int) contract._getContractId()) && contractIsApplicableForPackage(contract, packages._getPackageId())) {
                applicableContracts.add(contract);
            }
        }
        return applicableContracts;
    }

    private boolean contractIsApplicableForPackage(VideoRightsContractHollow contract, long packageId) {
        if(contract._getPackageId() == packageId)
            return true;

        for(VideoRightsContractPackageHollow pkg : contract._getPackages()) {
            if(pkg._getPackageId() == packageId)
                return true;
        }

        return false;
    }

    /// we need to merge both the allowed asset types (for excluded downloadable calculations) and the language bundle restrictions
    /// the language bundle restrictions logic is complicated and broken down into steps indicated in the comments.
    private void buildRestrictionBasedOnMultipleApplicableContracts(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, List<VideoRightsContractHollow> applicableContracts) {
        VideoRightsContractHollow selectedContract = null;

        /// Step 1: gather all of the audio languages which have language bundle restrictions
        Set<String> audioLanguagesWithDisallowedAssetBundles = new HashSet<String>();
        Set<String> audioLanguagesWhichRequireForcedSubtitles = new HashSet<String>();
        for(VideoRightsContractHollow contract : applicableContracts) {
            //// for unmerged fields, select the contract with the highest ID.
            if(selectedContract == null || contract._getContractId() > selectedContract._getContractId())
                selectedContract = contract;

            markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, contract);

            for(DisallowedAssetBundleHollow disallowedAssetBundle : contract._getDisallowedAssetBundles()) {
                String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();

                if(!disallowedAssetBundle._getDisallowedSubtitleLangCodes().isEmpty()) {
                    audioLanguagesWithDisallowedAssetBundles.add(audioLang);
                }

                audioLanguagesWhichRequireForcedSubtitles.add(audioLang);
            }
        }


        /// Step 2: if any audio languages have language bundle restrictions, then determine the merged set of disallowed text languages for each
        /// for the purposes of merging, we aim to minimize the number of restrictions -- if an asset combination is disallowed via one contract,
        /// but allowed via another contract, then we ultimately want to *allow* that combination.
        Map<String, MergeableTextLanguageBundleRestriction> mergedTextLangaugeRestrictions = Collections.emptyMap();

        if(!audioLanguagesWithDisallowedAssetBundles.isEmpty()) {
            Map<String, MergeableTextLanguageBundleRestriction> mergedTextLanguageRestrictions = new HashMap<String, MergeableTextLanguageBundleRestriction>();
            for(VideoRightsContractHollow contract : applicableContracts) {
                /// find the set of allowed text languages from this contract.
                Set<String> overallContractAllowedTextLanguages = new HashSet<String>();
                for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
                    String contractAssetType = asset._getAssetType()._getValue();

                    if(StreamContractAssetTypeDeterminer.CLOSEDCAPTIONING.equals(contractAssetType)
                            || StreamContractAssetTypeDeterminer.SUBTITLES.equals(contractAssetType)) {
                        overallContractAllowedTextLanguages.add(asset._getBcp47Code()._getValue());
                    }
                }

                /// for each audio language where there is a disallowed asset bundle,
                /// merge the disallowed text languages, AND all available text languages
                /// which were *not* disallowed, are allowed.
                Set<String> bundleRestrictedAudioLanguagesFromThisContract = new HashSet<String>();
                for(DisallowedAssetBundleHollow disallowedAssetBundle : contract._getDisallowedAssetBundles()) {
                    String audioLang = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                    MergeableTextLanguageBundleRestriction textRestriction = getMergeableTextRestrictionsByAudioLang(mergedTextLanguageRestrictions, audioLang);

                    DisallowedSubtitleLangCodesListHollow disallowedSubtitleLangCodes = disallowedAssetBundle._getDisallowedSubtitleLangCodes();
                    if(!disallowedSubtitleLangCodes.isEmpty()) {
                        /// For this audio language, we need to modify the text languages allowed for this contract by removing each
                        /// disallowed text language from the set.
                        Set<String> thisAudioLanguageAllowedTextLanguages = new HashSet<String>(overallContractAllowedTextLanguages);
                        for(DisallowedSubtitleLangCodeHollow lang : disallowedSubtitleLangCodes) {
                            String textLang = lang._getValue()._getValue();
                            thisAudioLanguageAllowedTextLanguages.remove(textLang);
                            textRestriction.addDisallowedTextLanguage(textLang);
                        }

                        textRestriction.addAllowedTextLanguages(thisAudioLanguageAllowedTextLanguages);
                        //// don't process this audio language for this contract again.
                        bundleRestrictedAudioLanguagesFromThisContract.add(disallowedAssetBundle._getAudioLanguageCode()._getValue());
                    }
                }

                /// for each audio language where there was *not* a disallowed asset bundle, all available text
                /// languages for the contract are allowed.
                for(String audioLanguage : audioLanguagesWithDisallowedAssetBundles) {
                    if(!bundleRestrictedAudioLanguagesFromThisContract.contains(audioLanguage)) {
                        MergeableTextLanguageBundleRestriction textRestriction = getMergeableTextRestrictionsByAudioLang(mergedTextLanguageRestrictions, audioLanguage);
                        textRestriction.addAllowedTextLanguages(overallContractAllowedTextLanguages);
                    }
                }
            }
        }


        /// Step 3: If any contract doesn't require forced subtitles for a particular language, then don't
        /// require forced subtitles for that language
        if(!audioLanguagesWhichRequireForcedSubtitles.isEmpty()) {
            for(VideoRightsContractHollow contract : applicableContracts) {
                Set<String> forcedSubtitleLanguagesForThisContract = new HashSet<String>();

                for(DisallowedAssetBundleHollow assetBundle : contract._getDisallowedAssetBundles()) {
                    if(assetBundle._getForceSubtitle())
                        forcedSubtitleLanguagesForThisContract.add(assetBundle._getAudioLanguageCode()._getValue());
                }

                audioLanguagesWhichRequireForcedSubtitles.retainAll(forcedSubtitleLanguagesForThisContract);
            }
        }

        Set<String> restrictedAudioLanguages = audioLanguagesWithDisallowedAssetBundles;
        restrictedAudioLanguages.addAll(audioLanguagesWhichRequireForcedSubtitles);

        for(String audioLang : restrictedAudioLanguages) {
            Set<String> disallowedTextLangauges = Collections.emptySet();

            MergeableTextLanguageBundleRestriction mergeableTextLanguageBundleRestriction = mergedTextLangaugeRestrictions.get(audioLang);
            if(mergeableTextLanguageBundleRestriction != null) {
                disallowedTextLangauges = mergeableTextLanguageBundleRestriction.getFinalDisallowedTextLanguages();
            }

            boolean requiresForcedSubtitles = audioLanguagesWhichRequireForcedSubtitles.contains(audioLang);

            if(requiresForcedSubtitles || !disallowedTextLangauges.isEmpty()) {
                LanguageRestrictions langRestriction = new LanguageRestrictions();
                langRestriction.audioLanguage = getBcp47Code(audioLang);
                langRestriction.audioLanguageId = getBcp47CodeId(audioLang).val;
                langRestriction.requiresForcedSubtitles = requiresForcedSubtitles;

                Set<com.netflix.vms.transformer.hollowoutput.Integer> disallowedTimedTextIds = new HashSet<com.netflix.vms.transformer.hollowoutput.Integer>();
                Set<Strings> disallowedTimedTextCodes = new HashSet<Strings>();

                for(String textLang : disallowedTextLangauges) {
                    disallowedTimedTextCodes.add(getBcp47Code(textLang));
                    disallowedTimedTextIds.add(getBcp47CodeId(textLang));
                }

                langRestriction.disallowedTimedText = disallowedTimedTextIds;
                langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;

                restriction.languageBcp47RestrictionsMap.put(langRestriction.audioLanguage, langRestriction);
            }

        }

        finalizeContractRestriction(assetTypeIdx, restriction, selectedContract);
    }

    private MergeableTextLanguageBundleRestriction getMergeableTextRestrictionsByAudioLang(Map<String, MergeableTextLanguageBundleRestriction> mergedLanguageBundleRestrictions, String audioLanguageCode) {
        MergeableTextLanguageBundleRestriction mergeableRestriction = mergedLanguageBundleRestrictions.get(audioLanguageCode);
        if(mergeableRestriction == null) {
            mergeableRestriction = new MergeableTextLanguageBundleRestriction();
            mergedLanguageBundleRestrictions.put(audioLanguageCode, mergeableRestriction);
        }
        return mergeableRestriction;
    }

    private void buildRestrictionBasedOnSingleApplicableContract(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, VideoRightsContractHollow contract) {
        List<DisallowedAssetBundleHollow> disallowedAssetBundles = contract._getDisallowedAssetBundles();
        for(DisallowedAssetBundleHollow disallowedAssetBundle : disallowedAssetBundles) {
            LanguageRestrictions langRestriction = new LanguageRestrictions();
            String audioLangStr = disallowedAssetBundle._getAudioLanguageCode()._getValue();
            Strings audioLanguage = getBcp47Code(audioLangStr);
            langRestriction.audioLanguage = audioLanguage;
            langRestriction.audioLanguageId = getBcp47CodeId(audioLangStr).val;
            langRestriction.requiresForcedSubtitles = disallowedAssetBundle._getForceSubtitle();

            Set<com.netflix.vms.transformer.hollowoutput.Integer> disallowedTimedTextIds = new HashSet<com.netflix.vms.transformer.hollowoutput.Integer>();
            Set<Strings> disallowedTimedTextCodes = new HashSet<Strings>();
            List<DisallowedSubtitleLangCodeHollow> disallowedSubtitles = disallowedAssetBundle._getDisallowedSubtitleLangCodes();

            for(DisallowedSubtitleLangCodeHollow sub : disallowedSubtitles) {
                String subLang = sub._getValue()._getValue();
                disallowedTimedTextCodes.add(getBcp47Code(subLang));
                disallowedTimedTextIds.add(getBcp47CodeId(subLang));
            }

            langRestriction.disallowedTimedText = disallowedTimedTextIds;
            langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;

            //if(langRestriction.requiresForcedSubtitles || !disallowedTimedTextCodes.isEmpty())
            restriction.languageBcp47RestrictionsMap.put(audioLanguage, langRestriction);
        }

        markAssetTypeIndexForExcludedDownloadablesCalculation(assetTypeIdx, contract);

        finalizeContractRestriction(assetTypeIdx, restriction, contract);
    }

    private void markAssetTypeIndexForExcludedDownloadablesCalculation(DownloadableAssetTypeIndex assetTypeIdx, VideoRightsContractHollow contract) {
        for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
            String contractAssetType = asset._getAssetType()._getValue();

            if(StreamContractAssetTypeDeterminer.CLOSEDCAPTIONING.equals(contractAssetType))
                contractAssetType = StreamContractAssetTypeDeterminer.SUBTITLES;
            if(StreamContractAssetTypeDeterminer.SECONDARY_AUDIO.equals(contractAssetType))
                contractAssetType = StreamContractAssetTypeDeterminer.PRIMARYVIDEO_AUDIOMUXED;

            assetTypeIdx.mark(new ContractAssetType(contractAssetType, asset._getBcp47Code()._getValue()));
        }
    }

    private void finalizeContractRestriction(DownloadableAssetTypeIndex assetTypeIdx, ContractRestriction restriction, VideoRightsContractHollow selectedContract) {
        String cupToken = selectedContract._getCupToken()._getValue();
        CupKey cupKey = cupKeysMap.get(cupToken);
        if(cupKey == null) {
            cupKey = new CupKey(new Strings(cupToken));
            cupKeysMap.put(cupToken, cupKey);
        }
        restriction.cupKeys.add(cupKey);

        if(selectedContract._getPrePromotionDays() != Long.MIN_VALUE)
            restriction.prePromotionDays = (int)selectedContract._getPrePromotionDays();

        restriction.excludedDownloadables = assetTypeIdx.getAllUnmarked();
    }

    private String getLanguageForAsset(PackageStreamHollow stream, String assetType) {
        StreamNonImageInfoHollow nonImageInfo = stream._getNonImageInfo();
        if(StreamContractAssetTypeDeterminer.SUBTITLES.equals(assetType)) {
            return nonImageInfo._getTextInfo()._getTextLanguageCode()._getValue();
        }

        if(nonImageInfo != null) {
            AudioStreamInfoHollow audioInfo = nonImageInfo._getAudioInfo();
            if(audioInfo != null) {
                StringHollow audioLangCode = audioInfo._getAudioLanguageCode();
                if(audioLangCode != null) {
                    return audioLangCode._getValue();
                }
            }
        }
        return null;
    }

    private Strings getBcp47Code(String code) {
        Strings bcp47Code = bcp47Codes.get(code);
        if(bcp47Code == null) {
            bcp47Code = new Strings(code);
            bcp47Codes.put(code, bcp47Code);
        }
        return bcp47Code;
    }

    private com.netflix.vms.transformer.hollowoutput.Integer getBcp47CodeId(String code) {
        com.netflix.vms.transformer.hollowoutput.Integer id = bcp47Ids.get(code);
        if(id == null) {
            int bcp47Ordinal = bcp47CodeIdx.getMatchingOrdinal(code);
            Bcp47CodeHollow bcp47CodeHollow = api.getBcp47CodeHollow(bcp47Ordinal);

            id = new com.netflix.vms.transformer.hollowoutput.Integer((int)bcp47CodeHollow._getLanguageId());
            bcp47Ids.put(code, id);
        }

        return id;
    }


    private static class DownloadableAssetTypeIndex {
        private final Map<ContractAssetType, DownloadableIdList> downloadableIdsByContract;

        public DownloadableAssetTypeIndex() {
            this.downloadableIdsByContract = new HashMap<ContractAssetType, DownloadableIdList>();
        }

        public void addDownloadableId(ContractAssetType assetType, long downloadableId) {
            DownloadableIdList idList = downloadableIdsByContract.get(assetType);
            if(idList == null) {
                idList = new DownloadableIdList();
                downloadableIdsByContract.put(assetType, idList);
            }

            idList.addDownloadableId(downloadableId);
        }

        public void mark(ContractAssetType assetType) {
            DownloadableIdList idList = downloadableIdsByContract.get(assetType);
            if(idList != null)
                idList.mark();
        }

        public void resetMarks() {
            for(Map.Entry<ContractAssetType, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
                entry.getValue().resetMark();
            }
        }

        public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarked() {
            Set<com.netflix.vms.transformer.hollowoutput.Long> set = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

            for(Map.Entry<ContractAssetType, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
                if(!entry.getValue().isMarked()) {
                    set.addAll(entry.getValue().getList());
                }
            }

            return set;
        }
    }

    private static class DownloadableIdList {

        private final List<com.netflix.vms.transformer.hollowoutput.Long> list;
        private boolean marked;

        public DownloadableIdList() {
            this.list = new ArrayList<com.netflix.vms.transformer.hollowoutput.Long>();
        }

        public void addDownloadableId(long downloadableId) {
            list.add(new com.netflix.vms.transformer.hollowoutput.Long(downloadableId));
        }

        public void mark() {
            this.marked = true;
        }

        public void resetMark() {
            this.marked = false;
        }

        public boolean isMarked() {
            return marked;
        }

        public List<com.netflix.vms.transformer.hollowoutput.Long> getList() {
            return list;
        }
    }

}
