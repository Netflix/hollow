package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class VideoDateWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDateWindowDelegate {

    private final int countryCodeOrdinal;
    private final int releaseDatesOrdinal;
   private VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateCachedImpl(VideoDateWindowTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.releaseDatesOrdinal = typeAPI.getReleaseDatesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getReleaseDatesOrdinal(int ordinal) {
        return releaseDatesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDateWindowTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDateWindowTypeAPI) typeAPI;
    }

}