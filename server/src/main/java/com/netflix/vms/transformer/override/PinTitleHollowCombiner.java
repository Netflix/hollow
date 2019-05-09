package com.netflix.vms.transformer.override;

import static com.netflix.vms.transformer.common.config.OutputTypeConfig.NamedCollectionHolder;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CyclePinnedTitles;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerExcludePrimaryKeysCopyDirector;
import com.netflix.type.ISOCountry;
import com.netflix.type.NFCountry;
import com.netflix.vms.generated.notemplate.MapOfStringsToSetOfVideoHollow;
import com.netflix.vms.generated.notemplate.NamedCollectionHolderHollow;
import com.netflix.vms.generated.notemplate.SetOfVideoHollow;
import com.netflix.vms.generated.notemplate.StringsHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.index.VMSOutputTypeIndexer;
import com.netflix.vms.transformer.publish.workflow.IndexDuplicateChecker;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Combiner to handle title override / pinning
 *
 * - Combines NamedLists - special case (lifted off from legacy implementation)
 * - Uses HollowCombinerExcludePrimaryKeysCopyDirector to prioritize Fastlane for Non-Video Related data
 * - Uses HollowCombiner new feature to dedupe/remap referenced keys
 *
 * @author dsu
 */
public class PinTitleHollowCombiner {
    public static final String NAMEDLIST_TYPE_STATE_NAME = NamedCollectionHolder.getType();
    private static final String SET_OF_PREFIX = "SetOf";
    private static final String MAP_OF_PREFIX = "MapOfStringsToSetOf";

    private final TransformerContext ctx;
    private final HollowCombiner combiner;
    private final HollowReadStateEngine inputs[];
    private final HollowWriteStateEngine output;

    // NamedList combined results
    protected final ConcurrentHashMap<ISOCountry, ConcurrentHashMap<String, Set<Integer>>> combinedVideoLists;

    public PinTitleHollowCombiner(TransformerContext ctx, HollowWriteStateEngine output, HollowWriteStateEngine fastlaneOutput, List<HollowReadStateEngine> pinnedTitleInputs) throws Exception {
        this(ctx, output, roundTrip(fastlaneOutput), pinnedTitleInputs);
    }

    public PinTitleHollowCombiner(TransformerContext ctx, HollowWriteStateEngine output, HollowReadStateEngine fastlaneInput, List<HollowReadStateEngine> pinnedTitleInputs) throws Exception {
        this.ctx = ctx;

        this.inputs = createPrioritizedOrderingReadStateEngines(pinnedTitleInputs, fastlaneInput);
        this.output = output;
        this.combiner = initCombiner(this.output, fastlaneInput, pinnedTitleInputs, inputs);

        this.combinedVideoLists = new ConcurrentHashMap<ISOCountry, ConcurrentHashMap<String,Set<Integer>>>();
    }



    /**
     * Order the read state engine such that by default fastlane is at the end - after pinned/override titles.
     * <p>
     * The combiner de-dupes records based on the specified primary keys and the record corresponding to the first occurrence of a primary key wins i.e.,
     * in the event that multiple records with the same primary key exist in the list of inputs, the combiner steps through the list of inputs one by one,
     * and accepts the record corresponding to the first occurrence of the primary key while subsequent duplicate occurrences are ignored. Note that
     * if any record <i>references</i> another record which was omitted because the primary key was deemed duplicate based on this rule, then that reference
     * is remapped in the destination state to the matching record which was chosen to be included.
     * </p>
     */
    private static HollowReadStateEngine[] createPrioritizedOrderingReadStateEngines(List<HollowReadStateEngine> pinnedTitleInputs, HollowReadStateEngine fastlane) throws IOException {
        int size = pinnedTitleInputs.size() + 1;
        HollowReadStateEngine[] outputs = new HollowReadStateEngine[size];

        int i = 0;
        for (HollowReadStateEngine item : pinnedTitleInputs) {
            outputs[i++] = item;
        }
        outputs[i++] = fastlane; // fastlane last

        return outputs;
    }

