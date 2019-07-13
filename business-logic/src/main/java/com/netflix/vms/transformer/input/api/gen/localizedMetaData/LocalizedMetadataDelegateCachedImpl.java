package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LocalizedMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, LocalizedMetadataDelegate {

    private final Long movieId;
    private final String attributeName;
    private final int attributeNameOrdinal;
    private final String label;
    private final int labelOrdinal;
    private final int translatedTextsOrdinal;
    private LocalizedMetadataTypeAPI typeAPI;

    public LocalizedMetadataDelegateCachedImpl(LocalizedMetadataTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.attributeNameOrdinal = typeAPI.getAttributeNameOrdinal(ordinal);
        int attributeNameTempOrdinal = attributeNameOrdinal;
        this.attributeName = attributeNameTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(attributeNameTempOrdinal);
        this.labelOrdinal = typeAPI.getLabelOrdinal(ordinal);
        int labelTempOrdinal = labelOrdinal;
        this.label = labelTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(labelTempOrdinal);
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public String getAttributeName(int ordinal) {
        return attributeName;
    }

    public boolean isAttributeNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return attributeName == null;
        return testValue.equals(attributeName);
    }

    public int getAttributeNameOrdinal(int ordinal) {
        return attributeNameOrdinal;
    }

    public String getLabel(int ordinal) {
        return label;
    }

    public boolean isLabelEqual(int ordinal, String testValue) {
        if(testValue == null)
            return label == null;
        return testValue.equals(label);
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