package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class TopNAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements TopNAttributesDelegate {

    private final TopNAttributesTypeAPI typeAPI;

    public TopNAttributesDelegateLookupImpl(TopNAttributesTypeAPI typeAPI) {
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

    public TopNAttributesTypeAPI getTypeAPI() {
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