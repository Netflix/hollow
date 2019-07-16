package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieTitleAkaTypeAPI extends HollowObjectTypeAPI {

    private final MovieTitleAkaDelegateLookupImpl delegateLookupImpl;

    public MovieTitleAkaTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "movieId",
            "alias",
            "bcpCode",
            "sourceType",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieTitleAkaDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("MovieTitleAka", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("MovieTitleAka", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getAliasOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "alias");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MovieTitleStringTypeAPI getAliasTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "bcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public BcpCodeTypeAPI getBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getSourceTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "sourceType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public TitleSourceTypeTypeAPI getSourceTypeTypeAPI() {
        return getAPI().getTitleSourceTypeTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleAka", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieTitleAkaDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}