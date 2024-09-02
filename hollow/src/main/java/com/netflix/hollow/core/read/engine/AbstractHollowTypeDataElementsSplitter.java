package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

/**
 * Join multiple {@code HollowListTypeDataElements}s into 1 {@code HollowListTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
public abstract class AbstractHollowTypeDataElementsSplitter<T extends AbstractHollowTypeDataElements> {
    public final int numSplits;
    public final int toMask;
    public final int toOrdinalShift;
    public final T from;

    public T[] to;

    public AbstractHollowTypeDataElementsSplitter(T from, int numSplits) {
        this.from = from;
        this.numSplits = numSplits;
        this.toMask = numSplits - 1;
        this.toOrdinalShift = 31 - Integer.numberOfLeadingZeros(numSplits);

        if (numSplits<=0 || !((numSplits&(numSplits-1))==0)) {
            throw new IllegalStateException("Must split by power of 2");
        }

        if (from.encodedAdditions != null) {
            throw new IllegalStateException("Encountered encodedAdditions in data elements splitter- this is not expected " +
                    "since encodedAdditions only exist on delta data elements and they dont carry over to target data elements, " +
                    "delta data elements are never split/joined");
        }
    }

    public T[] split() {

        initToElements();
        for(int i=0;i<to.length;i++) {
            to[i].maxOrdinal = -1;
        }

        populateStats();

        copyRecords();

        if (from.encodedRemovals != null) {
            GapEncodedVariableLengthIntegerReader[] splitRemovals = from.encodedRemovals.split(numSplits);
            for(int i=0;i<to.length;i++) {
                to[i].encodedRemovals = splitRemovals[i];
            }
        }

        return to;
    }

    /**
     * Initialize the target data elements.
     */
    public abstract void initToElements();

    /**
     * Populate the stats of the target data elements.
     */
    public abstract void populateStats();

    /**
     * Copy records from the source data elements to the target data elements.
     */
    public abstract void copyRecords();


}
