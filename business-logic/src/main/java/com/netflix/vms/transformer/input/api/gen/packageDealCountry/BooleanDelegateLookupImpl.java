package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class BooleanDelegateLookupImpl extends HollowObjectAbstractDelegate implements BooleanDelegate {

    private final BooleanTypeAPI typeAPI;

    public BooleanDelegateLookupImpl(BooleanTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public boolean getValue(int ordinal) {
        return typeAPI.getValue(ordinal);
    }

    public Boolean getValueBoxed(int ordinal) {
        return typeAPI.getValueBoxed(ordinal);
    }

    public BooleanTypeAPI getTypeAPI() {
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