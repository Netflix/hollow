package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SupplementalsDelegateLookupImpl extends HollowObjectAbstractDelegate implements SupplementalsDelegate {

    private final SupplementalsTypeAPI typeAPI;

    public SupplementalsDelegateLookupImpl(SupplementalsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getSupplementalsOrdinal(int ordinal) {
        return typeAPI.getSupplementalsOrdinal(ordinal);
    }

    public SupplementalsTypeAPI getTypeAPI() {
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