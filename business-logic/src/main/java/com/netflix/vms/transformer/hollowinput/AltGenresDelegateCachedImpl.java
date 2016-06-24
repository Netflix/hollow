package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class AltGenresDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AltGenresDelegate {

    private final Long altGenreId;
    private final int displayNameOrdinal;
    private final int shortNameOrdinal;
    private final int alternateNamesOrdinal;
   private AltGenresTypeAPI typeAPI;

    public AltGenresDelegateCachedImpl(AltGenresTypeAPI typeAPI, int ordinal) {
        this.altGenreId = typeAPI.getAltGenreIdBoxed(ordinal);
        this.displayNameOrdinal = typeAPI.getDisplayNameOrdinal(ordinal);
        this.shortNameOrdinal = typeAPI.getShortNameOrdinal(ordinal);
        this.alternateNamesOrdinal = typeAPI.getAlternateNamesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAltGenreId(int ordinal) {
        return altGenreId.longValue();
    }

    public Long getAltGenreIdBoxed(int ordinal) {
        return altGenreId;
    }

    public int getDisplayNameOrdinal(int ordinal) {
        return displayNameOrdinal;
    }

    public int getShortNameOrdinal(int ordinal) {
        return shortNameOrdinal;
    }

    public int getAlternateNamesOrdinal(int ordinal) {
        return alternateNamesOrdinal;
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