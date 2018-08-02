package com.netflix.vms.transformer.util;

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;


public class VideoContractUtil {
	
    public static ContractHollow getContract(VMSHollowInputAPI api, VMSTransformerIndexer indexer, long videoId, String countryCode, long contractId) {    	
    	HollowHashIndex videoContractsIdx = indexer.getHashIndex(IndexSpec.VIDEO_CONTRACT_BY_DEALID);
        HollowHashIndexResult results = videoContractsIdx.findMatches(videoId, countryCode, contractId);
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
    
}