    // Initialize Hollow Combiner to handle special cases
    private static HollowCombiner initCombiner(HollowWriteStateEngine output, HollowReadStateEngine fastlane, List<HollowReadStateEngine> pinnedTitleInputs, HollowReadStateEngine allInputs[]) {
        if (pinnedTitleInputs.isEmpty() && fastlane != null) {
            // Only fastlane and no override/pin title exists so just use simple combiner
            HollowCombiner combiner = new HollowCombiner(output, fastlane);
            addExcludedTypes(combiner);
            return combiner;
        }

        /**
         * 1) allInputs should have fastlane at the end of the list so that pinned video has higher precedence over fastlane.
         * 2) Prioritize Fastlane non video related data - allows newer non-video data to have higher precedence.
         *    For non-video data, we want records in the fast lane to win over records with duplicate primary keys from pinned sources.
         *    To that effect, the <i>prioritizeFastLaneTypes</i> method iterates over the records in the fast lane, and for each
         *    primary key value drops the duplicate records from the pinned data sources so that when the combiner executes,
         *    it gets all the fast lane records.
         */
        HollowCombinerExcludePrimaryKeysCopyDirector copyDirector = new HollowCombinerExcludePrimaryKeysCopyDirector();
        prioritizeFastLaneTypes(copyDirector, OutputTypeConfig.NON_VIDEO_RELATED_TYPES, fastlane, pinnedTitleInputs, allInputs);
        HollowCombiner combiner = new HollowCombiner(copyDirector, output, allInputs);

        // 3) Configure to skip NamedLists and its sub-types
        addExcludedTypes(combiner);

        // 4) Configure PrimaryKey for record deduping and remapping
        List<PrimaryKey> primaryKeys = new ArrayList<>();
        for (OutputTypeConfig config : OutputTypeConfig.values()) {
            primaryKeys.add(config.getPrimaryKey());
        }
        combiner.setPrimaryKeys(primaryKeys.toArray(new PrimaryKey[0]));

        return combiner;
    }

    private static void addExcludedTypes(HollowCombiner combiner) {
        // NamedList needs special manual combining so must be excluded as well as its sub-types
        combiner.addIgnoredTypes(NamedCollectionHolder.getType());
        for (String subType : Arrays.asList("Video", "Episode", "VPerson")) {
            combiner.addIgnoredTypes(SET_OF_PREFIX + subType);
            combiner.addIgnoredTypes(MAP_OF_PREFIX + subType);
        }
    }

    // Configure HollowCombinerExcludePrimaryKeysCopyDirector to favor fastlane for non-video related data
    private static void prioritizeFastLaneTypes(HollowCombinerExcludePrimaryKeysCopyDirector copyCombiner, Set<OutputTypeConfig> types, HollowReadStateEngine fastlane, List<HollowReadStateEngine> pinnedTitleInputs, HollowReadStateEngine allInputs[]) {
        // create Index map
        Map<HollowReadStateEngine, VMSOutputTypeIndexer> indexerMap = new HashMap<>();
        for (HollowReadStateEngine stateEngine : allInputs) {
            String blobID = PinTitleHelper.getBlobID(stateEngine);
            VMSOutputTypeIndexer indexer = new VMSOutputTypeIndexer(blobID, stateEngine, types);
            indexerMap.put(stateEngine, indexer);
        }

        // loop through all types that should favor fastlane
        for (OutputTypeConfig type : types) {
            String typeName = type.getType();
            HollowTypeReadState fastlaneTypeState = fastlane.getTypeState(typeName);
            if (fastlaneTypeState == null) continue; // skip - fastlane does not have this type

            PopulatedOrdinalListener fastlaneTypeListener = fastlaneTypeState.getListener(PopulatedOrdinalListener.class);
            BitSet populatedOrdinals = fastlaneTypeListener.getPopulatedOrdinals();
            if (populatedOrdinals.isEmpty()) continue; // skip - fastlane has no data for this type

            // loop through fastlane keys
            VMSOutputTypeIndexer fastlaneIndexer = indexerMap.get(fastlane);
            HollowPrimaryKeyIndex fastlaneIdx = fastlaneIndexer.getPrimaryKeyIndex(typeName);
            int nextOrdinal = populatedOrdinals.nextSetBit(0);
            while(nextOrdinal != -1) {
                Object[] fastlaneRecKey = fastlaneIdx.getRecordKey(nextOrdinal);

                // Exclude fastlane key from other inputs
                for (HollowReadStateEngine input : pinnedTitleInputs) {
                    VMSOutputTypeIndexer inputIndexer = indexerMap.get(input);
                    HollowPrimaryKeyIndex inputIdx = inputIndexer.getPrimaryKeyIndex(typeName);
                    if (inputIdx == null) continue;

                    copyCombiner.excludeKey(inputIdx, fastlaneRecKey);
                }

                nextOrdinal = populatedOrdinals.nextSetBit(nextOrdinal + 1);
            }
        }

        // Make sure to exclude sub-types; otherwise, there might be a possibility of data flapping between (sub-types copied from any inputs)
        copyCombiner.excludeReferencedObjects();
    }

