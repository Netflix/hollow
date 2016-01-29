package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RolloutPhasesElementsTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhasesElementsDelegateLookupImpl delegateLookupImpl;

    RolloutPhasesElementsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "localized_metadata",
            "characters",
            "cast",
            "artwork_new",
            "artwork",
            "trailers"
        });
        this.delegateLookupImpl = new RolloutPhasesElementsDelegateLookupImpl(this);
    }

    public int getLocalized_metadataOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "localized_metadata");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public RolloutPhasesElementsLocalized_metadataTypeAPI getLocalized_metadataTypeAPI() {
        return getAPI().getRolloutPhasesElementsLocalized_metadataTypeAPI();
    }

    public int getCharactersOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "characters");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public RolloutPhasesElementsArrayOfCharactersTypeAPI getCharactersTypeAPI() {
        return getAPI().getRolloutPhasesElementsArrayOfCharactersTypeAPI();
    }

    public int getCastOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "cast");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RolloutPhasesElementsArrayOfCastTypeAPI getCastTypeAPI() {
        return getAPI().getRolloutPhasesElementsArrayOfCastTypeAPI();
    }

    public int getArtwork_newOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "artwork_new");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public RolloutPhasesElementsArtwork_newTypeAPI getArtwork_newTypeAPI() {
        return getAPI().getRolloutPhasesElementsArtwork_newTypeAPI();
    }

    public int getArtworkOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "artwork");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutPhasesElementsArrayOfArtworkTypeAPI getArtworkTypeAPI() {
        return getAPI().getRolloutPhasesElementsArrayOfArtworkTypeAPI();
    }

    public int getTrailersOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhasesElements", ordinal, "trailers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public RolloutPhasesElementsArrayOfTrailersTypeAPI getTrailersTypeAPI() {
        return getAPI().getRolloutPhasesElementsArrayOfTrailersTypeAPI();
    }

    public RolloutPhasesElementsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}