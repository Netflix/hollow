package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamBoxInfoTypeAPI extends HollowObjectTypeAPI {

    private final StreamBoxInfoDelegateLookupImpl delegateLookupImpl;

    StreamBoxInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "boxOffset",
            "boxSize",
            "key"
        });
        this.delegateLookupImpl = new StreamBoxInfoDelegateLookupImpl(this);
    }

    public int getBoxOffset(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("StreamBoxInfo", ordinal, "boxOffset");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getBoxOffsetBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("StreamBoxInfo", ordinal, "boxOffset");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getBoxSize(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("StreamBoxInfo", ordinal, "boxSize");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getBoxSizeBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("StreamBoxInfo", ordinal, "boxSize");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getKeyOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamBoxInfo", ordinal, "key");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StreamBoxInfoKeyTypeAPI getKeyTypeAPI() {
        return getAPI().getStreamBoxInfoKeyTypeAPI();
    }

    public StreamBoxInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}