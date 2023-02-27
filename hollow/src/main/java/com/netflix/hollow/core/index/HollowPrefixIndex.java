/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.index;

import static java.util.Objects.requireNonNull;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.util.BitSet;
import java.util.logging.Logger;

/**
 * This class builds a prefix index. A prefix index can be used to build applications like auto-complete or spell checker.
 * The current prefix index implementation is backed by a TST (Ternary Search Tree) that is capable of indexing multiple
 * elements per tree node.
 * <p>
 * Although TSTs are typically more space efficient than tries for prefix search, there are some important
 * considerations when using this implementation that can impact memory usage and query performance:
 * <p><ul>
 * <li> Memory utilization will be efficient with more duplicates in the indexed keys. This is because by default
 *      the underlying TST reserves space for one key reference per node but as it encounters duplicate keys it the data it
 *      dynamically resizes each node in the tree to hold multiple references. This leads to un-utilized space at nodes
 *      corresponding to which there is less duplication in keys and memory churn from resize when building the index. If
 *      measure of duplication of values is known upfront it can be specified during index initialization to minimize resizing.
 * <li> Future: bits per key defaults to 16 (for utf-16), but if the input character set can be represented in fewer bits
 *      (for e.g. 1 for binary strings and 4 for hex strings) then the implementation could support a constructor that accepts
 *      custom bitsPerKey to allocate less space per node.
 * </ul><p>
 * Includes methods for getting stats on memory usage and query performance.
 */
public class HollowPrefixIndex implements HollowTypeStateListener {
    private static final Logger LOG = Logger.getLogger(HollowPrefixIndex.class.getName());

    private final FieldPath fieldPath;
    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final int estimatedMaxStringDuplicates;

    private volatile TST prefixIndexVolatile;
    private ArraySegmentRecycler memoryRecycle;

    private int totalWords;
    private int averageWordLen;
    private int maxOrdinalOfType;

    private boolean buildIndexOnUpdate;

