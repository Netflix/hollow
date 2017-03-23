package com.netflix.hollow.tools.diff.count;

import com.netflix.hollow.tools.diff.HollowTypeDiff;

import java.util.Collections;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.core.util.IntList;
import java.util.List;

public class HollowDiffShortcutTypeCountingNode extends HollowDiffCountingNode {

    private final HollowFieldDiff fieldDiff;

    private int currentTopLevelFromOrdinal;
    private int currentTopLevelToOrdinal;

    public HollowDiffShortcutTypeCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId) {
        super(diff, topLevelTypeDiff, nodeId);
        
        this.fieldDiff = new HollowFieldDiff(nodeId);
    }

    @Override
    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        this.currentTopLevelFromOrdinal = topLevelFromOrdinal;
        this.currentTopLevelToOrdinal = topLevelToOrdinal;
    }

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        return addResultToFieldDiff(fromOrdinals, toOrdinals);
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        return addResultToFieldDiff(fromOrdinals, toOrdinals);
    }
    
    private int addResultToFieldDiff(IntList fromOrdinals, IntList toOrdinals) {
        int score = fromOrdinals.size() + toOrdinals.size();
        
        if(score != 0)
            fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, score);
        
        return score;
    }

    @Override
    public List<HollowFieldDiff> getFieldDiffs() {
        if(fieldDiff.getTotalDiffScore() > 0)
            return Collections.singletonList(fieldDiff);
        return Collections.emptyList();
    }

}
