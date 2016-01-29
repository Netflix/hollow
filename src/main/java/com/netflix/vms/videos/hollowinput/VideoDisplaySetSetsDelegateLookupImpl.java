package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetSetsDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDisplaySetSetsDelegate {

    private final VideoDisplaySetSetsTypeAPI typeAPI;

    public VideoDisplaySetSetsDelegateLookupImpl(VideoDisplaySetSetsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getChildrenOrdinal(int ordinal) {
        return typeAPI.getChildrenOrdinal(ordinal);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getSetTypeOrdinal(int ordinal) {
        return typeAPI.getSetTypeOrdinal(ordinal);
    }

    public VideoDisplaySetSetsTypeAPI getTypeAPI() {
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