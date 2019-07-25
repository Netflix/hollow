package com.netflix.vms.transformer.misc;

import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.TOPN;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.TopNAttributeHollow;
import com.netflix.vms.transformer.hollowinput.TopNAttributesSetHollow;
import com.netflix.vms.transformer.hollowinput.TopNHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.TopNVideoData;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.api.gen.topn.SetOfTopNAttribute;
import com.netflix.vms.transformer.input.api.gen.topn.TopN;
import com.netflix.vms.transformer.input.api.gen.topn.TopNAPI;
import com.netflix.vms.transformer.input.api.gen.topn.TopNAttribute;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.modules.ModuleDataSourceTransitionUtil;
import java.util.HashMap;
import java.util.Map;

public class TopNVideoDataModule extends AbstractTransformModule{

    private final UpstreamDatasetHolder upstream;

    public TopNVideoDataModule(VMSHollowInputAPI api, UpstreamDatasetHolder upstream, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper) {
        super(api, ctx, cycleConstants, mapper);
        this.upstream = upstream;
    }

    @Override
    public void transform() {
        /// short-circuit for FastLane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.TopNVideoData) && ctx.getFastlaneIds() != null)
            return;

        TopNAPI topNAPI = (TopNAPI) upstream.getDataset(TOPN).getAPI();

        //Map Video id -> attributes to Country id -> TopNVideoData
        Map<String, TopNVideoData> topNVideoDataMap = new HashMap<>();

        if(ModuleDataSourceTransitionUtil.useTopNJson()) {  // NOTE: [TopN JSON input] Remove after TopN cinder migration
            for (TopNHollow topN : api.getAllTopNHollow()) {
                TopNAttributesSetHollow attributes = topN._getAttributes();
                int videoId = (int) topN._getVideoId();
                for (TopNAttributeHollow topNAttribute : attributes) {
                    try {
                        String countryId = topNAttribute._getCountry()._getValue();
                        TopNVideoData topNVideoData = topNVideoDataMap.computeIfAbsent(countryId, k -> {
                                TopNVideoData v = new TopNVideoData();
                                v.videoViewHrs1Day = new HashMap<>();
                                v.countryId = countryId.toCharArray();
                                v.countryViewHrs1Day = java.lang.Float.parseFloat(topNAttribute._getCountryViewHrs()._getValue());
                                return v;
                            });
                        float viewShare = java.lang.Float.parseFloat(topNAttribute._getViewShare()._getValue());
                        topNVideoData.videoViewHrs1Day.put(
                                new com.netflix.vms.transformer.hollowoutput.Integer(videoId),
                                new com.netflix.vms.transformer.hollowoutput.Float(viewShare));
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to process videoId=" + videoId
                                + ", country=" + topNAttribute._getCountry()._getValue()
                                + ", countryViewShare[" + topNAttribute._getCountryViewHrs()._getValue() + "]"
                                + ", viewShare[" + topNAttribute._getViewShare()._getValue() + "]", ex);
                    }
                }
            }
        } else {
            for (TopN topN : topNAPI.getAllTopN()) {
                SetOfTopNAttribute attributes = topN.getAttributes();
                int videoId = (int) topN.getVideoId();
                for (TopNAttribute topNAttribute : attributes) {
                    String countryId = topNAttribute.getCountry();
                    TopNVideoData topNVideoData = topNVideoDataMap.computeIfAbsent(countryId, k -> {
                            TopNVideoData v = new TopNVideoData();
                            v.videoViewHrs1Day = new HashMap<>();
                            v.countryId = countryId.toCharArray();
                            v.countryViewHrs1Day = topNAttribute.getCountryViewHoursDaily();
                            return v;
                        });
                    topNVideoData.videoViewHrs1Day.put(
                            new com.netflix.vms.transformer.hollowoutput.Integer(videoId),
                            new com.netflix.vms.transformer.hollowoutput.Float(topNAttribute.getVideoViewHoursDaily()));
                }
            }
        }
        for(TopNVideoData topNVideoData : topNVideoDataMap.values()) {
            mapper.addObject(topNVideoData);
        }
    }
}
