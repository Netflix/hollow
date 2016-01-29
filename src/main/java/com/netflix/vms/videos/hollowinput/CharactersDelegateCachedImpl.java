package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CharactersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CharactersDelegate {

    private final int bOrdinal;
    private final int prefixOrdinal;
    private final Long id;
    private final int cnOrdinal;
   private CharactersTypeAPI typeAPI;

    public CharactersDelegateCachedImpl(CharactersTypeAPI typeAPI, int ordinal) {
        this.bOrdinal = typeAPI.getBOrdinal(ordinal);
        this.prefixOrdinal = typeAPI.getPrefixOrdinal(ordinal);
        this.id = typeAPI.getIdBoxed(ordinal);
        this.cnOrdinal = typeAPI.getCnOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getBOrdinal(int ordinal) {
        return bOrdinal;
    }

    public int getPrefixOrdinal(int ordinal) {
        return prefixOrdinal;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getCnOrdinal(int ordinal) {
        return cnOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CharactersTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CharactersTypeAPI) typeAPI;
    }

}