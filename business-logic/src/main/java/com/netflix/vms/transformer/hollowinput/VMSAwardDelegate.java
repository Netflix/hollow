package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VMSAwardDelegate extends HollowObjectDelegate {

    public long getAwardId(int ordinal);

    public Long getAwardIdBoxed(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public long getFestivalId(int ordinal);

    public Long getFestivalIdBoxed(int ordinal);

    public int getCountryCodeOrdinal(int ordinal);

    public boolean getIsMovieAward(int ordinal);

    public Boolean getIsMovieAwardBoxed(int ordinal);

    public VMSAwardTypeAPI getTypeAPI();

}