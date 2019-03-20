package com.netflix.vms.transformer.util;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VmsAttributeFeedEntryHollow;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;


public class VideoContractUtil {
	
	/*
    public static ContractHollow getContract(VMSHollowInputAPI api, VMSTransformerIndexer indexer, TransformerContext ctx, long videoId, String countryCode, long dealId) {
    	HollowHashIndex videoContractsIdx = null;
    	videoContractsIdx = indexer.getHashIndex(IndexSpec.VIDEO_CONTRACT_BY_DEALID);
    	
    	if(videoContractsIdx == null)
    		return null;
    	
    	// Also check the Cinder feed for the answer
    	lookupInCinderFeed(api, indexer, ctx, videoId, countryCode, dealId);
    	
        HollowHashIndexResult results = videoContractsIdx.findMatches(videoId, countryCode, dealId);
        if (results != null) {
            HollowOrdinalIterator iter = results.iterator();
            int ordinal = iter.next();
            while (ordinal != NO_MORE_ORDINALS) {
                ContractHollow data = api.getContractHollow(ordinal);
                if (data != null) {
                	return data;
                }
                ordinal = iter.next();
            }
        }        
        return null;
    }
    */
    
    public static VmsAttributeFeedEntryHollow getVmsAttributeFeedEntry(VMSHollowInputAPI api, VMSTransformerIndexer indexer, TransformerContext ctx, long videoId, String countryCode, long dealId) {
    	HollowPrimaryKeyIndex idx = null;
    	idx = indexer.getPrimaryKeyIndex(IndexSpec.VMS_ATTRIBUTE_FEED_ENTRY);
    	
    	if(idx == null)
    		return null;
    	
    	// Look up using the primary key: videoId, dealId, countryCode
    	int ordinal = idx.getMatchingOrdinal(videoId, dealId, countryCode);
    	if(ordinal == -1)
    		return null;
    	
    	return api.getVmsAttributeFeedEntryHollow(ordinal);
    }
    
    
}

