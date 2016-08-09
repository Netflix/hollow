package com.netflix.vms.transformer.util;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Caches precalculated results which are related to input ordinals. 
 *
 */
public class InputOrdinalResultCache<T> {

    private final AtomicReferenceArray<T> resultsByOrdinal;
    
    public InputOrdinalResultCache(int maxInputOrdinal) {
        this.resultsByOrdinal = new AtomicReferenceArray<T>(maxInputOrdinal+1);
    }
    
    /**
     * Get the precalculated result for the specified input ordinal
     */
    public T getResult(int inputOrdinal) {
        return resultsByOrdinal.get(inputOrdinal);
    }
    
    /**
     * Set the calculated result for the specified input ordinal
     *  
     * @return the canonical result (may not be the specified calculatedResult if some other thread calculated this result first).
     */
    public T setResult(int inputOrdinal, T calculatedResult) {
        if(resultsByOrdinal.compareAndSet(inputOrdinal, null, calculatedResult))
            return calculatedResult;
        
        return getResult(inputOrdinal);
    }
}
