package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedCertSystemRatingDelegate extends HollowObjectDelegate {

    public long getRatingId(int ordinal);

    public Long getRatingIdBoxed(int ordinal);

    public long getMaturityLevel(int ordinal);

    public Long getMaturityLevelBoxed(int ordinal);

    public int getRatingCodeOrdinal(int ordinal);

    public int getRatingCodesOrdinal(int ordinal);

    public int getDescriptionsOrdinal(int ordinal);

    public ConsolidatedCertSystemRatingTypeAPI getTypeAPI();

}