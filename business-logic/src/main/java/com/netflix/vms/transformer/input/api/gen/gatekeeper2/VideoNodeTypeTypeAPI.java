package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoNodeTypeTypeAPI extends HollowObjectTypeAPI {

    private final VideoNodeTypeDelegateLookupImpl delegateLookupImpl;

    public VideoNodeTypeTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "nodeType"
        });
        this.delegateLookupImpl = new VideoNodeTypeDelegateLookupImpl(this);
    }

    public String getNodeType(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("VideoNodeType", ordinal, "nodeType");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isNodeTypeEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("VideoNodeType", ordinal, "nodeType", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    public VideoNodeTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}