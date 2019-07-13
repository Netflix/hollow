package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseElementsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseElementsDelegate {

    private final int localized_metadataOrdinal;
    private final int artworkOrdinal;
    private RolloutPhaseElementsTypeAPI typeAPI;

    public RolloutPhaseElementsDelegateCachedImpl(RolloutPhaseElementsTypeAPI typeAPI, int ordinal) {
        this.localized_metadataOrdinal = typeAPI.getLocalized_metadataOrdinal(ordinal);
        this.artworkOrdinal = typeAPI.getArtworkOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        return localized_metadataOrdinal;
    }

    public int getArtworkOrdinal(int ordinal) {
        return artworkOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhaseElementsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseElementsTypeAPI) typeAPI;
    }

}