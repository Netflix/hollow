package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLDerivativeGroupDelegate extends HollowObjectDelegate {

    public String getExternalId(int ordinal);

    public boolean isExternalIdEqual(int ordinal, String testValue);

    public int getExternalIdOrdinal(int ordinal);

    public int getSubmission(int ordinal);

    public Integer getSubmissionBoxed(int ordinal);

    public String getImageType(int ordinal);

    public boolean isImageTypeEqual(int ordinal, String testValue);

    public int getImageTypeOrdinal(int ordinal);

    public int getDerivativesOrdinal(int ordinal);

    public IPLDerivativeGroupTypeAPI getTypeAPI();

}