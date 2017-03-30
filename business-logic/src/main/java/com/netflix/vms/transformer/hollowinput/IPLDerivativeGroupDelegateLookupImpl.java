package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLDerivativeGroupDelegateLookupImpl extends HollowObjectAbstractDelegate implements IPLDerivativeGroupDelegate {

    private final IPLDerivativeGroupTypeAPI typeAPI;

    public IPLDerivativeGroupDelegateLookupImpl(IPLDerivativeGroupTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getExternalIdOrdinal(int ordinal) {
        return typeAPI.getExternalIdOrdinal(ordinal);
    }

    public int getSubmission(int ordinal) {
        return typeAPI.getSubmission(ordinal);
    }

    public Integer getSubmissionBoxed(int ordinal) {
        return typeAPI.getSubmissionBoxed(ordinal);
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public int getDerivativesOrdinal(int ordinal) {
        return typeAPI.getDerivativesOrdinal(ordinal);
    }

    public IPLDerivativeGroupTypeAPI getTypeAPI() {
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