package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhaseElementsTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseElementsDelegateLookupImpl delegateLookupImpl;

    RolloutPhaseElementsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "localized_metadata",
            "characters",
            "cast",
            "artwork_new",
            "artwork",
            "trailers"
        });
        this.delegateLookupImpl = new RolloutPhaseElementsDelegateLookupImpl(this);
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "localized_metadata");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getLocalized_metadataTypeAPI() {
        return getAPI().getRolloutPhaseLocalizedMetadataTypeAPI();
    }

    public int getCharactersOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "characters");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutPhaseCharacterListTypeAPI getCharactersTypeAPI() {
        return getAPI().getRolloutPhaseCharacterListTypeAPI();
    }

    public int getCastOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "cast");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RolloutPhaseCastListTypeAPI getCastTypeAPI() {
        return getAPI().getRolloutPhaseCastListTypeAPI();
    }

    public int getArtwork_newOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "artwork_new");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public RolloutPhaseNewArtworkTypeAPI getArtwork_newTypeAPI() {
        return getAPI().getRolloutPhaseNewArtworkTypeAPI();
    }

    public int getArtworkOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "artwork");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutPhaseOldArtworkListTypeAPI getArtworkTypeAPI() {
        return getAPI().getRolloutPhaseOldArtworkListTypeAPI();
    }

    public int getTrailersOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhaseElements", ordinal, "trailers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public RolloutPhaseTrailerListTypeAPI getTrailersTypeAPI() {
        return getAPI().getRolloutPhaseTrailerListTypeAPI();
    }

    public RolloutPhaseElementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}