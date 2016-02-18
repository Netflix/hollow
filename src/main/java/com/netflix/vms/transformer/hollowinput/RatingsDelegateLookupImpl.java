package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RatingsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RatingsDelegate {

    private final RatingsTypeAPI typeAPI;

    public RatingsDelegateLookupImpl(RatingsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getRatingId(int ordinal) {
        return typeAPI.getRatingId(ordinal);
    }

    public Long getRatingIdBoxed(int ordinal) {
        return typeAPI.getRatingIdBoxed(ordinal);
    }

    public int getRatingCodeOrdinal(int ordinal) {
        return typeAPI.getRatingCodeOrdinal(ordinal);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public RatingsTypeAPI getTypeAPI() {
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