package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class TopNAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TopNAttributesDelegate {

    private final int countryOrdinal;
    private final int viewShareOrdinal;
    private final int countryViewHrsOrdinal;
   private TopNAttributesTypeAPI typeAPI;

    public TopNAttributesDelegateCachedImpl(TopNAttributesTypeAPI typeAPI, int ordinal) {
        this.countryOrdinal = typeAPI.getCountryOrdinal(ordinal);
        this.viewShareOrdinal = typeAPI.getViewShareOrdinal(ordinal);
        this.countryViewHrsOrdinal = typeAPI.getCountryViewHrsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryOrdinal(int ordinal) {
        return countryOrdinal;
    }

    public int getViewShareOrdinal(int ordinal) {
        return viewShareOrdinal;
    }

    public int getCountryViewHrsOrdinal(int ordinal) {
        return countryViewHrsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TopNAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TopNAttributesTypeAPI) typeAPI;
    }

}