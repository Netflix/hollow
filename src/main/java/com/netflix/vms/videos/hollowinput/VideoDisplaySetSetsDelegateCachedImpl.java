package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDisplaySetSetsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDisplaySetSetsDelegate {

    private final int childrenOrdinal;
    private final int countryCodeOrdinal;
    private final int setTypeOrdinal;
   private VideoDisplaySetSetsTypeAPI typeAPI;

    public VideoDisplaySetSetsDelegateCachedImpl(VideoDisplaySetSetsTypeAPI typeAPI, int ordinal) {
        this.childrenOrdinal = typeAPI.getChildrenOrdinal(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.setTypeOrdinal = typeAPI.getSetTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getChildrenOrdinal(int ordinal) {
        return childrenOrdinal;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getSetTypeOrdinal(int ordinal) {
        return setTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDisplaySetSetsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDisplaySetSetsTypeAPI) typeAPI;
    }

}