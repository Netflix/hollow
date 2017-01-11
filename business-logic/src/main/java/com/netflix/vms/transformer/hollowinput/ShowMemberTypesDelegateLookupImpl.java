package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowMemberTypesDelegateLookupImpl extends HollowObjectAbstractDelegate implements ShowMemberTypesDelegate {

    private final ShowMemberTypesTypeAPI typeAPI;

    public ShowMemberTypesDelegateLookupImpl(ShowMemberTypesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getShowMemberTypeId(int ordinal) {
        return typeAPI.getShowMemberTypeId(ordinal);
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        return typeAPI.getShowMemberTypeIdBoxed(ordinal);
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return typeAPI.getDisplayNameOrdinal(ordinal);
    }

    public ShowMemberTypesTypeAPI getTypeAPI() {
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