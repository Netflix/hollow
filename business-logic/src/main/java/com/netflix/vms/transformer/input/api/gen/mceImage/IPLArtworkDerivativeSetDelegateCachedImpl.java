package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLArtworkDerivativeSetDelegate {

    private final String derivativeSetId;
    private final int derivativeSetIdOrdinal;
    private final int derivativesGroupBySourceOrdinal;
    private IPLArtworkDerivativeSetTypeAPI typeAPI;

    public IPLArtworkDerivativeSetDelegateCachedImpl(IPLArtworkDerivativeSetTypeAPI typeAPI, int ordinal) {
        this.derivativeSetIdOrdinal = typeAPI.getDerivativeSetIdOrdinal(ordinal);
        int derivativeSetIdTempOrdinal = derivativeSetIdOrdinal;
        this.derivativeSetId = derivativeSetIdTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(derivativeSetIdTempOrdinal);
        this.derivativesGroupBySourceOrdinal = typeAPI.getDerivativesGroupBySourceOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getDerivativeSetId(int ordinal) {
        return derivativeSetId;
    }

    public boolean isDerivativeSetIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return derivativeSetId == null;
        return testValue.equals(derivativeSetId);
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