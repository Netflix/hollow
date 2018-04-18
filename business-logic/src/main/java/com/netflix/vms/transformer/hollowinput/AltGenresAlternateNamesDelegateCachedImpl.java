package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AltGenresAlternateNamesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AltGenresAlternateNamesDelegate {

    private final Long typeId;
    private final int typeOrdinal;
    private final int translatedTextsOrdinal;
    private AltGenresAlternateNamesTypeAPI typeAPI;

    public AltGenresAlternateNamesDelegateCachedImpl(AltGenresAlternateNamesTypeAPI typeAPI, int ordinal) {
        this.typeId = typeAPI.getTypeIdBoxed(ordinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getTypeId(int ordinal) {
        if(typeId == null)
            return Long.MIN_VALUE;
        return typeId.longValue();
    }

    public Long getTypeIdBoxed(int ordinal) {
        return typeId;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
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

    public AltGenresAlternateNamesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AltGenresAlternateNamesTypeAPI) typeAPI;
    }

}