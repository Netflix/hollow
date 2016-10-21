package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PersonBioDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonBioDelegate {

    private final int spousesOrdinal;
    private final int partnersOrdinal;
    private final int relationshipsOrdinal;
    private final int currentRelationshipOrdinal;
    private final Long personId;
    private final int birthDateOrdinal;
    private final int deathDateOrdinal;
    private final int movieIdsOrdinal;
   private PersonBioTypeAPI typeAPI;

    public PersonBioDelegateCachedImpl(PersonBioTypeAPI typeAPI, int ordinal) {
        this.spousesOrdinal = typeAPI.getSpousesOrdinal(ordinal);
        this.partnersOrdinal = typeAPI.getPartnersOrdinal(ordinal);
        this.relationshipsOrdinal = typeAPI.getRelationshipsOrdinal(ordinal);
        this.currentRelationshipOrdinal = typeAPI.getCurrentRelationshipOrdinal(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.birthDateOrdinal = typeAPI.getBirthDateOrdinal(ordinal);
        this.deathDateOrdinal = typeAPI.getDeathDateOrdinal(ordinal);
        this.movieIdsOrdinal = typeAPI.getMovieIdsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSpousesOrdinal(int ordinal) {
        return spousesOrdinal;
    }

    public int getPartnersOrdinal(int ordinal) {
        return partnersOrdinal;
    }

    public int getRelationshipsOrdinal(int ordinal) {
        return relationshipsOrdinal;
    }

    public int getCurrentRelationshipOrdinal(int ordinal) {
        return currentRelationshipOrdinal;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public int getBirthDateOrdinal(int ordinal) {
        return birthDateOrdinal;
    }

    public int getDeathDateOrdinal(int ordinal) {
        return deathDateOrdinal;
    }

    public int getMovieIdsOrdinal(int ordinal) {
        return movieIdsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonBioTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonBioTypeAPI) typeAPI;
    }

}