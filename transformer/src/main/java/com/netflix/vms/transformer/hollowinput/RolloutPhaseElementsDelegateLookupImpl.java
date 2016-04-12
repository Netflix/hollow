package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhaseElementsDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseElementsDelegate {

    private final RolloutPhaseElementsTypeAPI typeAPI;

    public RolloutPhaseElementsDelegateLookupImpl(RolloutPhaseElementsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        return typeAPI.getLocalized_metadataOrdinal(ordinal);
    }

    public int getCharactersOrdinal(int ordinal) {
        return typeAPI.getCharactersOrdinal(ordinal);
    }

    public int getCastOrdinal(int ordinal) {
        return typeAPI.getCastOrdinal(ordinal);
    }

    public int getArtwork_newOrdinal(int ordinal) {
        return typeAPI.getArtwork_newOrdinal(ordinal);
    }

    public int getArtworkOrdinal(int ordinal) {
        return typeAPI.getArtworkOrdinal(ordinal);
    }

    public int getTrailersOrdinal(int ordinal) {
        return typeAPI.getTrailersOrdinal(ordinal);
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