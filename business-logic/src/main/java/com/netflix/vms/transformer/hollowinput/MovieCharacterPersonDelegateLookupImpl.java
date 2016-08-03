package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieCharacterPersonDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieCharacterPersonDelegate {

    private final MovieCharacterPersonTypeAPI typeAPI;

    public MovieCharacterPersonDelegateLookupImpl(MovieCharacterPersonTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public long getCharacterId(int ordinal) {
        return typeAPI.getCharacterId(ordinal);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        return typeAPI.getCharacterIdBoxed(ordinal);
    }

    public MovieCharacterPersonTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}