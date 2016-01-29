package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class Bcp47CodeDelegateLookupImpl extends HollowObjectAbstractDelegate implements Bcp47CodeDelegate {

    private final Bcp47CodeTypeAPI typeAPI;

    public Bcp47CodeDelegateLookupImpl(Bcp47CodeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getIso6392CodeOrdinal(int ordinal) {
        return typeAPI.getIso6392CodeOrdinal(ordinal);
    }

    public long getLanguageId(int ordinal) {
        return typeAPI.getLanguageId(ordinal);
    }

    public Long getLanguageIdBoxed(int ordinal) {
        return typeAPI.getLanguageIdBoxed(ordinal);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return typeAPI.getBcp47CodeOrdinal(ordinal);
    }

    public int getIso6391CodeOrdinal(int ordinal) {
        return typeAPI.getIso6391CodeOrdinal(ordinal);
    }

    public int getIso6393CodeOrdinal(int ordinal) {
        return typeAPI.getIso6393CodeOrdinal(ordinal);
    }

    public Bcp47CodeTypeAPI getTypeAPI() {
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