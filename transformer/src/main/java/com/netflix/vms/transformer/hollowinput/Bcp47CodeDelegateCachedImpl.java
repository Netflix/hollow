package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class Bcp47CodeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, Bcp47CodeDelegate {

    private final Long languageId;
    private final int iso6392CodeOrdinal;
    private final int bcp47CodeOrdinal;
    private final int iso6391CodeOrdinal;
    private final int iso6393CodeOrdinal;
   private Bcp47CodeTypeAPI typeAPI;

    public Bcp47CodeDelegateCachedImpl(Bcp47CodeTypeAPI typeAPI, int ordinal) {
        this.languageId = typeAPI.getLanguageIdBoxed(ordinal);
        this.iso6392CodeOrdinal = typeAPI.getIso6392CodeOrdinal(ordinal);
        this.bcp47CodeOrdinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        this.iso6391CodeOrdinal = typeAPI.getIso6391CodeOrdinal(ordinal);
        this.iso6393CodeOrdinal = typeAPI.getIso6393CodeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getLanguageId(int ordinal) {
        return languageId.longValue();
    }

    public Long getLanguageIdBoxed(int ordinal) {
        return languageId;
    }

    public int getIso6392CodeOrdinal(int ordinal) {
        return iso6392CodeOrdinal;
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return bcp47CodeOrdinal;
    }

    public int getIso6391CodeOrdinal(int ordinal) {
        return iso6391CodeOrdinal;
    }

    public int getIso6393CodeOrdinal(int ordinal) {
        return iso6393CodeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public Bcp47CodeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (Bcp47CodeTypeAPI) typeAPI;
    }

}