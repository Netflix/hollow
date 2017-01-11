package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class LanguagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, LanguagesDelegate {

    private final Long languageId;
    private final int nameOrdinal;
   private LanguagesTypeAPI typeAPI;

    public LanguagesDelegateCachedImpl(LanguagesTypeAPI typeAPI, int ordinal) {
        this.languageId = typeAPI.getLanguageIdBoxed(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getLanguageId(int ordinal) {
        return languageId.longValue();
    }

    public Long getLanguageIdBoxed(int ordinal) {
        return languageId;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public LanguagesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (LanguagesTypeAPI) typeAPI;
    }

}