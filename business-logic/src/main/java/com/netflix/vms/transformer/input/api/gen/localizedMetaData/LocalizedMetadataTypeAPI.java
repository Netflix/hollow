package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class LocalizedMetadataTypeAPI extends HollowObjectTypeAPI {

    private final LocalizedMetadataDelegateLookupImpl delegateLookupImpl;

    public LocalizedMetadataTypeAPI(LocalizedMetaDataAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "attributeName",
            "label",
            "translatedTexts"
        });
        this.delegateLookupImpl = new LocalizedMetadataDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("LocalizedMetadata", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("LocalizedMetadata", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAttributeNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedMetadata", ordinal, "attributeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getAttributeNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLabelOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedMetadata", ordinal, "label");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getLabelTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("LocalizedMetadata", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public MapOfTranslatedTextTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMapOfTranslatedTextTypeAPI();
    }

    public LocalizedMetadataDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public LocalizedMetaDataAPI getAPI() {
        return (LocalizedMetaDataAPI) api;
    }

}