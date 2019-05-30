package com.netflix.vms.transformer.gatekeeper2migration;

import static com.netflix.vms.transformer.index.IndexSpec.ALL_VIDEO_STATUS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_STATUS;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.hollowinput.StatusHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class GatekeeperStatusRetriever {
    
    private final VMSHollowInputAPI gk2StatusAPI;
    private final HollowPrimaryKeyIndex gk2StatusIdx;
    private final Set<String> gk2RolloutCountries;
    
    private final VMSHollowInputAPI converterInputAPI;
    private final HollowPrimaryKeyIndex converterStatusIdx;
    private final HollowHashIndex converterAllStatusIdx;
    
    public GatekeeperStatusRetriever(
            VMSHollowInputAPI gk2StatusAPI,
            VMSHollowInputAPI converterInputAPI,
            Set<String> gk2RolloutCountries,
            VMSTransformerIndexer indexer) {

        this.gk2StatusAPI = gk2StatusAPI;
        this.gk2RolloutCountries = gk2RolloutCountries;
        this.gk2StatusIdx = new HollowPrimaryKeyIndex((HollowReadStateEngine) gk2StatusAPI.getDataAccess(), new PrimaryKey("Status", "movieId", "countryCode"));
        
        this.converterInputAPI = converterInputAPI;
        this.converterStatusIdx = indexer.getPrimaryKeyIndex(VIDEO_STATUS);
        this.converterAllStatusIdx = indexer.getHashIndex(ALL_VIDEO_STATUS);
    }
    
    /** This constructor is for testing purposes only */
    public GatekeeperStatusRetriever(VMSHollowInputAPI converterInputAPI, VMSTransformerIndexer indexer) {
        this.gk2StatusAPI = null;
        this.gk2RolloutCountries = Collections.emptySet();
        this.gk2StatusIdx = null;
        
        this.converterInputAPI = converterInputAPI;
        this.converterStatusIdx = indexer.getPrimaryKeyIndex(VIDEO_STATUS);
        this.converterAllStatusIdx = indexer.getHashIndex(ALL_VIDEO_STATUS);
    }
    
    public StatusHollow getStatus(Long videoId, String country) {
        if(gk2RolloutCountries.contains(country)) {
            int ordinal = gk2StatusIdx.getMatchingOrdinal(videoId, country);
            return ordinal == -1 ? null : gk2StatusAPI.getStatusHollow(ordinal);
        } else {
            int ordinal = converterStatusIdx.getMatchingOrdinal(videoId, country);
            return ordinal == -1 ? null : converterInputAPI.getStatusHollow(ordinal);
        }
    }
    
    public Iterable<StatusHollow> getAllStatus(Long videoId) {
        HollowHashIndexResult converterMatches = converterAllStatusIdx.findMatches(videoId);
        
        return () -> new Iterator<StatusHollow>() {
            private final Iterator<String> gk2RolloutCountriesIterator = gk2RolloutCountries.iterator();
            private final HollowOrdinalIterator iter = converterMatches == null ? EMPTY_ORDINAL_ITERATOR : converterMatches.iterator();
            private int nextConverterOrdinal = iter.next();
            private StatusHollow nextStatus = findNextStatus();
            
            public boolean hasNext() {
                return nextStatus != null;
            }

            @Override
            public StatusHollow next() {
                if(nextStatus == null)
                    throw new NoSuchElementException();
                StatusHollow s = nextStatus;
                nextStatus = findNextStatus();
                return s;
            }
            
            private StatusHollow findNextStatus() {
                while(nextConverterOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    StatusHollow status = converterInputAPI.getStatusHollow(nextConverterOrdinal);  
                    nextConverterOrdinal = iter.next();
                    if(!gk2RolloutCountries.contains(status._getCountryCode()._getValue())) {
                        return status;
                    }
                }
                
                while(gk2RolloutCountriesIterator.hasNext()) {
                    String country = gk2RolloutCountriesIterator.next();
                    int gk2Ordinal = gk2StatusIdx.getMatchingOrdinal(videoId, country);
                    if(gk2Ordinal != -1) {
                        return gk2StatusAPI.getStatusHollow(gk2Ordinal);
                    }
                }
                
                return null;
            }
        };
    }

    private static final HollowOrdinalIterator EMPTY_ORDINAL_ITERATOR = () -> HollowOrdinalIterator.NO_MORE_ORDINALS;
    
}