    /**
     * Initializes a new prefix index.
     *
     * This constructor defaults the estimatedMaxStringDuplicates to 1, however while building the index it is observed
     * that an indexed key references more than one records in the type then the prefix index dynamically resizes each node
     * to accommodate the multiple references. Note that this has an adverse impact on memory usage, both in terms of
     * memory footprint of prefix index and memory churn when building the index. If the expected number of duplicate
     * strings across the type are specified upfront (see other constructor) then the memory churn due to resizing can be avoided.
     *
     * @param readStateEngine              state engine to read data from
     * @param type                         type in the read state engine. Ordinals for this type
     *                                     will be returned when queried for a prefix.
     * @param fieldPath                    fieldPath should ultimately lead to a string field.
     *                                     The fields in the path could reference another Object,
     *                                     List, Set or a Map. The fields should be separated by ".".
     *
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        this(readStateEngine, type, fieldPath, 1);
    }

    /**
     * Initializes a new prefix index.
     *
     * @param readStateEngine              state engine to read data from
     * @param type                         type in the read state engine. Ordinals for this type
     *                                     will be returned when queried for a prefix.
     * @param fieldPath                    fieldPath should ultimately lead to a string field.
     *                                     The fields in the path could reference another Object,
     *                                     List, Set or a Map. The fields should be separated by ".".
     * @param estimatedMaxStringDuplicates The estimated number of strings that are duplicated
     *                                     across instances of your type. Note that this means an
     *                                     exactly matching string, not a prefix match. A higher value will mean
     *                                     the prefix tree will reserve more memory to reference several elements per node.
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath,
            int estimatedMaxStringDuplicates) {
        requireNonNull(type, "Hollow Prefix Key Index creation failed because type was null");
        requireNonNull(readStateEngine, "Hollow Prefix Key Index creation for type [" + type
                + "] failed because read state wasn't initialized");

        if (fieldPath == null || fieldPath.isEmpty())
            throw new IllegalArgumentException("fieldPath cannot be null or empty");
        if (estimatedMaxStringDuplicates < 1) {
            throw new IllegalArgumentException("estimatedMaxStringDuplicates cannot be < 1");
        }

        this.readStateEngine = readStateEngine;
        this.type = type;
        this.estimatedMaxStringDuplicates = estimatedMaxStringDuplicates;
        this.fieldPath = new FieldPath(readStateEngine, type, fieldPath);
        if (!this.fieldPath.getLastFieldType().equals(HollowObjectSchema.FieldType.STRING))
            throw new IllegalArgumentException("Field path should lead to a string type");

        // create memory recycle for using shared memory pools.
        memoryRecycle = WastefulRecycler.DEFAULT_INSTANCE;
        buildIndexOnUpdate = true;
        initialize();
    }

    // initialize field positions and field paths.
    private void initialize() {

        String lastRefType = this.fieldPath.getLastRefTypeInPath();

        // get all cardinality to estimate size of array bits needed.
        totalWords = readStateEngine.getTypeState(lastRefType).getPopulatedOrdinals().cardinality();
        averageWordLen = 0;
        double avg = 0;
        HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState(lastRefType);
        BitSet keyBitSet = objectTypeReadState.getPopulatedOrdinals();
        int ordinal = keyBitSet.nextSetBit(0);
        while (ordinal != -1) {
            avg += ((double) objectTypeReadState.readString(ordinal, 0).length()) / ((double) totalWords);
            ordinal = keyBitSet.nextSetBit(ordinal + 1);
        }
        averageWordLen = (int) Math.ceil(avg);

        HollowObjectTypeReadState valueState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess(type);
        maxOrdinalOfType = valueState.maxOrdinal();

        // initialize the prefix index.
        build();
    }

    private void build() {

        if (!buildIndexOnUpdate) return;
        // tell memory recycler to use current tst's long arrays next time when long array is requested.
        // note reuse only happens once swap is called and bits are reset
        TST current = prefixIndexVolatile;
        if (current != null) current.recycleMemory(memoryRecycle);

        // This is a hard limit, and currently assumes worst case unbalanced tree i.e. the total length of all words
        long estimatedMaxNodes = estimateNumNodes(totalWords, averageWordLen);
        TST tst = new TST(estimatedMaxNodes, estimatedMaxStringDuplicates, maxOrdinalOfType,
                memoryRecycle);
        BitSet ordinals = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {
            for (String key : getKeys(ordinal)) {
                tst.insert(key, ordinal);
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        prefixIndexVolatile = tst;
        // safe to return previous long arrays on next request for long array.
        memoryRecycle.swap();
        buildIndexOnUpdate = false;

        Stats stats = usageStats();
        LOG.info("Prefix index built with stats= [" + stats + "]");
    }

    /**
     * Estimates the total number of nodes that will be required to create the index.
     * Override this method if lower/higher estimate is needed compared to the default implementation.
     *
     * @param totalWords the total number of words
     * @param averageWordLen the average word length
     * @return the estimated total number of nodes
     */
    @SuppressWarnings("WeakerAccess")
    protected long estimateNumNodes(long totalWords, long averageWordLen) {
        return totalWords * averageWordLen;
    }

