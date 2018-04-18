package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLArtworkDerivativeSetDelegate {

    private final int derivativeSetIdOrdinal;
    private final int derivativesGroupBySourceOrdinal;
    private IPLArtworkDerivativeSetTypeAPI typeAPI;

    public IPLArtworkDerivativeSetDelegateCachedImpl(IPLArtworkDerivativeSetTypeAPI typeAPI, int ordinal) {
        this.derivativeSetIdOrdinal = typeAPI.getDerivativeSetIdOrdinal(ordinal);
        this.derivativesGroupBySourceOrdinal = typeAPI.getDerivativesGroupBySourceOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDerivativeSetIdOrdinal(int ordinal) {
        return derivativeSetIdOrdinal;
    }

    public int getDerivativesGroupBySourceOrdinal(int ordinal) {
        return derivativesGroupBySourceOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IPLArtworkDerivativeSetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IPLArtworkDerivativeSetTypeAPI) typeAPI;
    }

}