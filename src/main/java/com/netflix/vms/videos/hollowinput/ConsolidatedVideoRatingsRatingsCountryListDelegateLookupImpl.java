package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedVideoRatingsRatingsCountryListDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedVideoRatingsRatingsCountryListDelegate {

    private final ConsolidatedVideoRatingsRatingsCountryListTypeAPI typeAPI;

    public ConsolidatedVideoRatingsRatingsCountryListDelegateLookupImpl(ConsolidatedVideoRatingsRatingsCountryListTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public ConsolidatedVideoRatingsRatingsCountryListTypeAPI getTypeAPI() {
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