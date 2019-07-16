package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class DisplaySetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, DisplaySetDelegate {

    private final Long setId;
    private final int countryCodesOrdinal;
    private final Boolean isDefault;
    private final int displaySetTypesOrdinal;
    private final int containersOrdinal;
    private final int createdOrdinal;
    private final int updatedOrdinal;
    private DisplaySetTypeAPI typeAPI;

    public DisplaySetDelegateCachedImpl(DisplaySetTypeAPI typeAPI, int ordinal) {
        this.setId = typeAPI.getSetIdBoxed(ordinal);
        this.countryCodesOrdinal = typeAPI.getCountryCodesOrdinal(ordinal);
        this.isDefault = typeAPI.getIsDefaultBoxed(ordinal);
        this.displaySetTypesOrdinal = typeAPI.getDisplaySetTypesOrdinal(ordinal);
        this.containersOrdinal = typeAPI.getContainersOrdinal(ordinal);
        this.createdOrdinal = typeAPI.getCreatedOrdinal(ordinal);
        this.updatedOrdinal = typeAPI.getUpdatedOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSetId(int ordinal) {
        if(setId == null)
            return Long.MIN_VALUE;
        return setId.longValue();
    }

    public Long getSetIdBoxed(int ordinal) {
        return setId;
    }

    public int getCountryCodesOrdinal(int ordinal) {
        return countryCodesOrdinal;
    }

    public boolean getIsDefault(int ordinal) {
        if(isDefault == null)
            return false;
        return isDefault.booleanValue();
    }

    public Boolean getIsDefaultBoxed(int ordinal) {
        return isDefault;
    }

    public int getDisplaySetTypesOrdinal(int ordinal) {
        return displaySetTypesOrdinal;
    }

    public int getContainersOrdinal(int ordinal) {
        return containersOrdinal;
    }

    public int getCreatedOrdinal(int ordinal) {
        return createdOrdinal;
    }

    public int getUpdatedOrdinal(int ordinal) {
        return updatedOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public DisplaySetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (DisplaySetTypeAPI) typeAPI;
    }

}