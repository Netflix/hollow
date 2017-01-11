package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ConsolidatedVideoCountryRatingDelegate extends HollowObjectDelegate {

    public int getAdvisoriesOrdinal(int ordinal);

    public int getReasonsOrdinal(int ordinal);

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public long getCertificationSystemId(int ordinal);

    public Long getCertificationSystemIdBoxed(int ordinal);

    public ConsolidatedVideoCountryRatingTypeAPI getTypeAPI();

}