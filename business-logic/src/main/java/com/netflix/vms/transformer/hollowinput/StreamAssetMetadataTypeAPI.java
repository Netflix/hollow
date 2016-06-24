package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamAssetMetadataTypeAPI extends HollowObjectTypeAPI {

    private final StreamAssetMetadataDelegateLookupImpl delegateLookupImpl;

    StreamAssetMetadataTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id"
        });
        this.delegateLookupImpl = new StreamAssetMetadataDelegateLookupImpl(this);
    }

    public String getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("StreamAssetMetadata", ordinal, "id");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isIdEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("StreamAssetMetadata", ordinal, "id", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public StreamAssetMetadataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}