    /**
     * Return combined Write State Engine
     */
    public HollowWriteStateEngine getCombinedStateEngine() {
        return output;
    }

    public void combine() throws Exception {
        combiner.combine();
        combineNamedLists();
        writeNamedListsToOutput(output);
        PinTitleHelper.combineHeader(output, inputs);
        validateCombinedData(output);
    }

    // Validates the combined data to check for duplicates
    private void validateCombinedData(HollowWriteStateEngine outputStateEngine) throws Exception {
        HollowReadStateEngine stateEngine = roundTrip(outputStateEngine);

        IndexDuplicateChecker dupChecker = new IndexDuplicateChecker(stateEngine);
        dupChecker.checkDuplicates();

        if (dupChecker.wasDupKeysDetected()) {
            Map<String, Collection<Object[]>> dups = dupChecker.getResults();

            for(Map.Entry<String, Collection<Object[]>> dupEntry : dups.entrySet()) {
                StringBuilder message = new StringBuilder("Duplicate keys detected in type " + dupEntry.getKey() + ": ");
                for(Object[] key : dupEntry.getValue()) {
                    message.append(Arrays.toString(key)).append(" ");
                }
                ctx.getLogger().error(CyclePinnedTitles, message.toString());
            }

            throw new Exception("Duplicate Keys detected in Core Type(s): " + dupChecker.getResults().keySet());
        }
    }

    public static HollowReadStateEngine roundTrip(HollowWriteStateEngine writeEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        reader.readSnapshot(is);

        return readEngine;
    }


    // ----- Logic to combine NamedList

    protected void combineNamedLists() throws Exception {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "combine-named-lists");

        for (HollowReadStateEngine input : inputs) {
            VMSRawHollowAPI api = new VMSRawHollowAPI(input);
            HollowObjectTypeReadState namedCollectionHolderState = (HollowObjectTypeReadState) input.getTypeState(NAMEDLIST_TYPE_STATE_NAME);

            PopulatedOrdinalListener listener = namedCollectionHolderState.getListener(PopulatedOrdinalListener.class);
            BitSet populatedOrdinals = listener.getPopulatedOrdinals();

            int ordinal = populatedOrdinals.nextSetBit(0);
            while (ordinal != -1) {
                traverseShardNamedCollectionHolder(api, ordinal, executor);

                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            }
        }

