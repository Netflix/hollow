package com.netflix.vms.transformer.data;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.hollowoutput.CupKey;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

/**
 * A helper class to encapsulate extracting Cup tokens from the HollowInputAPI. Once we finish the migration from
 * Beehive cup tokens to Cinder cup tokens, this class can be removed and its functionality inlined.
 */
public class CupTokenFetcher {
	private final HollowHashIndex cupTokenHashIndex;
    private final HollowPrimaryKeyIndex cupTokenPrimaryKeyIndex;
    private final VMSHollowInputAPI api;
    private final TransformerConfig config;

    public CupTokenFetcher(VMSTransformerIndexer indexer,
            VMSHollowInputAPI api, TransformerConfig config) {
        this.cupTokenPrimaryKeyIndex = indexer.getPrimaryKeyIndex(IndexSpec.CUP_TOKEN_PINDEX);
        this.cupTokenHashIndex = indexer.getHashIndex(IndexSpec.CUP_TOKEN_HINDEX);
        this.api = api;
        this.config = config;
    }
    
    
    

    public Strings getCupToken(long videoId, VmsAttributeFeedEntryHollow contractAttributes) {
        return new Strings(getCupTokenString(videoId, contractAttributes));
    }

    public String getCupTokenString(long videoId, VmsAttributeFeedEntryHollow contractAttributes) {
        if (contractAttributes == null) {
            return CupKey.DEFAULT;
        }
        long dealId = contractAttributes._getDealId()._getValue();
        return getCupTokenStringCinder(videoId, dealId);
    }
    

    private String getCupTokenStringCinder(long videoId, long dealId) {
    	if(config.useCuptokenFeedWithDealIdBasedPrimaryKey()) {
        	int ordinal = cupTokenPrimaryKeyIndex.getMatchingOrdinal(videoId, dealId);
        	if(ordinal == -1) return CupKey.DEFAULT;
        	return api.getCinderCupTokenRecordHollow(ordinal)._getCupTokenId()._getValue();    		
    	} else {
        	HollowHashIndexResult result = cupTokenHashIndex.findMatches(videoId, dealId);
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
