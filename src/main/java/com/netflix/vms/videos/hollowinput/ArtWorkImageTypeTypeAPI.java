package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ArtWorkImageTypeTypeAPI extends HollowObjectTypeAPI {

    private final ArtWorkImageTypeDelegateLookupImpl delegateLookupImpl;

    ArtWorkImageTypeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "extension",
            "recipe",
            "imageType"
        });
        this.delegateLookupImpl = new ArtWorkImageTypeDelegateLookupImpl(this);
    }

    public int getExtensionOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtWorkImageType", ordinal, "extension");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getExtensionTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRecipeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtWorkImageType", ordinal, "recipe");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getRecipeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtWorkImageType", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public ArtWorkImageTypeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}