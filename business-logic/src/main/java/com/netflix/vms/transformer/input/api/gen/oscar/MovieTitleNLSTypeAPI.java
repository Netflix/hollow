package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieTitleNLSTypeAPI extends HollowObjectTypeAPI {

    private final MovieTitleNLSDelegateLookupImpl delegateLookupImpl;

    public MovieTitleNLSTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "type",
            "titleText",
            "merchBcpCode",
            "titleBcpCode",
            "sourceType",
            "isOriginalTitle",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieTitleNLSDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieTitleTypeTypeAPI getTypeTypeAPI() {
        return getAPI().getMovieTitleTypeTypeAPI();
    }

    public int getTitleTextOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "titleText");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MovieTitleStringTypeAPI getTitleTextTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getMerchBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "merchBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public BcpCodeTypeAPI getMerchBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getTitleBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "titleBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public BcpCodeTypeAPI getTitleBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getSourceTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "sourceType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public TitleSourceTypeTypeAPI getSourceTypeTypeAPI() {
        return getAPI().getTitleSourceTypeTypeAPI();
    }

    public int getIsOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "isOriginalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public IsOriginalTitleTypeAPI getIsOriginalTitleTypeAPI() {
        return getAPI().getIsOriginalTitleTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieTitleNLS", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieTitleNLSDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}