package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhaseElementsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseElementsDelegate {

    private final int localized_metadataOrdinal;
    private final int charactersOrdinal;
    private final int castOrdinal;
    private final int artwork_newOrdinal;
    private final int artworkOrdinal;
    private final int trailersOrdinal;
   private RolloutPhaseElementsTypeAPI typeAPI;

    public RolloutPhaseElementsDelegateCachedImpl(RolloutPhaseElementsTypeAPI typeAPI, int ordinal) {
        this.localized_metadataOrdinal = typeAPI.getLocalized_metadataOrdinal(ordinal);
        this.charactersOrdinal = typeAPI.getCharactersOrdinal(ordinal);
        this.castOrdinal = typeAPI.getCastOrdinal(ordinal);
        this.artwork_newOrdinal = typeAPI.getArtwork_newOrdinal(ordinal);
        this.artworkOrdinal = typeAPI.getArtworkOrdinal(ordinal);
        this.trailersOrdinal = typeAPI.getTrailersOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        return localized_metadataOrdinal;
    }

    public int getCharactersOrdinal(int ordinal) {
        return charactersOrdinal;
    }

    public int getCastOrdinal(int ordinal) {
        return castOrdinal;
    }

    public int getArtwork_newOrdinal(int ordinal) {
        return artwork_newOrdinal;
    }

    public int getArtworkOrdinal(int ordinal) {
        return artworkOrdinal;
    }

    public int getTrailersOrdinal(int ordinal) {
        return trailersOrdinal;
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