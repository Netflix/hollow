package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CertificationSystemRatingDelegate extends HollowObjectDelegate {

    public int getRatingCodeOrdinal(int ordinal);

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public long getMaturityLevel(int ordinal);

    public Long getMaturityLevelBoxed(int ordinal);

    public CertificationSystemRatingTypeAPI getTypeAPI();

}