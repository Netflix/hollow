package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNAttributeDelegateLookupImpl extends HollowObjectAbstractDelegate implements TopNAttributeDelegate {

    private final TopNAttributeTypeAPI typeAPI;

    public TopNAttributeDelegateLookupImpl(TopNAttributeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryOrdinal(int ordinal) {
        return typeAPI.getCountryOrdinal(ordinal);
    }

    public int getViewShareOrdinal(int ordinal) {
        return typeAPI.getViewShareOrdinal(ordinal);
    }

    public int getCountryViewHrsOrdinal(int ordinal) {
        return typeAPI.getCountryViewHrsOrdinal(ordinal);
    }

    public TopNAttributeTypeAPI getTypeAPI() {
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