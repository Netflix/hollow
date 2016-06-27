package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class StorageGroupsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StorageGroupsDelegate {

    private final int idOrdinal;
    private final Long cdnId;
    private final int countriesOrdinal;
   private StorageGroupsTypeAPI typeAPI;

    public StorageGroupsDelegateCachedImpl(StorageGroupsTypeAPI typeAPI, int ordinal) {
        this.idOrdinal = typeAPI.getIdOrdinal(ordinal);
        this.cdnId = typeAPI.getCdnIdBoxed(ordinal);
        this.countriesOrdinal = typeAPI.getCountriesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getIdOrdinal(int ordinal) {
        return idOrdinal;
    }

    public long getCdnId(int ordinal) {
        return cdnId.longValue();
    }

    public Long getCdnIdBoxed(int ordinal) {
        return cdnId;
    }

    public int getCountriesOrdinal(int ordinal) {
        return countriesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StorageGroupsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StorageGroupsTypeAPI) typeAPI;
    }

}