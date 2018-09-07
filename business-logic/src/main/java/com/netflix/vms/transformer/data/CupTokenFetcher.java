package com.netflix.vms.transformer.data;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

/**
 * A helper class to encapsulate extracting Cup tokens from the HollowInputAPI. Once we finish the migration from
 * Beehive cup tokens to Cinder cup tokens, this class can be removed and its functionality inlined.
 */
public class CupTokenFetcher {
    private final HollowPrimaryKeyIndex cupTokenCinderIndex;
    private final HollowHashIndex cupTokenCinderHashIndex;
    private final TransformerConfig config;
    private final VMSHollowInputAPI api;

    public CupTokenFetcher(TransformerConfig config, VMSTransformerIndexer indexer,
            VMSHollowInputAPI api) {
        this.config = config;
        this.cupTokenCinderIndex = indexer.getPrimaryKeyIndex(IndexSpec.CUP_TOKEN);
        this.cupTokenCinderHashIndex = indexer.getHashIndex(IndexSpec.CUP_TOKEN_BY_DEALID);
        this.api = api;
    }
    

    public Strings getCupToken(long videoId, ContractHollow contract) {
        return new Strings(getCupTokenString(videoId, contract));
    }

    public String getCupTokenString(long videoId, ContractHollow contract) {
        if (contract == null) {
            return CupKey.DEFAULT;
        }
        long contractOrDealId = (config.isUseContractIdInsteadOfDealId()) ? contract._getContractId() : contract._getDealId();
        return getCupTokenStringCinder(videoId, contractOrDealId);
    }
    

    private String getCupTokenStringCinder(long videoId, long contractOrDealId) {
    	if(config.isUseContractIdInsteadOfDealId()) {
            int ordinal = cupTokenCinderIndex.getMatchingOrdinal(videoId, contractOrDealId);
            return ordinal == -1 ? CupKey.DEFAULT : api.getCinderCupTokenRecordHollow(ordinal)._getCupTokenId()._getValue();
    		
    	} else {
        	HollowHashIndexResult result = cupTokenCinderHashIndex.findMatches(videoId, contractOrDealId);
        	if(result == null) 
        		return CupKey.DEFAULT;
        	HollowOrdinalIterator iter = result.iterator();
        	int ordinal = iter.next();
        	if(ordinal == HollowOrdinalIterator.NO_MORE_ORDINALS)
        		return CupKey.DEFAULT;
        	return api.getCinderCupTokenRecordHollow(ordinal)._getCupTokenId()._getValue();    		
    	}
    }
}
