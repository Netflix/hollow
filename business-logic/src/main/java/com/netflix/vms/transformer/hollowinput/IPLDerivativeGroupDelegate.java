package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface IPLDerivativeGroupDelegate extends HollowObjectDelegate {

    public int getExternalIdOrdinal(int ordinal);

    public int getSubmission(int ordinal);

    public Integer getSubmissionBoxed(int ordinal);

    public int getImageTypeOrdinal(int ordinal);

    public int getDerivativesOrdinal(int ordinal);

    public IPLDerivativeGroupTypeAPI getTypeAPI();

}