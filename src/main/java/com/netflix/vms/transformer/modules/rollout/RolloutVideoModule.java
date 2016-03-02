package com.netflix.vms.transformer.modules.rollout;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseListHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.AvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.Phase;
import com.netflix.vms.transformer.hollowoutput.RolloutInfo;
import com.netflix.vms.transformer.hollowoutput.RolloutSummary;
import com.netflix.vms.transformer.hollowoutput.RolloutVideo;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolloutVideoModule extends AbstractTransformModule {
    private final HollowPrimaryKeyIndex supplementalIndex;
    private final HollowPrimaryKeyIndex rolloutVideoTypeIndex;

    public RolloutVideoModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, mapper);
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.rolloutVideoTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.ROLLOUT_VIDEO_TYPE);
    }

    @Override
    public void transform() {

        Map<Integer, List<RolloutHollow>> videoIdToRolloutMap = new HashMap<>();

        for (RolloutHollow input : api.getAllRolloutHollow()) {
            Integer videoId = (int)input._getMovieId();
            List<RolloutHollow> rolloutList = videoIdToRolloutMap.get(videoId);
            if (rolloutList == null) {
                rolloutList = new ArrayList<>();
                videoIdToRolloutMap.put(videoId, rolloutList);
            }
            rolloutList.add(input);
        }

        for (Integer videoId : videoIdToRolloutMap.keySet()) {
            RolloutVideo output = new RolloutVideo();
            output.video = new Video(videoId.intValue());

            Map<Strings, RolloutSummary> summaryMap = new HashMap<>();
            output.summaryMap = summaryMap;

            List<RolloutHollow> rolloutList = videoIdToRolloutMap.get(videoId);
            for (RolloutHollow rollout : rolloutList) {
                Strings rolloutType = new Strings(rollout._getRolloutType()._getValue());
                RolloutSummary summary = summaryMap.get(rolloutType);
                if (summary == null) {
                    summary = new RolloutSummary();
                    summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
                    summary.type = rollout._getRolloutType()._getValue().toCharArray();
                    summary.video = output.video;
                    summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
                    summaryMap.put(rolloutType, summary);
                    summary.allPhases = new ArrayList<Phase>();
                }

                RolloutInfo info = new RolloutInfo();
                info.name = rollout._getRolloutName()._getValue().toCharArray();
                info.rolloutId = (int) rollout._getRolloutId();
                info.video = output.video;
                info.type = rollout._getRolloutType()._getValue().toCharArray();
                summary.rolloutInfoMap.put(new com.netflix.vms.transformer.hollowoutput.Integer(info.rolloutId), info);

                RolloutPhaseListHollow phaseListHollow = rollout._getPhases();
                for (RolloutPhaseHollow phaseHollow : phaseListHollow) {
                    Phase phase = new Phase();
                    phase.rolloutId = info.rolloutId;
                    phase.video = info.video;
                    phase.name = phaseHollow._getName()._getValue().toCharArray();
                    phase.isCoreMetaDataShown = phaseHollow._getShowCoreMetadata();
                    phase.projectedLaunchDates = new HashMap<ISOCountry, Date>();
                    phase.windowsMap = new HashMap<ISOCountry, AvailabilityWindow>();

                }
            }
            mapper.addObject(output);
        }
    }

}
