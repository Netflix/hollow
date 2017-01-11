package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmSystemIdentifiersDelegateLookupImpl extends HollowObjectAbstractDelegate implements DrmSystemIdentifiersDelegate {

    private final DrmSystemIdentifiersTypeAPI typeAPI;

    public DrmSystemIdentifiersDelegateLookupImpl(DrmSystemIdentifiersTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public int getGuidOrdinal(int ordinal) {
        return typeAPI.getGuidOrdinal(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public boolean getHeaderDataAvailable(int ordinal) {
        return typeAPI.getHeaderDataAvailable(ordinal);
    }

    public Boolean getHeaderDataAvailableBoxed(int ordinal) {
        return typeAPI.getHeaderDataAvailableBoxed(ordinal);
    }

    public DrmSystemIdentifiersTypeAPI getTypeAPI() {
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