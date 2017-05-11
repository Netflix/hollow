package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetDelegateLookupImpl extends HollowObjectAbstractDelegate implements IPLArtworkDerivativeSetDelegate {

    private final IPLArtworkDerivativeSetTypeAPI typeAPI;

    public IPLArtworkDerivativeSetDelegateLookupImpl(IPLArtworkDerivativeSetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getDerivativeSetIdOrdinal(int ordinal) {
        return typeAPI.getDerivativeSetIdOrdinal(ordinal);
    }

    public int getDerivativesGroupBySourceOrdinal(int ordinal) {
        return typeAPI.getDerivativesGroupBySourceOrdinal(ordinal);
    }

    public IPLArtworkDerivativeSetTypeAPI getTypeAPI() {
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