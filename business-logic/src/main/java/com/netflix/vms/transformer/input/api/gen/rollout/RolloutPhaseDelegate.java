package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseDelegate extends HollowObjectDelegate {

    public long getSeasonMovieId(int ordinal);

    public Long getSeasonMovieIdBoxed(int ordinal);

    public int getElementsOrdinal(int ordinal);

    public String getName(int ordinal);

    public boolean isNameEqual(int ordinal, String testValue);

    public int getNameOrdinal(int ordinal);

    public boolean getShowCoreMetadata(int ordinal);

    public Boolean getShowCoreMetadataBoxed(int ordinal);

    public int getWindowsOrdinal(int ordinal);

    public String getPhaseType(int ordinal);

    public boolean isPhaseTypeEqual(int ordinal, String testValue);

    public int getPhaseTypeOrdinal(int ordinal);

    public boolean getOnHold(int ordinal);

    public Boolean getOnHoldBoxed(int ordinal);

    public RolloutPhaseTypeAPI getTypeAPI();

}