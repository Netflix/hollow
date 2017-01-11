package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ArtworkAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ArtworkAttributesDelegate {

    private final int passthroughOrdinal;
    private final String ROLLOUT_EXCLUSIVE;
   private ArtworkAttributesTypeAPI typeAPI;

    public ArtworkAttributesDelegateCachedImpl(ArtworkAttributesTypeAPI typeAPI, int ordinal) {
        this.passthroughOrdinal = typeAPI.getPassthroughOrdinal(ordinal);
        this.ROLLOUT_EXCLUSIVE = typeAPI.getROLLOUT_EXCLUSIVE(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getPassthroughOrdinal(int ordinal) {
        return passthroughOrdinal;
    }

    public String getROLLOUT_EXCLUSIVE(int ordinal) {
        return ROLLOUT_EXCLUSIVE;
    }

    public boolean isROLLOUT_EXCLUSIVEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return ROLLOUT_EXCLUSIVE == null;
        return testValue.equals(ROLLOUT_EXCLUSIVE);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ArtworkAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ArtworkAttributesTypeAPI) typeAPI;
    }

}