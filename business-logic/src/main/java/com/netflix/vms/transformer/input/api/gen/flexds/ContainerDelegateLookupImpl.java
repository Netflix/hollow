package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ContainerDelegateLookupImpl extends HollowObjectAbstractDelegate implements ContainerDelegate {

    private final ContainerTypeAPI typeAPI;

    public ContainerDelegateLookupImpl(ContainerTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getParentId(int ordinal) {
        return typeAPI.getParentId(ordinal);
    }

    public Long getParentIdBoxed(int ordinal) {
        return typeAPI.getParentIdBoxed(ordinal);
    }

    public long getDataId(int ordinal) {
        return typeAPI.getDataId(ordinal);
    }

    public Long getDataIdBoxed(int ordinal) {
        return typeAPI.getDataIdBoxed(ordinal);
    }

    public ContainerTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}