package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface AwardDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getWinnerOrdinal(int ordinal);

    public int getNomineesOrdinal(int ordinal);

    public AwardTypeAPI getTypeAPI();

}