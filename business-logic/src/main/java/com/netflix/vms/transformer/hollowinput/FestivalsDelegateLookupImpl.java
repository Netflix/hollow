package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class FestivalsDelegateLookupImpl extends HollowObjectAbstractDelegate implements FestivalsDelegate {

    private final FestivalsTypeAPI typeAPI;

    public FestivalsDelegateLookupImpl(FestivalsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getFestivalId(int ordinal) {
        return typeAPI.getFestivalId(ordinal);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        return typeAPI.getFestivalIdBoxed(ordinal);
    }

    public int getCopyrightOrdinal(int ordinal) {
        return typeAPI.getCopyrightOrdinal(ordinal);
    }

    public int getFestivalNameOrdinal(int ordinal) {
        return typeAPI.getFestivalNameOrdinal(ordinal);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public int getShortNameOrdinal(int ordinal) {
        return typeAPI.getShortNameOrdinal(ordinal);
    }

    public int getSingularNameOrdinal(int ordinal) {
        return typeAPI.getSingularNameOrdinal(ordinal);
    }

    public FestivalsTypeAPI getTypeAPI() {
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