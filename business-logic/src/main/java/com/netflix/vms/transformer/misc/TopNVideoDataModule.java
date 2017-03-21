package com.netflix.vms.transformer.misc;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.TopNAttributeHollow;
import com.netflix.vms.transformer.hollowinput.TopNAttributesSetHollow;
import com.netflix.vms.transformer.hollowinput.TopNHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Float;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.TopNVideoData;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import java.util.HashMap;
import java.util.Map;

public class TopNVideoDataModule extends AbstractTransformModule{

    public TopNVideoDataModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper) {
        super(api, ctx, cycleConstants, mapper);
    }

    @Override
    public void transform() {
        /// short-circuit for FastLane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.TopNVideoData) && ctx.getFastlaneIds() != null)
            return;

        //Map Video id -> attributes to Country id -> TopNVideoData
        Map<String, TopNVideoData> topNVideoDataMap = new HashMap<>();
        for(TopNHollow topN : api.getAllTopNHollow()) {
            TopNAttributesSetHollow attributes = topN._getAttributes();
            int videoId = (int) topN._getVideoId();
            for(TopNAttributeHollow topNAttribute : attributes) {
                try {
                    String countryId = topNAttribute._getCountry()._getValue();
                    TopNVideoData topNVideoData = getOrAddTopNVideoData(topNVideoDataMap, topNAttribute, countryId);
                    float viewShare = java.lang.Float.parseFloat(topNAttribute._getViewShare()._getValue());
                    topNVideoData.videoViewHrs1Day.put(new Integer(videoId), new Float(viewShare));
                } catch(Exception ex) {
                    throw new RuntimeException("Faile to process videoId=" + videoId
                            + ", country=" + topNAttribute._getCountry()._getValue()
                            + ", countryViewShare[" + topNAttribute._getCountryViewHrs()._getValue() + "]"
                            + ", viewShare[" + topNAttribute._getViewShare()._getValue() + "]", ex);
                }
            }
        }

        for(TopNVideoData topNVideoData : topNVideoDataMap.values()) {
            mapper.addObject(topNVideoData);
        }
    }

    private TopNVideoData getOrAddTopNVideoData(Map<String, TopNVideoData> topNVideoDataMap,
            TopNAttributeHollow topNAttribute, String countryId) {
        TopNVideoData topNVideoData = topNVideoDataMap.get(countryId);
        if(topNVideoData == null) {
            topNVideoData = new TopNVideoData();
            topNVideoData.videoViewHrs1Day = new HashMap<>();
            topNVideoData.countryId = countryId.toCharArray();
            topNVideoData.countryViewHrs1Day = java.lang.Float.parseFloat(topNAttribute._getCountryViewHrs()._getValue());
            topNVideoDataMap.put(countryId, topNVideoData);
        }
        return topNVideoData;
    }
}
