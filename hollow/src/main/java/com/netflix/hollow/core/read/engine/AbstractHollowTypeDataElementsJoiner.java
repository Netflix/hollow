package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

public abstract class AbstractHollowTypeDataElementsJoiner <T extends AbstractHollowTypeDataElements> {
    public final int fromMask;
    public final int fromOrdinalShift;
    public final T[] from;

    public T to;

    public AbstractHollowTypeDataElementsJoiner(T[] from) {
        this.from = from;
        this.fromMask = from.length - 1;
        this.fromOrdinalShift = 31 - Integer.numberOfLeadingZeros(from.length);

        if (from.length<=0 || !((from.length&(from.length-1))==0)) {
            throw new IllegalStateException("No. of DataElements to be joined must be a power of 2");
        }

        for (AbstractHollowTypeDataElements elements : from) {
            if (elements.encodedAdditions != null) {
                throw new IllegalStateException("Encountered encodedAdditions in data elements splitter- this is not expected " +
                        "since encodedAdditions only exist on delta data elements and they dont carry over to target data elements, " +
                        "delta data elements are never split/joined");
            }
        }
    }

    public T join() {

        init();

        to.maxOrdinal = -1;

        populateStats();

        copyRecords();

        GapEncodedVariableLengthIntegerReader[] fromRemovals = new GapEncodedVariableLengthIntegerReader[from.length];
        for (int i=0;i<from.length;i++) {
            fromRemovals[i] = from[i].encodedRemovals;
        }
        to.encodedRemovals = GapEncodedVariableLengthIntegerReader.join(fromRemovals);

        return (T) to;
    }

    public abstract void init();

    public abstract void populateStats();

    public abstract void copyRecords();


}
