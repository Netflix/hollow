package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class LocalizedMetadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements LocalizedMetadataDelegate {

    private final LocalizedMetadataTypeAPI typeAPI;

    public LocalizedMetadataDelegateLookupImpl(LocalizedMetadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return typeAPI.getAttributeNameOrdinal(ordinal);
    }

    public int getLabelOrdinal(int ordinal) {
        return typeAPI.getLabelOrdinal(ordinal);
    }

    public LocalizedMetadataTypeAPI getTypeAPI() {
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