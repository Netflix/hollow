package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class IPLDerivativeGroupDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLDerivativeGroupDelegate {

    private final int externalIdOrdinal;
    private final Integer submission;
    private final int imageTypeOrdinal;
    private final int derivativesOrdinal;
    private IPLDerivativeGroupTypeAPI typeAPI;

    public IPLDerivativeGroupDelegateCachedImpl(IPLDerivativeGroupTypeAPI typeAPI, int ordinal) {
        this.externalIdOrdinal = typeAPI.getExternalIdOrdinal(ordinal);
        this.submission = typeAPI.getSubmissionBoxed(ordinal);
        this.imageTypeOrdinal = typeAPI.getImageTypeOrdinal(ordinal);
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getExternalIdOrdinal(int ordinal) {
        return externalIdOrdinal;
    }

    public int getSubmission(int ordinal) {
        if(submission == null)
            return Integer.MIN_VALUE;
        return submission.intValue();
    }

    public Integer getSubmissionBoxed(int ordinal) {
        return submission;
    }

    public int getImageTypeOrdinal(int ordinal) {
        return imageTypeOrdinal;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return derivativesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IPLDerivativeGroupTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IPLDerivativeGroupTypeAPI) typeAPI;
    }

}