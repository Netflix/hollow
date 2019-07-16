package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RightsContractAssetDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RightsContractAssetDelegate {

    private final String bcp47Code;
    private final int bcp47CodeOrdinal;
    private final String assetType;
    private final int assetTypeOrdinal;
    private RightsContractAssetTypeAPI typeAPI;

    public RightsContractAssetDelegateCachedImpl(RightsContractAssetTypeAPI typeAPI, int ordinal) {
        this.bcp47CodeOrdinal = typeAPI.getBcp47CodeOrdinal(ordinal);
        int bcp47CodeTempOrdinal = bcp47CodeOrdinal;
        this.bcp47Code = bcp47CodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(bcp47CodeTempOrdinal);
        this.assetTypeOrdinal = typeAPI.getAssetTypeOrdinal(ordinal);
        int assetTypeTempOrdinal = assetTypeOrdinal;
        this.assetType = assetTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(assetTypeTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public String getBcp47Code(int ordinal) {
        return bcp47Code;
    }

    public boolean isBcp47CodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return bcp47Code == null;
        return testValue.equals(bcp47Code);
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        return bcp47CodeOrdinal;
    }

    public String getAssetType(int ordinal) {
        return assetType;
    }

    public boolean isAssetTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return assetType == null;
        return testValue.equals(assetType);
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return assetTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsContractAssetTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsContractAssetTypeAPI) typeAPI;
    }

}