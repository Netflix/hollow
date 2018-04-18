package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonAliasesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonAliasesDelegate {

    private final Long aliasId;
    private final int nameOrdinal;
    private PersonAliasesTypeAPI typeAPI;

    public PersonAliasesDelegateCachedImpl(PersonAliasesTypeAPI typeAPI, int ordinal) {
        this.aliasId = typeAPI.getAliasIdBoxed(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAliasId(int ordinal) {
        if(aliasId == null)
            return Long.MIN_VALUE;
        return aliasId.longValue();
    }

    public Long getAliasIdBoxed(int ordinal) {
        return aliasId;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonAliasesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonAliasesTypeAPI) typeAPI;
    }

}