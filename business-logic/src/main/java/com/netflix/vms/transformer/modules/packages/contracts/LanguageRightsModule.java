package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.config.utils.Pair;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundlesListHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodeHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedSubtitleLangCodesListHollow;
import com.netflix.vms.transformer.hollowinput.ListOfRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.LanguageRights;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.util.VideoContractUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LanguageRightsModule extends AbstractTransformModule {
    private final VMSHollowInputAPI api;
    private final VMSTransformerIndexer indexer;

    public LanguageRightsModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
        this.api = api;
        this.indexer = indexer;
    }

    @Override
    public void transform() {
        /// short circuit FastLane
        if (OutputTypeConfig.FASTLANE_SKIP_TYPES.contains(OutputTypeConfig.LanguageRights) && ctx.getFastlaneIds() != null)
            return;

        Map<Pair<Integer, Integer>, LanguageRights> contractMovieRights = new HashMap<>();

        for (StatusHollow status : api.getAllStatusHollow()) {
            int movieId = (int) status._getMovieId();
            String countryCode = status._getCountryCode()._getValue();

            ListOfRightsContractHollow rightsContracts = status._getRights()._getContracts();
            for (RightsContractHollow rightContract : rightsContracts) {
                int contractId = (int) rightContract._getContractId();
                Pair<Integer, Integer> rightsKey = new Pair<>(contractId, movieId);

                ContractHollow contract = VideoContractUtil.getContract(api, indexer, movieId, countryCode, rightContract._getContractId());
                DisallowedAssetBundlesListHollow disallowedBundleList_ = contract == null ? null : contract._getDisallowedAssetBundles();
                if (disallowedBundleList_ == null || disallowedBundleList_.isEmpty()) {
                    continue;
                }

                LanguageRights langRights = contractMovieRights.get(rightsKey);
                if (langRights == null) {
                    langRights = new LanguageRights();
                    langRights.contractId = contractId;
                    langRights.videoId = new Video(movieId);
                    langRights.languageRestrictionsMap = new HashMap<ISOCountry, Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions>>();
                    langRights.fallbackRestrictionsMap = new HashMap<Strings, Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions>>();
                    contractMovieRights.put(rightsKey, langRights);
                }

                Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions> lrMap = new HashMap<>();
                langRights.languageRestrictionsMap.put(new ISOCountry(countryCode), lrMap);

                for (DisallowedAssetBundleHollow bundle_ : disallowedBundleList_) {
                    LanguageRestrictions lr = new LanguageRestrictions();
                    StringHollow audioLangHollow = bundle_._getAudioLanguageCode();
                    String audioLang = audioLangHollow == null ? null : audioLangHollow._getValue();
                    lr.audioLanguage = audioLang == null ? null : new Strings(audioLang);
                    lr.audioLanguageId = LanguageIdMapping.getLanguageId(audioLang);
                    lr.requiresForcedSubtitles = bundle_._getForceSubtitleBoxed().booleanValue();
                    lr.disallowedTimedText = new HashSet<>();
                    lr.disallowedTimedTextBcp47codes = new HashSet<>();

                    DisallowedSubtitleLangCodesListHollow disallowedlangs_ = bundle_._getDisallowedSubtitleLangCodes();
                    for(DisallowedSubtitleLangCodeHollow lang_ : disallowedlangs_) {
                        StringHollow bcp_ = lang_._getValue();
                        if (bcp_ != null) {
                            String bcp = bcp_._getValue();
                            lr.disallowedTimedTextBcp47codes.add(new Strings(bcp));
                            lr.disallowedTimedText.add(new com.netflix.vms.transformer.hollowoutput.Integer(LanguageIdMapping.getLanguageId(bcp)));
                        }
                    }

                    lrMap.put(new com.netflix.vms.transformer.hollowoutput.Integer(lr.audioLanguageId), lr);
                }
            }
        }
        for (LanguageRights langRights : contractMovieRights.values()) {
            mapper.addObject(langRights);
        }
    }

}
