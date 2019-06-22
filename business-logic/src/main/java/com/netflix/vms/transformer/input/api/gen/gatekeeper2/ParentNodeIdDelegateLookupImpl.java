package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ParentNodeIdDelegateLookupImpl extends HollowObjectAbstractDelegate implements ParentNodeIdDelegate {

    private final ParentNodeIdTypeAPI typeAPI;

    public ParentNodeIdDelegateLookupImpl(ParentNodeIdTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public Long getValueBoxed(int ordinal) {
        return typeAPI.getValueBoxed(ordinal);
    }

    public ParentNodeIdTypeAPI getTypeAPI() {
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