package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerIncludeOrdinalsCopyDirector;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DataSlicer {

    public final int numberOfRandomTopNodesToInclude;
    public final int[] specificTopNodeIdsToInclude;
    public final Set<Integer> videoIdsToInclude;

    public Map<String, BitSet> ordinalsToInclude;

    private Set<String> excludedTypes = new HashSet<>();

    public DataSlicer(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        this.numberOfRandomTopNodesToInclude = numberOfRandomTopNodesToInclude;
        this.specificTopNodeIdsToInclude = specificTopNodeIdsToInclude;
        this.ordinalsToInclude = new HashMap<String, BitSet>();
        this.videoIdsToInclude = new HashSet<Integer>();
    }

    public void setExcludedTypes(Set<String> excludedTypes) {
        this.excludedTypes = excludedTypes;
    }

    public void includeAll(HollowReadStateEngine stateEngine, String type) {
        if (excludedTypes.contains(type)) return;

        ordinalsToInclude.put(type, populatedOrdinals(stateEngine, type));
    }

    public BitSet findIncludedOrdinals(HollowReadStateEngine stateEngine, String type, Set<Integer> includedVideoIds, VideoIdDeriver idDeriver) {
        if (excludedTypes.contains(type) || stateEngine.getTypeState(type) == null) {
            return new BitSet();
        }

        int maxOrdinal = stateEngine.getTypeState(type).maxOrdinal();
        BitSet populatedOrdinals = new BitSet(maxOrdinal + 1);
        for(int i=0;i<maxOrdinal + 1;i++) {
            if(ordinalIsPopulated(stateEngine, type, i)) {
                if(includedVideoIds.contains(idDeriver.deriveId(i))) {
                    populatedOrdinals.set(i);
                }
            }
        }

        ordinalsToInclude.put(type, populatedOrdinals);

        return populatedOrdinals;
    }

    public void joinIncludedOrdinals(
            HollowReadStateEngine stateEngine,
            BitSet fromOrdinals,
            String toType, String toField,
            String fromType, String... fromFields) {

        HollowPrimaryKeyIndex pkIdx = new HollowPrimaryKeyIndex(stateEngine, toType, toField);

        BitSet includedOrdinals = new BitSet();

        for(String fromField : fromFields) {
            HollowIndexerValueTraverser traverser = new HollowIndexerValueTraverser(stateEngine, fromType, fromField);


            int ordinal = fromOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                traverser.traverse(ordinal);
                for(int i=0;i<traverser.getNumMatches();i++) {
                    Object key = traverser.getMatchedValue(i, 0);
                    //System.out.println("MATCHED KEY: " + key);
                    int personImagesOrdinal = pkIdx.getMatchingOrdinal(key);
                    if(personImagesOrdinal != -1) {
                        //System.out.println("joined ordinal: " + personImagesOrdinal);
                        includedOrdinals.set(personImagesOrdinal);
                    }
                }

                ordinal = fromOrdinals.nextSetBit(ordinal + 1);
            }
        }

        ordinalsToInclude.put(toType, includedOrdinals);
    }

    public boolean isNumberCharacter(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean ordinalIsPopulated(HollowReadStateEngine stateEngine, String type, int ordinal) {
        return populatedOrdinals(stateEngine, type).get(ordinal);
    }

    public BitSet populatedOrdinals(HollowReadStateEngine stateEngine, String type) {
        if (stateEngine.getTypeState(type) == null) {
            return new BitSet();
        }

        return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

    public interface VideoIdDeriver {
        Integer deriveId(int ordinal);
    }

    public static HollowWriteStateEngine populateFilteredBlob(HollowReadStateEngine inputStateEngine, Map<String, BitSet> ordinalsToInclude) {
        Map<String, String> headerTags = inputStateEngine.getHeaderTags();
        HollowCombiner combiner = new HollowCombiner(new HollowCombinerIncludeOrdinalsCopyDirector(ordinalsToInclude), inputStateEngine);

        combiner.combine();
        HollowWriteStateEngine outputStateEngine = combiner.getCombinedStateEngine();
        outputStateEngine.addHeaderTags(headerTags);

        System.out.println(String.format("writeFilteredBlob headers:"));
        for (Map.Entry<String, String> entry : headerTags.entrySet()) {
            System.out.println(String.format("\t %s=%s", entry.getKey(), entry.getValue()));
        }
        return outputStateEngine;
    }
}
