package com.netflix.hollow.diff.ui.temp.core;

import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ProfileIdDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ProfileIdDelegate {

    private final Integer value;
    private ProfileIdTypeAPI typeAPI;

    public ProfileIdDelegateCachedImpl(ProfileIdTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getValue(int ordinal) {
        if(value == null)
            return Integer.MIN_VALUE;
        return value.intValue();
    }

    public Integer getValueBoxed(int ordinal) {
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

    public ProfileIdTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ProfileIdTypeAPI) typeAPI;
    }

}