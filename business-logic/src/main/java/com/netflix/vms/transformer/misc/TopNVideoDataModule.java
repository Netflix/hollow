package com.netflix.vms.transformer.misc;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.TopNAttributeHollow;
import com.netflix.vms.transformer.hollowinput.TopNAttributesListHollow;
import com.netflix.vms.transformer.hollowinput.TopNHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Float;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.TopNVideoData;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.HashMap;
import java.util.Map;

public class TopNVideoDataModule extends AbstractTransformModule{

    public TopNVideoDataModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        /// short-circuit for FastLane
        if (OutputTypeConfig.FASTLANE_SKIP_TYPES.contains(OutputTypeConfig.TopNVideoData) && ctx.getFastlaneIds() != null)
            return;

        //Map Video id -> attributes to Country id -> TopNVideoData
        Map<String, TopNVideoData> topNVideoDataMap = new HashMap<>();
        for(TopNHollow topN : api.getAllTopNHollow()) {
            TopNAttributesListHollow attributes = topN._getAttributes();
            int videoId = (int) topN._getVideoId();

            for(int index = 0; index < attributes.size(); index++) {
                TopNAttributeHollow topNAttribute = attributes.get(index);
                String countryId = topNAttribute._getCountry()._getValue();
                TopNVideoData topNVideoData = getOrAddTopNVideoData(topNVideoDataMap, topNAttribute, countryId);
                float viewShare = java.lang.Float.parseFloat(topNAttribute._getViewShare()._getValue());
                topNVideoData.videoViewHrs1Day.put(new Integer(videoId), new Float(viewShare));
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
