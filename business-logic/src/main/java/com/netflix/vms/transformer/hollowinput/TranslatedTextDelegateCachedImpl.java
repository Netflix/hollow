package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class TranslatedTextDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TranslatedTextDelegate {

    private final int translatedTextsOrdinal;
   private TranslatedTextTypeAPI typeAPI;

    public TranslatedTextDelegateCachedImpl(TranslatedTextTypeAPI typeAPI, int ordinal) {
        this.translatedTextsOrdinal = typeAPI.getTranslatedTextsOrdinal(ordinal);
        this.typeAPI = typeAPI;
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

    public TranslatedTextTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TranslatedTextTypeAPI) typeAPI;
    }

}