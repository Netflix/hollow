package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class CodecPrivateDataStringDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CodecPrivateDataStringDelegate {

    private final String value;
   private CodecPrivateDataStringTypeAPI typeAPI;

    public CodecPrivateDataStringDelegateCachedImpl(CodecPrivateDataStringTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValue(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getValue(int ordinal) {
        return value;
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return value == null;
        return testValue.equals(value);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CodecPrivateDataStringTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CodecPrivateDataStringTypeAPI) typeAPI;
    }

}