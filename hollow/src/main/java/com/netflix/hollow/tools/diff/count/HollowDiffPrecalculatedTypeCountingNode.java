package com.netflix.hollow.tools.diff.count;

import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class HollowDiffPrecalculatedTypeCountingNode extends HollowDiffCountingNode {

    private final HollowTypeDiff precalculatedTypeDiff;
    private final HollowFieldDiff fieldDiff;
    
    private final BitSet matchedToOrdinalIndexes;
    
    private int currentTopLevelFromOrdinal;
    private int currentTopLevelToOrdinal;
    
    public HollowDiffPrecalculatedTypeCountingNode(HollowDiff diff, HollowTypeDiff topLevelTypeDiff, HollowDiffNodeIdentifier nodeId, HollowTypeDiff precalculatedTypeDiff) {
        super(diff, topLevelTypeDiff, nodeId);
        
        this.precalculatedTypeDiff = precalculatedTypeDiff;
        this.matchedToOrdinalIndexes = new BitSet();
        this.fieldDiff = new HollowFieldDiff(nodeId);
    }

    @Override
    public void prepare(int topLevelFromOrdinal, int topLevelToOrdinal) {
        this.currentTopLevelFromOrdinal = topLevelFromOrdinal;
        this.currentTopLevelToOrdinal = topLevelToOrdinal;
    }

    @Override
    public int traverseDiffs(IntList fromOrdinals, IntList toOrdinals) {
        toOrdinals.sort();
        matchedToOrdinalIndexes.clear();
        
        int score = 0;
        
        for(int i=0;i<fromOrdinals.size();i++) {
            int matchedToOrdinal = precalculatedTypeDiff.getMatcher().getMatchedToOrdinal(fromOrdinals.get(i));
            int matchedToOrdinalIdx = matchedToOrdinal == -1 ? -1 : toOrdinals.binarySearch(matchedToOrdinal);
            
            if(matchedToOrdinalIdx >= 0) {
                score += precalculatedTypeDiff.getMatchedRecordDiffScoreByFromOrdinal(fromOrdinals.get(i));
                matchedToOrdinalIndexes.set(matchedToOrdinalIdx);
            } else {
                score += precalculatedTypeDiff.getUnmatchedRecordFieldCounter().getUnmatchedFromRecordDiffScore(fromOrdinals.get(i));
            }
        }
        
        for(int i=0;i<toOrdinals.size();i++) {
            if(!matchedToOrdinalIndexes.get(i)) {
                score += precalculatedTypeDiff.getUnmatchedRecordFieldCounter().getUnmatchedToRecordDiffScore(toOrdinals.get(i));
            }
        }
        
        fieldDiff.addDiff(currentTopLevelFromOrdinal, currentTopLevelToOrdinal, score);
        
        return score;
    }

    @Override
    public int traverseMissingFields(IntList fromOrdinals, IntList toOrdinals) {
        int score = 0;
        
        for(int i=0;i<toOrdinals.size();i++) {
            score += precalculatedTypeDiff.getUnmatchedRecordFieldCounter().getUnmatchedToRecordDiffScore(toOrdinals.get(i));
        }
        
        for(int i=0;i<fromOrdinals.size();i++) {
            score += precalculatedTypeDiff.getUnmatchedRecordFieldCounter().getUnmatchedFromRecordDiffScore(fromOrdinals.get(i));
        }
        
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
