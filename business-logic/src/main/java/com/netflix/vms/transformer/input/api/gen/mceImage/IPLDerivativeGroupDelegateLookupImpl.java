package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLDerivativeGroupDelegateLookupImpl extends HollowObjectAbstractDelegate implements IPLDerivativeGroupDelegate {

    private final IPLDerivativeGroupTypeAPI typeAPI;

    public IPLDerivativeGroupDelegateLookupImpl(IPLDerivativeGroupTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getExternalId(int ordinal) {
        ordinal = typeAPI.getExternalIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isExternalIdEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getExternalIdOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
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

    public String getImageType(int ordinal) {
        ordinal = typeAPI.getImageTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isImageTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getImageTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
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