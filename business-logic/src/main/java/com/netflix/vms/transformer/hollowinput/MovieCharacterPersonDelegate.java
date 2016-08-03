package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieCharacterPersonDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public long getPersonId(int ordinal);

    public Long getPersonIdBoxed(int ordinal);

    public long getCharacterId(int ordinal);

    public Long getCharacterIdBoxed(int ordinal);

    public MovieCharacterPersonTypeAPI getTypeAPI();

}