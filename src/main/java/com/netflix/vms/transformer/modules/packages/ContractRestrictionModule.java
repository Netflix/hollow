package com.netflix.vms.transformer.modules.packages;

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

public class ContractRestrictionModule {

    private final HollowHashIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex bcp47CodeIdx;

    private final VMSHollowVideoInputAPI api;

    private final Map<String, CupKey> cupKeysMap;
    private final Map<String, Strings> bcp47Codes;
    private final Map<String, Integer> bcp47Ids;

    private final StreamContractAssetTypeDeterminer assetTypeDeterminer;

    public ContractRestrictionModule(VMSHollowVideoInputAPI api , VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getHashIndex(IndexSpec.ALL_VIDEO_RIGHTS);
        this.bcp47CodeIdx = indexer.getPrimaryKeyIndex(IndexSpec.BCP47_CODE);
        this.cupKeysMap = new HashMap<String, CupKey>();
        this.bcp47Codes = new HashMap<String, Strings>();
        this.bcp47Ids = new HashMap<String, Integer>();
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

                VideoRightsContractHollow selectedContract = null;

                assetTypeIdx.resetMarks();

                for(VideoRightsContractHollow contract : contracts) {
                    if(contractIds.contains((int) contract._getContractId()) && contractIsApplicableForPackage(contract, packages._getPackageId())) {

                        if(selectedContract == null || contract._getContractId() > selectedContract._getContractId())
                            selectedContract = contract;

                        List<DisallowedAssetBundleHollow> disallowedAssetBundles = contract._getDisallowedAssetBundles();
                        for(DisallowedAssetBundleHollow disallowedAssetBundle : disallowedAssetBundles) {
                            LanguageRestrictions langRestriction = new LanguageRestrictions();
                            String audioLangStr = disallowedAssetBundle._getAudioLanguageCode()._getValue();
                            Strings audioLanguage = getBcp47Code(audioLangStr);
                            langRestriction.audioLanguage = audioLanguage;
                            langRestriction.audioLanguageId = getBcp47CodeId(audioLangStr);
                            langRestriction.requiresForcedSubtitles = disallowedAssetBundle._getForceSubtitle();

                            Set<Integer> disallowedTimedTextIds = new HashSet<Integer>();
                            Set<Strings> disallowedTimedTextCodes = new HashSet<Strings>();
                            List<DisallowedSubtitleLangCodeHollow> disallowedSubtitles = disallowedAssetBundle._getDisallowedSubtitleLangCodes();

                            for(DisallowedSubtitleLangCodeHollow sub : disallowedSubtitles) {
                                String subLang = sub._getValue()._getValue();
                                disallowedTimedTextCodes.add(getBcp47Code(subLang));
                                disallowedTimedTextIds.add(getBcp47CodeId(subLang));
                            }

                            langRestriction.disallowedTimedTextBcp47codes = disallowedTimedTextCodes;

                            if(langRestriction.requiresForcedSubtitles || !disallowedTimedTextCodes.isEmpty())
                                restriction.languageBcp47RestrictionsMap.put(audioLanguage, langRestriction);
                        }

                        for(VideoRightsContractAssetHollow asset : contract._getAssets()) {
                            String contractAssetType = asset._getAssetType()._getValue();

                            if(StreamContractAssetTypeDeterminer.CLOSEDCAPTIONING.equals(contractAssetType))
                                contractAssetType = StreamContractAssetTypeDeterminer.SUBTITLES;
                            if(StreamContractAssetTypeDeterminer.SECONDARY_AUDIO.equals(contractAssetType))
                                contractAssetType = StreamContractAssetTypeDeterminer.PRIMARYVIDEO_AUDIOMUXED;

                            assetTypeIdx.mark(new ContractAssetType(contractAssetType, asset._getBcp47Code()._getValue()));
                        }
                    }
                }

                if(selectedContract != null) {
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

                    contractRestrictions.add(restriction);
                }
            }

            if(!contractRestrictions.isEmpty())
                restrictions.put(new ISOCountry(rights._getCountryCode()._getValue()), contractRestrictions);

            videoRightsOrdinal = iter.next();
        }

        return restrictions;
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

    private Integer getBcp47CodeId(String code) {
        Integer id = bcp47Ids.get(code);
        if(id == null) {
            int bcp47Ordinal = bcp47CodeIdx.getMatchingOrdinal(code);
            Bcp47CodeHollow bcp47CodeHollow = api.getBcp47CodeHollow(bcp47Ordinal);

            id = Integer.valueOf((int)bcp47CodeHollow._getLanguageId());
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
