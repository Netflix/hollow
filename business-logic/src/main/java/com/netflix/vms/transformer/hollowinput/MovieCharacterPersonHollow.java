package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieCharacterPersonHollow extends HollowObject {

    public MovieCharacterPersonHollow(MovieCharacterPersonDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public CharacterListHollow _getCharacters() {
        int refOrdinal = delegate().getCharactersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCharacterListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public MovieCharacterPersonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieCharacterPersonDelegate delegate() {
        return (MovieCharacterPersonDelegate)delegate;
    }

}