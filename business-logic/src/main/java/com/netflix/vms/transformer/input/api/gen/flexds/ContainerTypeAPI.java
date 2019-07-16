package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ContainerTypeAPI extends HollowObjectTypeAPI {

    private final ContainerDelegateLookupImpl delegateLookupImpl;

    public ContainerTypeAPI(FlexDSAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "sequenceNumber",
            "parentId",
            "dataId"
        });
        this.delegateLookupImpl = new ContainerDelegateLookupImpl(this);
    }

    public int getSequenceNumber(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("Container", ordinal, "sequenceNumber");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("Container", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public long getParentId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Container", ordinal, "parentId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getParentIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Container", ordinal, "parentId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDataId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Container", ordinal, "dataId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getDataIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Container", ordinal, "dataId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ContainerDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public FlexDSAPI getAPI() {
        return (FlexDSAPI) api;
    }

}