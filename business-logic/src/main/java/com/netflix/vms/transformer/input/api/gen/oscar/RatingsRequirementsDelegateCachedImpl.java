package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RatingsRequirementsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RatingsRequirementsDelegate {

    private final String _name;
    private RatingsRequirementsTypeAPI typeAPI;

    public RatingsRequirementsDelegateCachedImpl(RatingsRequirementsTypeAPI typeAPI, int ordinal) {
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

    public RatingsRequirementsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RatingsRequirementsTypeAPI) typeAPI;
    }

}