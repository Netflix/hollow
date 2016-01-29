package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkDerivativesDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterArtworkDerivativesDelegate {

    private final CharacterArtworkDerivativesTypeAPI typeAPI;

    public CharacterArtworkDerivativesDelegateLookupImpl(CharacterArtworkDerivativesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getRecipeNameOrdinal(int ordinal) {
        return typeAPI.getRecipeNameOrdinal(ordinal);
    }

    public int getFileNameOrdinal(int ordinal) {
        return typeAPI.getFileNameOrdinal(ordinal);
    }

    public long getImageId(int ordinal) {
        return typeAPI.getImageId(ordinal);
    }

    public Long getImageIdBoxed(int ordinal) {
        return typeAPI.getImageIdBoxed(ordinal);
    }

    public int getCdnOriginServerIdOrdinal(int ordinal) {
        return typeAPI.getCdnOriginServerIdOrdinal(ordinal);
    }

    public long getWidth(int ordinal) {
        return typeAPI.getWidth(ordinal);
    }

    public Long getWidthBoxed(int ordinal) {
        return typeAPI.getWidthBoxed(ordinal);
    }

    public int getCdnDirectoryOrdinal(int ordinal) {
        return typeAPI.getCdnDirectoryOrdinal(ordinal);
    }

    public int getImageTypeOrdinal(int ordinal) {
        return typeAPI.getImageTypeOrdinal(ordinal);
    }

    public int getCdnOriginServerOrdinal(int ordinal) {
        return typeAPI.getCdnOriginServerOrdinal(ordinal);
    }

    public long getHeight(int ordinal) {
        return typeAPI.getHeight(ordinal);
    }

    public Long getHeightBoxed(int ordinal) {
        return typeAPI.getHeightBoxed(ordinal);
    }

    public CharacterArtworkDerivativesTypeAPI getTypeAPI() {
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