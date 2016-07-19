package com.netflix.vms.transformer.override;

import com.netflix.hollow.combine.HollowCombinerCopyDirector;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.read.engine.HollowTypeReadState;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.index.VMSOutputTypeIndexer;

import java.util.ArrayList;
import java.util.List;

public class TitleOverrideHollowCombinerCopyDirector implements HollowCombinerCopyDirector {

    private final HollowReadStateEngine fastlane;
    private final List<HollowReadStateEngine> overrideTitles;

    public TitleOverrideHollowCombinerCopyDirector(HollowReadStateEngine fastlane, List<HollowReadStateEngine> overrideTitles) {
        this.fastlane = fastlane;
        this.overrideTitles = overrideTitles;
    }

    private void init() {
        VMSOutputTypeIndexer fastlaneIndexer = new VMSOutputTypeIndexer(fastlane);

        List<VMSOutputTypeIndexer> overrideTitleIndexers = new ArrayList<>();
        for (HollowReadStateEngine e : overrideTitles) {
            overrideTitleIndexers.add(new VMSOutputTypeIndexer(e));
        }

        for (OutputTypeConfig config : OutputTypeConfig.values()) {
            HollowTypeReadState typeState = fastlane.getTypeState(config.getType());

        }
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        return true;
    }
}