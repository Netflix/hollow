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
import java.util.Collections;
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

        HollowHashIndexResult videoRightsResult = videoRightsIdx.findMatches(packages._getMovieId());
        HollowOrdinalIterator iter = videoRightsResult.iterator();
        int videoRightsOrdinal = iter.next();

        while(videoRightsOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            VideoRightsHollow rights = api.getVideoRightsHollow(videoRightsOrdinal);

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

                Set<ContractAssets> contractAssets = new HashSet<ContractAssets>();
                
                for(VideoRightsContractHollow contract : contracts) {
                    //if(contract._getPackageId() == packages._getPackageId()) {
                    if(contract._getPackageId() == packages._getPackageId() && contractIds.contains((int) contract._getContractId())) {
                        
                        /*for(VideoRightsContractPackageHollow contractPkg : contract._getPackages()) {
                            contractPkg._getPackageId()
                        }*/
                        
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
                            langRestriction.disallowedTimedTextBcp47codes = Collections.emptySet();

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

                            contractAssets.add(new ContractAssets(contractAssetType, asset._getBcp47Code()._getValue()));
                        }


                        /*if(assets.assetTypeToLanguagesMap.size() > 1)
                            System.out.println(assets.assetTypeToLanguagesMap.size());*/

                        //System.out.println(assets);

                    }

                }
                
                for(PackageStreamHollow stream : packages._getDownloadables()) {
                    if(stream._getDownloadableId() == 734066790 && "CH".equals(rights._getCountryCode()._getValue())) {
                        System.out.println("watch");
                        System.out.println(new java.util.Date(restriction.availabilityWindow.startDate.val));
                    }
                    
                    String assetType = assetTypeDeterminer.getAssetType(stream);

                    if(assetType == null)
                        continue;

                    String language = getLanguageForAsset(stream, assetType);

                    if(!contractAssets.contains(new ContractAssets(assetType, language))) {
                        restriction.excludedDownloadables.add(new com.netflix.vms.transformer.hollowoutput.Long(stream._getDownloadableId()));
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

                    contractRestrictions.add(restriction);
                }
            }


            if(!contractRestrictions.isEmpty())
                restrictions.put(new ISOCountry(rights._getCountryCode()._getValue()), contractRestrictions);

            videoRightsOrdinal = iter.next();
        }

        return restrictions;
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

}
