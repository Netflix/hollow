package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLDerivativeGroupDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IPLDerivativeGroupDelegate {

    private final String externalId;
    private final int externalIdOrdinal;
    private final Integer submission;
    private final String imageType;
    private final int imageTypeOrdinal;
    private final int derivativesOrdinal;
    private IPLDerivativeGroupTypeAPI typeAPI;

    public IPLDerivativeGroupDelegateCachedImpl(IPLDerivativeGroupTypeAPI typeAPI, int ordinal) {
        this.externalIdOrdinal = typeAPI.getExternalIdOrdinal(ordinal);
        int externalIdTempOrdinal = externalIdOrdinal;
        this.externalId = externalIdTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(externalIdTempOrdinal);
        this.submission = typeAPI.getSubmissionBoxed(ordinal);
        this.imageTypeOrdinal = typeAPI.getImageTypeOrdinal(ordinal);
        int imageTypeTempOrdinal = imageTypeOrdinal;
        this.imageType = imageTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(imageTypeTempOrdinal);
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getExternalId(int ordinal) {
        return externalId;
    }

    public boolean isExternalIdEqual(int ordinal, String testValue) {
        if(testValue == null)
            return externalId == null;
        return testValue.equals(externalId);
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

    public String getImageType(int ordinal) {
        return imageType;
    }

    public boolean isImageTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return imageType == null;
        return testValue.equals(imageType);
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