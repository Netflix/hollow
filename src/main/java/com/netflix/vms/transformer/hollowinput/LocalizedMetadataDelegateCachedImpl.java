package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class LocalizedMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, LocalizedMetadataDelegate {

    private final Long movieId;
    private final int attributeNameOrdinal;
    private final int labelOrdinal;
    private final int translatedTextsOrdinal;
   private LocalizedMetadataTypeAPI typeAPI;

    public LocalizedMetadataDelegateCachedImpl(LocalizedMetadataTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.attributeNameOrdinal = typeAPI.getAttributeNameOrdinal(ordinal);
        this.labelOrdinal = typeAPI.getLabelOrdinal(ordinal);
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return attributeNameOrdinal;
    }

    public int getLabelOrdinal(int ordinal) {
        return labelOrdinal;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public LocalizedMetadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (LocalizedMetadataTypeAPI) typeAPI;
    }

}