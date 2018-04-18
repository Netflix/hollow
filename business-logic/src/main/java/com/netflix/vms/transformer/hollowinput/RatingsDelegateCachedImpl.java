package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RatingsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RatingsDelegate {

    private final Long ratingId;
    private final int ratingCodeOrdinal;
    private final int descriptionOrdinal;
    private RatingsTypeAPI typeAPI;

    public RatingsDelegateCachedImpl(RatingsTypeAPI typeAPI, int ordinal) {
        this.ratingId = typeAPI.getRatingIdBoxed(ordinal);
        this.ratingCodeOrdinal = typeAPI.getRatingCodeOrdinal(ordinal);
        this.descriptionOrdinal = typeAPI.getDescriptionOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getRatingId(int ordinal) {
        if(ratingId == null)
            return Long.MIN_VALUE;
        return ratingId.longValue();
    }

    public Long getRatingIdBoxed(int ordinal) {
        return ratingId;
    }

    public int getRatingCodeOrdinal(int ordinal) {
        return ratingCodeOrdinal;
    }

    public int getDescriptionOrdinal(int ordinal) {
        return descriptionOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RatingsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RatingsTypeAPI) typeAPI;
    }

}