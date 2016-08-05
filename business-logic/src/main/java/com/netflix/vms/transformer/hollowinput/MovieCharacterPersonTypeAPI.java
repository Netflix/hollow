package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieCharacterPersonTypeAPI extends HollowObjectTypeAPI {

    private final MovieCharacterPersonDelegateLookupImpl delegateLookupImpl;

    MovieCharacterPersonTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "characters"
        });
        this.delegateLookupImpl = new MovieCharacterPersonDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("MovieCharacterPerson", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("MovieCharacterPerson", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCharactersOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieCharacterPerson", ordinal, "characters");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CharacterListTypeAPI getCharactersTypeAPI() {
        return getAPI().getCharacterListTypeAPI();
    }

    public MovieCharacterPersonDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}