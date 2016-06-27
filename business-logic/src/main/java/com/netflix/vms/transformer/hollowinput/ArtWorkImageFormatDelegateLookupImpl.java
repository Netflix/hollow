package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ArtWorkImageFormatDelegateLookupImpl extends HollowObjectAbstractDelegate implements ArtWorkImageFormatDelegate {

    private final ArtWorkImageFormatTypeAPI typeAPI;

    public ArtWorkImageFormatDelegateLookupImpl(ArtWorkImageFormatTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getFormatOrdinal(int ordinal) {
        return typeAPI.getFormatOrdinal(ordinal);
    }

    public long getWidth(int ordinal) {
        return typeAPI.getWidth(ordinal);
    }

    public Long getWidthBoxed(int ordinal) {
        return typeAPI.getWidthBoxed(ordinal);
    }

    public long getHeight(int ordinal) {
        return typeAPI.getHeight(ordinal);
    }

    public Long getHeightBoxed(int ordinal) {
        return typeAPI.getHeightBoxed(ordinal);
    }

    public ArtWorkImageFormatTypeAPI getTypeAPI() {
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