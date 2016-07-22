package com.netflix.vms.transformer.override;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.TitleOverride;

import com.netflix.hollow.combine.HollowCombinerCopyDirector;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.read.engine.HollowTypeReadState;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.index.VMSOutputTypeIndexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleOverrideHollowCombinerCopyDirector implements HollowCombinerCopyDirector {

    private final TransformerContext ctx;
    private final HollowReadStateEngine fastlane;
    private final List<HollowReadStateEngine> overrideTitles;

    private final Map<HollowTypeReadState, BitSet> bitsetMap = new HashMap<>();
    private final Map<HollowReadStateEngine, VMSOutputTypeIndexer> indexerMap = new HashMap<>();


    public TitleOverrideHollowCombinerCopyDirector(TransformerContext ctx, HollowReadStateEngine fastlane, List<HollowReadStateEngine> overrideTitles) throws Exception {
        this.ctx = ctx;
        this.fastlane = fastlane;
        this.overrideTitles = overrideTitles;

        init();
    }

    @Override
    public boolean shouldCopy(HollowTypeReadState typeState, int ordinal) {
        BitSet bitset = bitsetMap.get(typeState);
        boolean shouldCopy = bitset != null && bitset.get(ordinal);
        return shouldCopy;
    }

    private void init() throws Exception {
        long start = System.currentTimeMillis();
        List<HollowReadStateEngine> fastlaneFirstList = createList(fastlane, overrideTitles, true);
        List<HollowReadStateEngine> fastlaneLastList = createList(fastlane, overrideTitles, false);

        // create indexers
        for (HollowReadStateEngine stateEngine : fastlaneFirstList) {
            String blobID = TitleOverrideHelper.getBlobID(stateEngine);
            VMSOutputTypeIndexer indexer = new VMSOutputTypeIndexer(blobID, stateEngine);
            indexerMap.put(stateEngine, indexer);
        }

        { // Process VIDEO TYPES - Prefer Pinned Title over Fastlane : fastlaneLastList
            List<VMSOutputTypeIndexer> processedIndexers = new ArrayList<>();
            for (HollowReadStateEngine stateEngine : fastlaneLastList) {
                VMSOutputTypeIndexer currIndexer = indexerMap.get(stateEngine);
                for(OutputTypeConfig typeConfig : OutputTypeConfig.VIDEO_RELATED_TYPES) {
                    process(currIndexer.getName(), typeConfig.getType(), currIndexer, processedIndexers);
                }
                processedIndexers.add(currIndexer);
            }
        }

        { // Process COMMON TYPES - fastlane then the rest: fastlaneFirstList
            List<VMSOutputTypeIndexer> processedIndexers = new ArrayList<>();
            for (HollowReadStateEngine stateEngine : fastlaneFirstList) {
                VMSOutputTypeIndexer currIndexer = indexerMap.get(stateEngine);
                for (OutputTypeConfig typeConfig : OutputTypeConfig.TOP_LEVEL_NON_VIDEO_TYPES) {
                    process(currIndexer.getName(), typeConfig.getType(), currIndexer, processedIndexers);
                }
                processedIndexers.add(currIndexer);
            }
        }

        ctx.getLogger().info(TitleOverride, "TitleOverrideHollowCombinerCopyDirector init duration={}", System.currentTimeMillis() - start);
    }

    List<HollowReadStateEngine> createList(HollowReadStateEngine fastlane, List<HollowReadStateEngine> overrideTitles, boolean isFastlaneFirst) {
        List<HollowReadStateEngine> result = new ArrayList<>();

        if (isFastlaneFirst) result.add(fastlane);
        result.addAll(overrideTitles);
        if (!isFastlaneFirst) result.add(fastlane);

        return result;
    }

    void process(String blobID, String typeName, VMSOutputTypeIndexer currIndexer, List<VMSOutputTypeIndexer> processedIndexers) throws Exception {
        HollowReadStateEngine stateEngine = currIndexer.getStateEngine();
        HollowTypeReadState typeState = stateEngine.getTypeState(typeName);

        BitSet bitset = null;
        if (processedIndexers.isEmpty()) {
            // first blob so allow everything
            bitset = newBitSet(typeState, true);
        } else {
            bitset = newBitSet(typeState, false);
            int maxOrdinal = typeState.maxOrdinal();
            HollowPrimaryKeyIndex primaryKeyIndex = currIndexer.getPrimaryKeyIndex(typeName);
            for (int i = 0; i <= maxOrdinal; i++) {
                try {
                    Object[] recKey = primaryKeyIndex.getRecordKey(i);
                    boolean hasDupRecord = hasDupRecord(processedIndexers, typeName, recKey);
                    bitset.set(i, !hasDupRecord);
                } catch (Exception ex) {
                    ctx.getLogger().warn(TitleOverride, "Unable to find recKey for blobID:{}, type={}", blobID, typeName);
                    throw ex;
                }
            }
        }

        addToBitSetMap(blobID, typeName, typeState, bitset);
    }

    boolean hasDupRecord(List<VMSOutputTypeIndexer> overrideTitleIndexers, String type, Object[] recKey) {
        for (VMSOutputTypeIndexer indexer : overrideTitleIndexers) {
            HollowPrimaryKeyIndex primaryKeyIndex = indexer.getPrimaryKeyIndex(type);
            if (primaryKeyIndex.getMatchingOrdinal(recKey) >= 0) {
                return true;
            }
        }

        return false;
    }

    void addToBitSetMap(String blobID, String typeName, HollowTypeReadState typeState, BitSet bitset) throws Exception {
        if (bitsetMap.containsKey(typeState)) {
            // indicate failure since it does not expect to have dup typeState
            ctx.getLogger().error(TransformerLogTag.TitleOverride, "Unexpected duplicate typeState found for blobID={} typeName={}", blobID, typeName);
            throw new Exception("Unable to init CopyDirector.  Duplicate typeState found for blobID=" + blobID + " typeName=" + typeName);
        }

        //System.out.println(String.format("blobId=%s typeName=%s bitSet=%s", blobID, typeName, bitset));
        bitsetMap.put(typeState, bitset);
    }

    Map<HollowTypeReadState, BitSet> getBitSetMap() {
        return bitsetMap;
    }

    BitSet newBitSet(HollowTypeReadState typeState, boolean isInitAllSet) {
        int size = typeState.maxOrdinal() + 1;
        BitSet bitset = new BitSet(size);
        if (isInitAllSet) bitset.set(0, size);
        return bitset;
    }

}