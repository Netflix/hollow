package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class IsOriginalTitleDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IsOriginalTitleDelegate {

    private final Boolean value;
    private IsOriginalTitleTypeAPI typeAPI;

    public IsOriginalTitleDelegateCachedImpl(IsOriginalTitleTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getValue(int ordinal) {
        if(value == null)
            return false;
        return value.booleanValue();
    }

    public Boolean getValueBoxed(int ordinal) {
        return value;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IsOriginalTitleTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IsOriginalTitleTypeAPI) typeAPI;
    }

}