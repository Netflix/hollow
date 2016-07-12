package com.netflix.vms.transformer.util;

import static com.netflix.hollow.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.hollowinput.ContractHollow;
import com.netflix.vms.transformer.hollowinput.ContractsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.Map;

public class VideoContractUtil {
    public static ContractHollow getContract(VMSHollowInputAPI api, VMSTransformerIndexer indexer, long videoId, String countryCode, long contractId) {
        HollowHashIndex videoContractsIdx = indexer.getHashIndex(IndexSpec.VIDEO_CONTRACT_BY_CONTRACTID);
        HollowHashIndexResult results = videoContractsIdx.findMatches(videoId, countryCode, contractId);
        if (results != null) {
            HollowOrdinalIterator iter = results.iterator();
            int ordinal = iter.next();
            while (ordinal != NO_MORE_ORDINALS) {
                ContractHollow data = api.getContractHollow(ordinal);
                if (data != null) {
                    return data;
                }
            }
        }

        return null;
    }

    public static Map<Long, ContractHollow> getContractMap(VMSHollowInputAPI api, VMSTransformerIndexer indexer, long videoId, String countryCode, Map<Long, ContractHollow> resultMap) {
        Map<Long, ContractHollow> result = resultMap == null ? new HashMap<>() : resultMap;

        HollowHashIndex videoContractsIdx = indexer.getHashIndex(IndexSpec.VIDEO_CONTRACTS);
        HollowHashIndexResult results = videoContractsIdx.findMatches(videoId, countryCode);
        if (results != null) {
            HollowOrdinalIterator iter = results.iterator();
            int ordinal = iter.next();
            while (ordinal != NO_MORE_ORDINALS) {
                ContractsHollow data = api.getContractsHollow(ordinal);
                if (data != null) {
                    for (ContractHollow contract : data._getContracts()) {
                        result.put(contract._getContractIdBoxed(), contract);
                    }
                }
            }
        }

        return result;
    }
}
