package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class OverrideEntityTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, OverrideEntityTypeDelegate {

    private final String _name;
    private OverrideEntityTypeTypeAPI typeAPI;

    public OverrideEntityTypeDelegateCachedImpl(OverrideEntityTypeTypeAPI typeAPI, int ordinal) {
        this._name = typeAPI.get_name(ordinal);
        this.typeAPI = typeAPI;
    }

    public String get_name(int ordinal) {
        return _name;
    }

    public boolean is_nameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return _name == null;
        return testValue.equals(_name);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public OverrideEntityTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (OverrideEntityTypeTypeAPI) typeAPI;
    }

}