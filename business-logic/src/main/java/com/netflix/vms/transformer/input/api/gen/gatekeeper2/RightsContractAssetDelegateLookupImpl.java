package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractAssetDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsContractAssetDelegate {

    private final RightsContractAssetTypeAPI typeAPI;

    public RightsContractAssetDelegateLookupImpl(RightsContractAssetTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getBcp47Code(int ordinal) {
        ordinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isBcp47CodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return typeAPI.getBcp47CodeOrdinal(ordinal);
    }

    public String getAssetType(int ordinal) {
        ordinal = typeAPI.getAssetTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isAssetTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getAssetTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return typeAPI.getAssetTypeOrdinal(ordinal);
    }

    public RightsContractAssetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}