package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CinderCupTokenRecordDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public long getDealId(int ordinal);

    public Long getDealIdBoxed(int ordinal);

    public int getDealIdOrdinal(int ordinal);

    public String getCupTokenId(int ordinal);

    public boolean isCupTokenIdEqual(int ordinal, String testValue);

    public int getCupTokenIdOrdinal(int ordinal);

    public CinderCupTokenRecordTypeAPI getTypeAPI();

}