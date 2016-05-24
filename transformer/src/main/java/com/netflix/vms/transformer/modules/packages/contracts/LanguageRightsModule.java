package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.config.utils.Pair;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundleHollow;
import com.netflix.vms.transformer.hollowinput.DisallowedAssetBundlesListHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractSetHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LanguageRestrictions;
import com.netflix.vms.transformer.hollowoutput.LanguageRights;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

public class LanguageRightsModule extends AbstractTransformModule {
    private final VMSHollowInputAPI api;
    private final Properties bcp47Mapping = new Properties();

    public LanguageRightsModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
        this.api = api;
        InputStream istream = null;
        try {
            bcp47Mapping.load(this.getClass().getClassLoader().getResourceAsStream("bcp47.properties"));
        } catch (Exception e) {
            ctx.getLogger().error(LogTag.LanguageRightsError, "bcp47mapping not loaded", e);
            // throw new RuntimeException(e); // TODO: test that it works in deployment as well
        } finally {
            IOUtils.closeQuietly(istream);
        }
    }

    @Override
    public void transform() {
    	/// short circuit FastLane
    	if(ctx.getFastlaneIds() != null)
    		return;
    	
    	
        Map<Pair<Integer, Integer>, LanguageRights> contractMovieRights = new HashMap<>();

        for (VideoRightsHollow videoRights_ : api.getAllVideoRightsHollow()) {
            int movieId = (int) videoRights_._getMovieId();
            String countryCode = videoRights_._getCountryCode()._getValue();

            VideoRightsContractSetHollow contracts_ = videoRights_._getRights()._getContracts();
            for (VideoRightsContractHollow contract_ : contracts_) {
                int contractId = (int) contract_._getContractId();
                Pair<Integer, Integer> rightsKey = new Pair<>(contractId, movieId);

                LanguageRights langRights = contractMovieRights.get(rightsKey);
                if (langRights == null) {
                    langRights = new LanguageRights();
                    langRights.contractId = contractId;
                    langRights.videoId = new Video(movieId);
                    langRights.languageRestrictionsMap = new HashMap<ISOCountry, Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions>>();
                    langRights.fallbackRestrictionsMap = new HashMap<Strings, Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions>>();
                    contractMovieRights.put(rightsKey, langRights);
                }

                DisallowedAssetBundlesListHollow disallowedBundleList_ = contract_._getDisallowedAssetBundles();
                if (disallowedBundleList_ == null || disallowedBundleList_.isEmpty()) {
                    continue;
                }

                Map<com.netflix.vms.transformer.hollowoutput.Integer, LanguageRestrictions> lrMap = new HashMap<>();
                langRights.languageRestrictionsMap.put(new ISOCountry(countryCode), lrMap);

                for (DisallowedAssetBundleHollow bundle_ : disallowedBundleList_) {
                    LanguageRestrictions lr = new LanguageRestrictions();
                    lr.audioLanguage = toStrings(bundle_._getAudioLanguageCode());
                    lr.audioLanguageId = getlanguageId(lr.audioLanguage);
                    lr.requiresForcedSubtitles = bundle_._getForceSubtitleBoxed().booleanValue();
                    lrMap.put(new com.netflix.vms.transformer.hollowoutput.Integer(lr.audioLanguageId), lr);
                }
            }
        }
        for (LanguageRights langRights : contractMovieRights.values()) {
            mapper.addObject(langRights);
        }
    }

    private int getlanguageId(Strings bcpCode) {
        if (bcpCode == null) {
            return -1;
        }
        String val = bcp47Mapping.getProperty(new String(bcpCode.value), "-1");
        return Integer.valueOf(val).intValue();
    }

    private Strings toStrings(StringHollow str_) {
        if (str_ == null)
            return null;
        return new Strings(str_._getValue());
    }

}
