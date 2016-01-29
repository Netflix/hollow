package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class PersonArtworkLocalesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonArtworkLocalesDelegate {

    private final int territoryCodesOrdinal;
    private final int bcp47CodeOrdinal;
    private final Long effectiveDate;
   private PersonArtworkLocalesTypeAPI typeAPI;

    public PersonArtworkLocalesDelegateCachedImpl(PersonArtworkLocalesTypeAPI typeAPI, int ordinal) {
        this.territoryCodesOrdinal = typeAPI.getTerritoryCodesOrdinal(ordinal);
        this.bcp47CodeOrdinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        this.effectiveDate = typeAPI.getEffectiveDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTerritoryCodesOrdinal(int ordinal) {
        return territoryCodesOrdinal;
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return bcp47CodeOrdinal;
    }

    public long getEffectiveDate(int ordinal) {
        return effectiveDate.longValue();
    }

    public Long getEffectiveDateBoxed(int ordinal) {
        return effectiveDate;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonArtworkLocalesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonArtworkLocalesTypeAPI) typeAPI;
    }

}