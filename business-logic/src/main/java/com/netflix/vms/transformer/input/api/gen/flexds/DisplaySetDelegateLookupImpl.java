package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisplaySetDelegateLookupImpl extends HollowObjectAbstractDelegate implements DisplaySetDelegate {

    private final DisplaySetTypeAPI typeAPI;

    public DisplaySetDelegateLookupImpl(DisplaySetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSetId(int ordinal) {
        return typeAPI.getSetId(ordinal);
    }

    public Long getSetIdBoxed(int ordinal) {
        return typeAPI.getSetIdBoxed(ordinal);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return typeAPI.getCountryCodesOrdinal(ordinal);
    }

    public boolean getIsDefault(int ordinal) {
        return typeAPI.getIsDefault(ordinal);
    }

    public Boolean getIsDefaultBoxed(int ordinal) {
        return typeAPI.getIsDefaultBoxed(ordinal);
    }

    public int getDisplaySetTypesOrdinal(int ordinal) {
        return typeAPI.getDisplaySetTypesOrdinal(ordinal);
    }

    public int getContainersOrdinal(int ordinal) {
        return typeAPI.getContainersOrdinal(ordinal);
    }

    public int getCreatedOrdinal(int ordinal) {
        return typeAPI.getCreatedOrdinal(ordinal);
    }

    public int getUpdatedOrdinal(int ordinal) {
        return typeAPI.getUpdatedOrdinal(ordinal);
    }

    public DisplaySetTypeAPI getTypeAPI() {
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