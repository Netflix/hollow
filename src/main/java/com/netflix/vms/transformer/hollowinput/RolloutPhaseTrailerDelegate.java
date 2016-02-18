package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseTrailerDelegate extends HollowObjectDelegate {

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getTrailerMovieId(int ordinal);

    public Long getTrailerMovieIdBoxed(int ordinal);

    public int getSupplementalInfoOrdinal(int ordinal);

    public RolloutPhaseTrailerTypeAPI getTypeAPI();

}