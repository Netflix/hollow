package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VMSAwardDelegate extends HollowObjectDelegate {

    public long getAwardId(int ordinal);

    public Long getAwardIdBoxed(int ordinal);

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public long getSequenceNumber(int ordinal);

    public Long getSequenceNumberBoxed(int ordinal);

    public boolean getIsMovieAward(int ordinal);

    public Boolean getIsMovieAwardBoxed(int ordinal);

    public long getFestivalId(int ordinal);

    public Long getFestivalIdBoxed(int ordinal);

    public VMSAwardTypeAPI getTypeAPI();

}