package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedCertificationSystemsRatingDelegate extends HollowObjectDelegate {

    public int getRatingCodeOrdinal(int ordinal);

    public int getRatingCodesOrdinal(int ordinal);

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public long getMaturityLevel(int ordinal);

    public Long getMaturityLevelBoxed(int ordinal);

    public int getDescriptionsOrdinal(int ordinal);

    public ConsolidatedCertificationSystemsRatingTypeAPI getTypeAPI();

}