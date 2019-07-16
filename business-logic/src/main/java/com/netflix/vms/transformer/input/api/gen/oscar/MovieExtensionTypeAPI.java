package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieExtensionTypeAPI extends HollowObjectTypeAPI {

    private final MovieExtensionDelegateLookupImpl delegateLookupImpl;

    public MovieExtensionTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieExtensionId",
            "movieId",
            "attributeName",
            "attributeValue",
            "overrides",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieExtensionDelegateLookupImpl(this);
    }

    public long getMovieExtensionId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("MovieExtension", ordinal, "movieExtensionId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieExtensionIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("MovieExtension", ordinal, "movieExtensionId");
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
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getAttributeNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "attributeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public AttributeNameTypeAPI getAttributeNameTypeAPI() {
        return getAPI().getAttributeNameTypeAPI();
    }

    public int getAttributeValueOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "attributeValue");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public AttributeValueTypeAPI getAttributeValueTypeAPI() {
        return getAPI().getAttributeValueTypeAPI();
    }

    public int getOverridesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "overrides");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public SetOfMovieExtensionOverrideTypeAPI getOverridesTypeAPI() {
        return getAPI().getSetOfMovieExtensionOverrideTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("MovieExtension", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieExtensionDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}