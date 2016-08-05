package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ContractIdDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ContractIdDelegate {

    private final Long value;
   private ContractIdTypeAPI typeAPI;

    public ContractIdDelegateCachedImpl(ContractIdTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValueBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getValue(int ordinal) {
        return value.longValue();
    }

    public Long getValueBoxed(int ordinal) {
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

    public ContractIdTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ContractIdTypeAPI) typeAPI;
    }

}