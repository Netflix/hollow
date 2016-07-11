package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class TopNAttributeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TopNAttributeDelegate {

    private final int countryOrdinal;
    private final int viewShareOrdinal;
    private final int countryViewHrsOrdinal;
   private TopNAttributeTypeAPI typeAPI;

    public TopNAttributeDelegateCachedImpl(TopNAttributeTypeAPI typeAPI, int ordinal) {
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

    public TopNAttributeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TopNAttributeTypeAPI) typeAPI;
    }

}