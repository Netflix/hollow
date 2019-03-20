package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.config.utils.Pair;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleEntryHollow;
import com.netflix.vms.transformer.hollowinput.RightsHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowContractHollow;
import com.netflix.vms.transformer.hollowinput.RightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.SetOfDisallowedAssetBundleEntryHollow;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.LanguageRights;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.util.VideoContractUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LanguageRightsModule extends AbstractTransformModule {
    private final VMSHollowInputAPI api;
    private final VMSTransformerIndexer indexer;

    public LanguageRightsModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, cycleConstants, mapper);
        this.api = api;
        this.indexer = indexer;
    }

    @Override
    public void transform() {
        /// short circuit FastLane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.LanguageRights) && ctx.getFastlaneIds() != null)
            return;

        Map<Pair<Integer, Integer>, LanguageRights> contractMovieRights = new HashMap<>();

        for (StatusHollow status : api.getAllStatusHollow()) {
            int movieId = (int) status._getMovieId();
            String countryCode = status._getCountryCode()._getValue();

            List<RightsWindowContractHollow> windowContracts = new ArrayList<>();
            RightsHollow rightsHollow = status._getRights();
            List<RightsWindowHollow> windows = rightsHollow._getWindows();
            for (RightsWindowHollow windowHollow : windows) {
                if (windowHollow._getContractIdsExt() != null && !windowHollow._getContractIdsExt().isEmpty()) {
                    windowContracts.addAll(windowHollow._getContractIdsExt().stream().collect(Collectors.toList()));
                }
            }

            for (RightsWindowContractHollow windowContract : windowContracts) {
                int intDealId = (int) windowContract._getDealId();
                Pair<Integer, Integer> rightsKey = new Pair<>(intDealId, movieId);

                long dealId = windowContract._getDealId();
                VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(api, indexer, ctx, movieId, countryCode, dealId);
                SetOfDisallowedAssetBundleEntryHollow disallowedBundleSet_ = contractAttributes == null ? null : contractAttributes._getDisallowedAssetBundles();
                if (disallowedBundleSet_ == null || disallowedBundleSet_.isEmpty()) {
                    continue;
                }

                LanguageRights langRights = contractMovieRights.get(rightsKey);
                if (langRights == null) {
                    langRights = new LanguageRights();
                    langRights.contractId = intDealId;
                    langRights.videoId = new Video(movieId);
                    langRights.languageRestrictionsMap = new HashMap<>();
                    langRights.fallbackRestrictionsMap = new HashMap<>();
                    contractMovieRights.put(rightsKey, langRights);
                }

                Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions> lrMap = new HashMap<>();
                langRights.languageRestrictionsMap.put(cycleConstants.getISOCountry(countryCode), lrMap);

                for (DisallowedAssetBundleEntryHollow bundle_ : disallowedBundleSet_) {
                    LanguageRestrictions lr = new LanguageRestrictions();
                    StringHollow audioLangHollow = bundle_._getAudioLanguageCode();
                    String audioLang = audioLangHollow == null ? null : audioLangHollow._getValue();
                    lr.audioLanguage = audioLang == null ? null : new Strings(audioLang);
                    lr.audioLanguageId = LanguageIdMapping.getLanguageId(audioLang);
                    if(bundle_._getForceSubtitle() != null)
                    	lr.requiresForcedSubtitles = bundle_._getForceSubtitle()._getValue();
                    lr.disallowedTimedText = new HashSet<>();
                    lr.disallowedTimedTextBcp47codes = new HashSet<>();

                    Set<StringHollow> disallowedlangs_ = bundle_._getDisallowedSubtitleLangCodes();
                    for (StringHollow bcp_ : disallowedlangs_) {
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
