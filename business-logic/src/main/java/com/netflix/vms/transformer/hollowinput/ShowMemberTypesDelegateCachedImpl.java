package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ShowMemberTypesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ShowMemberTypesDelegate {

    private final Long showMemberTypeId;
    private final int displayNameOrdinal;
   private ShowMemberTypesTypeAPI typeAPI;

    public ShowMemberTypesDelegateCachedImpl(ShowMemberTypesTypeAPI typeAPI, int ordinal) {
        this.showMemberTypeId = typeAPI.getShowMemberTypeIdBoxed(ordinal);
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getShowMemberTypeId(int ordinal) {
        return showMemberTypeId.longValue();
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        return showMemberTypeId;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ShowMemberTypesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ShowMemberTypesTypeAPI) typeAPI;
    }

}