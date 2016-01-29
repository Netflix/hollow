package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AltGenresAlternateNamesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AltGenresAlternateNamesDelegate {

    private final int translatedTextsOrdinal;
    private final Long typeId;
    private final int typeOrdinal;
   private AltGenresAlternateNamesTypeAPI typeAPI;

    public AltGenresAlternateNamesDelegateCachedImpl(AltGenresAlternateNamesTypeAPI typeAPI, int ordinal) {
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeId = typeAPI.getTypeIdBoxed(ordinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return translatedTextsOrdinal;
    }

    public long getTypeId(int ordinal) {
        return typeId.longValue();
    }

    public Long getTypeIdBoxed(int ordinal) {
        return typeId;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AltGenresAlternateNamesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AltGenresAlternateNamesTypeAPI) typeAPI;
    }

}