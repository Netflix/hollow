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
	private static boolean useFirstAvailableContract = false;
	
    public static ConsolidatedContractInfo getContract(VMSHollowInputAPI api, VMSTransformerIndexer indexer, long videoId, String countryCode, long contractId) {
    	
    	// Create a new consolidated contract info
    	ConsolidatedContractInfo cinfo = new ConsolidatedContractInfo();
    	
    	HollowHashIndex videoContractsIdx = indexer.getHashIndex(IndexSpec.VIDEO_CONTRACT_BY_CONTRACTID);
        HollowHashIndexResult results = videoContractsIdx.findMatches(videoId, countryCode, contractId);
        if (results != null) {
            HollowOrdinalIterator iter = results.iterator();
            int ordinal = iter.next();
            while (ordinal != NO_MORE_ORDINALS) {
                ContractHollow data = api.getContractHollow(ordinal);
                if (data != null && cinfo.getContract() == null) {
                    cinfo.setContract(data);
                    if(useFirstAvailableContract) {
                    	// Set three values and return
                    	cinfo.setPrePromoDays(data._getPrePromotionDays());
                    	cinfo.setDayAfterBroadcast(data._getDayAfterBroadcast());
                    	cinfo.setDayOfBroadcast(data._getDayOfBroadcast());
                    	return cinfo;
                    }
                }
                
                // Check other three values
                // 1. prepromotion date (min of all)
                // 2. day after broadcast
                // 3. day of broadcast
                if(data != null) {
                	cinfo.setPrePromoDaysIfLesserThanExisting(data._getPrePromotionDays());
                	cinfo.mergeDayAfterBroadcast(data._getDayAfterBroadcast());
                	cinfo.mergeDayOfBroadcast(data._getDayOfBroadcast());
                }
                
                ordinal = iter.next();
            }
        }
        
        return cinfo;
    }
    
}
