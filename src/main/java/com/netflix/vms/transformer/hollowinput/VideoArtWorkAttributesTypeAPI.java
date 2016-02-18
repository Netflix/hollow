package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoArtWorkAttributesTypeAPI extends HollowObjectTypeAPI {

    private final VideoArtWorkAttributesDelegateLookupImpl delegateLookupImpl;

    VideoArtWorkAttributesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "name",
            "value"
        });
        this.delegateLookupImpl = new VideoArtWorkAttributesDelegateLookupImpl(this);
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkAttributes", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getValueOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWorkAttributes", ordinal, "value");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getValueTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public VideoArtWorkAttributesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}