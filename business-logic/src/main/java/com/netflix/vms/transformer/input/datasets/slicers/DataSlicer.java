package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerIncludeOrdinalsCopyDirector;
import com.netflix.vms.generated.notemplate.L10NResourcesHollow;
import com.netflix.vms.generated.notemplate.NamedCollectionHolderHollow;
import com.netflix.vms.generated.notemplate.SetOfVideoHollow;
import com.netflix.vms.generated.notemplate.StringsHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DataSlicer {

    private final Set<Integer> videoIdsToInclude;
    private Map<String, BitSet> ordinalsToInclude;
    private Set<String> excludedTypes = new HashSet<>();

    public DataSlicer() {
        this.ordinalsToInclude = new HashMap<>();
        this.videoIdsToInclude = new HashSet<>();
    }

    public DataSlicer(int[] specificVideoIds) {
        this();
        Arrays.stream(specificVideoIds).forEach(this.videoIdsToInclude::add);
    }

    protected void clearOrdinalsToInclude() {
        this.ordinalsToInclude.clear();
    }

    protected void addVideoIdsToInclude(Set<Integer> videos) {
        if (videos != null)
            this.videoIdsToInclude.addAll(videos);
    }

    protected void setExcludedTypes(Set<String> excludedTypes) {
        this.excludedTypes = excludedTypes;
    }

    protected void includeAll(HollowReadStateEngine stateEngine, String type) {
        if (excludedTypes.contains(type)) return;

        ordinalsToInclude.put(type, populatedOrdinals(stateEngine, type));
    }

    protected BitSet findIncludedOrdinals(HollowReadStateEngine stateEngine, String type, VideoIdDeriver idDeriver) {
        if (excludedTypes.contains(type) || stateEngine.getTypeState(type) == null) {
            return new BitSet();
        }

        int maxOrdinal = stateEngine.getTypeState(type).maxOrdinal();
        BitSet populatedOrdinals = new BitSet(maxOrdinal + 1);
        for(int i=0;i<maxOrdinal + 1;i++) {
            if(ordinalIsPopulated(stateEngine, type, i)) {
                if(videoIdsToInclude.contains(idDeriver.deriveId(i))) {
                    populatedOrdinals.set(i);
                }
            }
        }

        ordinalsToInclude.put(type, populatedOrdinals);

        return populatedOrdinals;
    }

    protected void joinIncludedOrdinals(
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

    protected boolean isNumberCharacter(char c) {
        return c >= '0' && c <= '9';
    }

    protected boolean ordinalIsPopulated(HollowReadStateEngine stateEngine, String type, int ordinal) {
        return populatedOrdinals(stateEngine, type).get(ordinal);
    }

    protected BitSet populatedOrdinals(HollowReadStateEngine stateEngine, String type) {
        if (stateEngine.getTypeState(type) == null) {
            return new BitSet();
        }

        return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

    public interface VideoIdDeriver {
        Integer deriveId(int ordinal);
    }

    protected HollowWriteStateEngine populateFilteredBlob(HollowReadStateEngine inputStateEngine) {
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

    protected void addFilteredNamedLists(VMSRawHollowAPI api, HollowWriteStateEngine writeStateEngine) {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.doNotUseDefaultHashKeys();

        for(NamedCollectionHolderHollow holder : api.getAllNamedCollectionHolderHollow()) {
            NamedCollectionHolder outputHolder = new NamedCollectionHolder();

            outputHolder.country = new ISOCountry(holder._getCountry()._getId());

            outputHolder.videoListMap = new HashMap<>();

            Set<Integer> validVideoIdsForCountry = new HashSet<Integer>();

            for(Map.Entry<StringsHollow, SetOfVideoHollow> entry : holder._getVideoListMap().entrySet()) {
                Set<Video> videoSet = new HashSet<Video>();

                for(VideoHollow video : entry.getValue()) {
                    Integer value = video._getValueBoxed();

                    if(videoIdsToInclude.contains(value)) {
                        videoSet.add(new Video(value.intValue()));
                    }
                }

                if("VALID_VIDEOS".equals(entry.getKey()._getValue())) {
                    for(Video video : videoSet)
                        validVideoIdsForCountry.add(video.value);
                }

                outputHolder.videoListMap.put(new Strings(entry.getKey()._getValue()), videoSet);
            }

            mapper.addObject(outputHolder);
        }
    }

    protected void findIncludedL10NOrdinals(HollowReadStateEngine stateEngine, String type, boolean isIncludeNonVideoL10N) {
        ordinalsToInclude.put(type, findIncludedL10NOrdinals(stateEngine, isIncludeNonVideoL10N));
    }

    protected BitSet findIncludedL10NOrdinals(HollowReadStateEngine stateEngine, boolean isIncludeNonVideoL10N) {
        VMSRawHollowAPI api = new VMSRawHollowAPI(stateEngine);

        BitSet l10nResourcesPopulatedOrdinals = populatedOrdinals(stateEngine, "L10NResources");
        BitSet includedL10nResourcesOrdinals = new BitSet(l10nResourcesPopulatedOrdinals.length());

        int l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(0);
        while(l10nOrdinal != -1) {
            L10NResourcesHollow l10nResources = api.getL10NResourcesHollow(l10nOrdinal);

            String idStr = l10nResources._getResourceIdStr();
            char c = idStr.charAt(0);
            if (c == 'm' || c == 'e' || (c == 'r' && idStr.startsWith("rv_"))) {
                // filter video related resourceIds
                for(int i=0;i<idStr.length();i++) {
                    if(isNumberCharacter(idStr.charAt(i))) {
                        int j=i+1;
                        while(j < idStr.length() && isNumberCharacter(idStr.charAt(j)))
                            j++;

                        int id = (int)Long.parseLong(idStr.substring(i, j));
                        if(videoIdsToInclude.contains(Integer.valueOf(id))) {
                            includedL10nResourcesOrdinals.set(l10nOrdinal);
                            break;
                        }

                        i = j;
                    }
                }
            } else {
                if (isIncludeNonVideoL10N) includedL10nResourcesOrdinals.set(l10nOrdinal);
            }

            l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(l10nOrdinal + 1);
        }

        return includedL10nResourcesOrdinals;
    }
}
