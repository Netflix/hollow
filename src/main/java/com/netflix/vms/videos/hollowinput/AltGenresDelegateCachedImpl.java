package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AltGenresDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AltGenresDelegate {

    private final int alternateNamesOrdinal;
    private final int displayNameOrdinal;
    private final Long altGenreId;
    private final int shortNameOrdinal;
   private AltGenresTypeAPI typeAPI;

    public AltGenresDelegateCachedImpl(AltGenresTypeAPI typeAPI, int ordinal) {
        this.alternateNamesOrdinal = typeAPI.getAlternateNamesOrdinal(ordinal);
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.altGenreId = typeAPI.getAltGenreIdBoxed(ordinal);
        this.shortNameOrdinal = typeAPI.getShortNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getAlternateNamesOrdinal(int ordinal) {
        return alternateNamesOrdinal;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    public long getAltGenreId(int ordinal) {
        return altGenreId.longValue();
    }

    public Long getAltGenreIdBoxed(int ordinal) {
        return altGenreId;
    }

    public int getShortNameOrdinal(int ordinal) {
        return shortNameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AltGenresTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AltGenresTypeAPI) typeAPI;
    }

}