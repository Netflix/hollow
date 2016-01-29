package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class DrmSystemIdentifiersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DrmSystemIdentifiersDelegate {

    private final int nameOrdinal;
    private final int guidOrdinal;
    private final Boolean headerDataAvailable;
    private final Long id;
   private DrmSystemIdentifiersTypeAPI typeAPI;

    public DrmSystemIdentifiersDelegateCachedImpl(DrmSystemIdentifiersTypeAPI typeAPI, int ordinal) {
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.guidOrdinal = typeAPI.getGuidOrdinal(ordinal);
        this.headerDataAvailable = typeAPI.getHeaderDataAvailableBoxed(ordinal);
        this.id = typeAPI.getIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getGuidOrdinal(int ordinal) {
        return guidOrdinal;
    }

    public boolean getHeaderDataAvailable(int ordinal) {
        return headerDataAvailable.booleanValue();
    }

    public Boolean getHeaderDataAvailableBoxed(int ordinal) {
        return headerDataAvailable;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DrmSystemIdentifiersTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DrmSystemIdentifiersTypeAPI) typeAPI;
    }

}