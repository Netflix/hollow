package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseElementsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseElementsDelegate {

    private final RolloutPhaseElementsTypeAPI typeAPI;

    public RolloutPhaseElementsDelegateLookupImpl(RolloutPhaseElementsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        return typeAPI.getLocalized_metadataOrdinal(ordinal);
    }

    public int getArtworkOrdinal(int ordinal) {
        return typeAPI.getArtworkOrdinal(ordinal);
    }

    public RolloutPhaseElementsTypeAPI getTypeAPI() {
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