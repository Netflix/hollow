package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

public abstract class AbstractHollowTypeDataElementsSplitter {
    public final int numSplits;
    public final int toMask;
    public final int toOrdinalShift;
    public final AbstractHollowTypeDataElements from;

    public AbstractHollowTypeDataElements[] to;

    public AbstractHollowTypeDataElementsSplitter(AbstractHollowTypeDataElements from, int numSplits) {
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

    public AbstractHollowTypeDataElements[] split() {
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

    public abstract void populateStats();

    public abstract void copyRecords();


}