        executor.awaitSuccessfulCompletion();
    }

    private void traverseShardNamedCollectionHolder(VMSRawHollowAPI api, int ordinal, SimultaneousExecutor executor) {
        final NamedCollectionHolderHollow holder = api.getNamedCollectionHolderHollow(ordinal);
        final String countryId = holder._getCountry()._getId();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, Set<Integer>> countryMap = getCountryMap(combinedVideoLists, countryId);
                MapOfStringsToSetOfVideoHollow videoListMaps = holder._getVideoListMap();
                for (Map.Entry<StringsHollow, SetOfVideoHollow> entry : videoListMaps.entrySet()) {
                    Set<Integer> namedSet = getNamedSet(countryMap, entry.getKey()._getValue());
                    synchronized (namedSet) {
                        for (VideoHollow video : entry.getValue()) {
                            namedSet.add(video._getValueBoxed());
                        }
                    }
                }
            }
        });
    }

    private ConcurrentHashMap<String, Set<Integer>> getCountryMap(ConcurrentHashMap<ISOCountry, ConcurrentHashMap<String, Set<Integer>>> combinedMaps, String countryId) {
        NFCountry country = NFCountry.findInstance(countryId);

        ConcurrentHashMap<String, Set<Integer>> map = combinedMaps.get(country);
        if (map == null) {
            map = new ConcurrentHashMap<String, Set<Integer>>();
            ConcurrentHashMap<String, Set<Integer>> existingMap = combinedMaps.putIfAbsent(country, map);
            if (existingMap != null)
                map = existingMap;
        }

        return map;
    }

    private Set<Integer> getNamedSet(ConcurrentHashMap<String, Set<Integer>> countryMaps, String listName) {
        Set<Integer> set = countryMaps.get(listName);

        if (set == null) {
            set = new HashSet<Integer>();
            Set<Integer> existingSet = countryMaps.putIfAbsent(listName, set);
            if (existingSet != null)
                set = existingSet;
        }

        return set;
    }

    private void writeNamedListsToOutput(HollowWriteStateEngine output) throws Exception {
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-named-lists");

        for(final Entry<ISOCountry, ConcurrentHashMap<String, Set<Integer>>> entry : combinedVideoLists.entrySet()) {
            final ISOCountry country = entry.getKey();

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ConcurrentHashMap<String, Set<Integer>> videoLists = entry.getValue();

                    HollowObjectWriteRecord holderRec = new HollowObjectWriteRecord((HollowObjectSchema) output.getSchema("NamedCollectionHolder"));
                    HollowObjectWriteRecord countryRec = new HollowObjectWriteRecord((HollowObjectSchema) output.getSchema("ISOCountry"));
                    countryRec.setString("id", country.getId());

                    HollowObjectWriteRecord videoRec = new HollowObjectWriteRecord((HollowObjectSchema)output.getSchema("Video"));

                    int videoMapOrdinal = writeToOutput(videoLists, videoRec, "Video", "value", output);
                    int countryOrdinal = output.add("ISOCountry", countryRec);

                    holderRec.setReference("videoListMap", videoMapOrdinal);
                    holderRec.setReference("country", countryOrdinal);

                    output.add("NamedCollectionHolder", holderRec);
                }
            });
        }

        executor.awaitSuccessfulCompletion();
    }

    private int writeToOutput(Map<String, Set<Integer>> itemLists, HollowObjectWriteRecord itemRec, String typeName, String itemIdFieldName, HollowWriteStateEngine output) {
        String setName = SET_OF_PREFIX + typeName;
        HollowMapWriteRecord mapRec = new HollowMapWriteRecord();
        HollowSetWriteRecord setRec = new HollowSetWriteRecord();
        HollowObjectWriteRecord stringsRec = new HollowObjectWriteRecord((HollowObjectSchema)output.getSchema("Strings"));

        for(Map.Entry<String, Set<Integer>> itemListEntry : itemLists.entrySet()) {
            stringsRec.setString("value", itemListEntry.getKey());
            int nameOrdinal = output.add("Strings", stringsRec);
            setRec.reset();
            for(Integer itemId : itemListEntry.getValue()) {
                itemRec.setInt(itemIdFieldName, itemId.intValue());
                int itemOrdinal = output.add(typeName, itemRec);
                setRec.addElement(itemOrdinal, itemId.intValue());
            }

            int setOrdinal = output.add(setName, setRec);
            mapRec.addEntry(nameOrdinal, setOrdinal, itemListEntry.getKey().hashCode());
        }

        return output.add(MAP_OF_PREFIX + typeName, mapRec);
    }
}