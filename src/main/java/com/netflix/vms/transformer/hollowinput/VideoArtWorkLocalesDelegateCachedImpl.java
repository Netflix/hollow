package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoArtWorkLocalesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoArtWorkLocalesDelegate {

    private final int territoryCodesOrdinal;
    private final int bcp47CodeOrdinal;
    private final int effectiveDateOrdinal;
   private VideoArtWorkLocalesTypeAPI typeAPI;

    public VideoArtWorkLocalesDelegateCachedImpl(VideoArtWorkLocalesTypeAPI typeAPI, int ordinal) {
        this.territoryCodesOrdinal = typeAPI.getTerritoryCodesOrdinal(ordinal);
        this.bcp47CodeOrdinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        this.effectiveDateOrdinal = typeAPI.getEffectiveDateOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        return territoryCodesOrdinal;
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return bcp47CodeOrdinal;
    }

    public int getEffectiveDateOrdinal(int ordinal) {
        return effectiveDateOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoArtWorkLocalesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoArtWorkLocalesTypeAPI) typeAPI;
    }

}