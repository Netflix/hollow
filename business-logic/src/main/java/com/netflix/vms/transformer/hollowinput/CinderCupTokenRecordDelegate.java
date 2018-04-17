package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CinderCupTokenRecordDelegate extends HollowObjectDelegate {

    public int getMovieIdOrdinal(int ordinal);

    public int getContractIdOrdinal(int ordinal);

    public int getCupTokenIdOrdinal(int ordinal);

    public CinderCupTokenRecordTypeAPI getTypeAPI();

}