package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class StreamAssetTypeTypeAPI extends HollowObjectTypeAPI {

    private final StreamAssetTypeDelegateLookupImpl delegateLookupImpl;

    StreamAssetTypeTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "assetTypeId",
            "assetType"
        });
        this.delegateLookupImpl = new StreamAssetTypeDelegateLookupImpl(this);
    }

    public long getAssetTypeId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("StreamAssetType", ordinal, "assetTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAssetTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("StreamAssetType", ordinal, "assetTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAssetTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamAssetType", ordinal, "assetType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAssetTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public StreamAssetTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}