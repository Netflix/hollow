package com.netflix.hollow.core.util;

import java.util.BitSet;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

public class ChangedOrdinalIterator implements HollowOrdinalIterator {
	public static enum ChangeType {ADDED, REMOVED}; 
	
    private final BitSet fromStateOrdinals;
    private final BitSet toStateOrdinals;
    private final int fromOrdinalsLength;
    private int ordinal = -1;
    
    public ChangedOrdinalIterator(HollowReadStateEngine stateEngine, String typeName, ChangeType type){
    	HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) stateEngine.getTypeState(typeName);
        if(type == ChangeType.REMOVED){
	        toStateOrdinals = typeState.getPopulatedOrdinals();
	        fromStateOrdinals = typeState.getPreviousOrdinals();
        } else{
        	fromStateOrdinals = typeState.getPopulatedOrdinals();
            toStateOrdinals = typeState.getPreviousOrdinals();
        }
        fromOrdinalsLength = fromStateOrdinals.length();
    }
    
	@Override
	public int next() {
        while(ordinal < fromOrdinalsLength) {
            ordinal = toStateOrdinals.nextClearBit(ordinal + 1);
            if(fromStateOrdinals.get(ordinal))
                return ordinal;
        }
        return NO_MORE_ORDINALS;
	}
	

    public void reset() {
        ordinal = -1;
    }

    public int countTotal() {
        int bookmark = ordinal;

        reset();

        int count = 0;
        while(next() != -1)
            count++;

        ordinal = bookmark;

        return count;
    }
}
