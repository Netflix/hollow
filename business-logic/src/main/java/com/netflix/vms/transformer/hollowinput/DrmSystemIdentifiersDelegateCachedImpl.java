package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmSystemIdentifiersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DrmSystemIdentifiersDelegate {

    private final Long id;
    private final int guidOrdinal;
    private final int nameOrdinal;
    private final Boolean headerDataAvailable;
   private DrmSystemIdentifiersTypeAPI typeAPI;

    public DrmSystemIdentifiersDelegateCachedImpl(DrmSystemIdentifiersTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.guidOrdinal = typeAPI.getGuidOrdinal(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.headerDataAvailable = typeAPI.getHeaderDataAvailableBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public int getGuidOrdinal(int ordinal) {
        return guidOrdinal;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public boolean getHeaderDataAvailable(int ordinal) {
        return headerDataAvailable.booleanValue();
    }

    public Boolean getHeaderDataAvailableBoxed(int ordinal) {
        return headerDataAvailable;
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