package com.netflix.vms.transformer.modules.packages.contracts;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;

import com.netflix.config.utils.Pair;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleEntryHollow;
import com.netflix.vms.transformer.hollowinput.SetOfDisallowedAssetBundleEntryHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.LanguageRights;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Rights;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindow;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.RightsWindowContract;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Status;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
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
    private final VMSHollowInputAPI converterAPI;
    private final Gk2StatusAPI gk2StatusAPI;
    private final VMSTransformerIndexer indexer;

    public LanguageRightsModule(UpstreamDatasetDefinition upstream, VMSHollowInputAPI converterAPI, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(converterAPI, ctx, cycleConstants, mapper);
        this.converterAPI = converterAPI;
        this.gk2StatusAPI = ((Gatekeeper2Dataset) upstream.getDataset(GATEKEEPER2)).getAPI();
        this.indexer = indexer;
    }

    @Override
    public void transform() {
        /// short circuit FastLane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.LanguageRights) && ctx.getFastlaneIds() != null)
            return;

        Map<Pair<Integer, Integer>, LanguageRights> contractMovieRights = new HashMap<>();

        for (Status status : gk2StatusAPI.getAllStatus()) {
            int movieId = (int) status.getMovieId();
            String countryCode = status.getCountryCode();

            List<RightsWindowContract> windowContracts = new ArrayList<>();
            Rights rightsHollow = status.getRights();
            List<RightsWindow> windows = rightsHollow.getWindows();
            for (RightsWindow windowHollow : windows) {
                if (windowHollow.getContractIdsExt() != null && !windowHollow.getContractIdsExt().isEmpty()) {
                    windowContracts.addAll(windowHollow.getContractIdsExt().stream().collect(Collectors.toList()));
                }
            }

            for (RightsWindowContract windowContract : windowContracts) {
                int intDealId = (int) windowContract.getDealId();
                Pair<Integer, Integer> rightsKey = new Pair<>(intDealId, movieId);

                long dealId = windowContract.getDealId();
                VmsAttributeFeedEntryHollow contractAttributes = VideoContractUtil.getVmsAttributeFeedEntry(
                        converterAPI, indexer, ctx, movieId, countryCode, dealId);
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