    /**
     * Return the key to index in prefix index. Override this method to support tokens for the key. By default keys are indexed as lower case characters.
     * <pre>{@code
     *     String[] keys = super.getKey(ordinal);
     *     String[] tokens = keys[0].split(" ")
     *     return tokens;
     * }</pre>
     *
     * @param ordinal ordinal of the parent type.
     * @return keys to index.
     */
    protected String[] getKeys(int ordinal) {
        Object[] values = fieldPath.findValues(ordinal);
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            stringValues[i] = ((String) values[i]).toLowerCase();
        }
        return stringValues;
    }

    /**
     * Query the index to find all the ordinals that match the given prefix. Example -
     * <pre>{@code
     *     HollowOrdinalIterator iterator = index.findKeysWithPrefix("a");
     *     int ordinal = iterator.next();
     *     while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINAL) {
     *         // print the result using API
     *     }
     * }</pre>
     * <p>
     * For larger data sets, querying smaller prefixes will be longer than querying for prefixes that are longer.
     *
     * @param prefix findKeysWithPrefix prefix.
     * @return An instance of HollowOrdinalIterator to iterate over ordinals that match the given findKeysWithPrefix.
     */
    @SuppressWarnings("WeakerAccess")
    public HollowOrdinalIterator findKeysWithPrefix(String prefix) {
        TST current;
        HollowOrdinalIterator it;
        do {
            current = prefixIndexVolatile;
            it = current.findKeysWithPrefix(prefix);
        } while (current != this.prefixIndexVolatile);
        return it;
    }

    /**
     * Check if the given key exists in the index.
     *
     * @param key the key
     * @return {@code true} if the key exists, otherwise {@code false}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        TST current;
        boolean result;
        do {
            current = prefixIndexVolatile;
            result = current.contains(key);
        } while (current != this.prefixIndexVolatile);
        return result;
    }

    /**
     * Use this method to keep the index updated with delta changes on the read state engine.
     * Remember to call detachFromDeltaUpdates to stop the delta changes.
     * NOTE: Each delta updates creates a new prefix index and swaps the new with current.
     */
    @SuppressWarnings("WeakerAccess")
    public void listenForDeltaUpdates() {
        readStateEngine.getTypeState(type).addListener(this);
    }

    /**
     * Stop delta updates for this index.
     */
    @SuppressWarnings("WeakerAccess")
    public void detachFromDeltaUpdates() {
        readStateEngine.getTypeState(type).removeListener(this);
    }

    @Override
    public void beginUpdate() {
        // before delta is applied -> no action to be taken
    }

    @Override
    public void addedOrdinal(int ordinal) {
        buildIndexOnUpdate = true;
    }

    @Override
    public void removedOrdinal(int ordinal) {
        buildIndexOnUpdate = true;
    }

    @Override
    public void endUpdate() {
        // pass 1 for delta support - rebuild the tree and swap the new tree with the one that is serving the queries.
        // next pass -  improve the index build time or add support for remove method.
        initialize();
    }

    /**
     * Returns memory usage stats for the prefix index. Not thread-safe with concurrent updates to index.
     * @return approx heap footprint in bytes
     */
    public Stats usageStats() {
        Stats stats = new Stats();
        stats.nodesCapacity = prefixIndexVolatile.getMaxNodes();
        stats.nodesUsed = prefixIndexVolatile.getNumNodes();
        stats.nodesEmpty = prefixIndexVolatile.getEmptyNodes();
        stats.worstCaseLookups = prefixIndexVolatile.getMaxDepth();
        stats.maxValuesPerNode = prefixIndexVolatile.getMaxElementsPerNode();

        stats.approxHeapFootprintInBytes = prefixIndexVolatile.approxHeapFootprintInBytes();
        return stats;
    }

    public static class Stats {
        long nodesCapacity;  // allocated capacity in underlying tree
        long nodesUsed;      // utilized nodes
        long nodesEmpty;     // un-utilized nodes (capacity - utilized)
        long worstCaseLookups;  // no. of nodes looked up for serving worst case query
        int maxValuesPerNode; // a single tree node reserves capacity to reference upto these many records
        long approxHeapFootprintInBytes;    // approx heap footprint of tree

        @Override
        public String toString() {
            return "nodesCapacity=" + nodesCapacity + ", "
                 + "nodesUsed=" + nodesUsed + ", "
                 + "nodesEmpty=" + nodesEmpty + ", "
                 + "worstCaseLookups=" + worstCaseLookups + ", "
                 + "maxValuesPerNode=" + maxValuesPerNode + ", "
                 + "approxHeapFootprintInBytes=" + approxHeapFootprintInBytes;
        }
    }
}
