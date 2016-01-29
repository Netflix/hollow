package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class ArtWorkImageFormatDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ArtWorkImageFormatDelegate {

    private final int formatOrdinal;
    private final Long width;
    private final Long height;
   private ArtWorkImageFormatTypeAPI typeAPI;

    public ArtWorkImageFormatDelegateCachedImpl(ArtWorkImageFormatTypeAPI typeAPI, int ordinal) {
        this.formatOrdinal = typeAPI.getFormatOrdinal(ordinal);
        this.width = typeAPI.getWidthBoxed(ordinal);
        this.height = typeAPI.getHeightBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getFormatOrdinal(int ordinal) {
        return formatOrdinal;
    }

    public long getWidth(int ordinal) {
        return width.longValue();
    }

    public Long getWidthBoxed(int ordinal) {
        return width;
    }

    public long getHeight(int ordinal) {
        return height.longValue();
    }

    public Long getHeightBoxed(int ordinal) {
        return height;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ArtWorkImageFormatTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ArtWorkImageFormatTypeAPI) typeAPI;
    }

}