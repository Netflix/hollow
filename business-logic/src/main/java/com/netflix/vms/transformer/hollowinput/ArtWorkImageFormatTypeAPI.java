package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ArtWorkImageFormatTypeAPI extends HollowObjectTypeAPI {

    private final ArtWorkImageFormatDelegateLookupImpl delegateLookupImpl;

    ArtWorkImageFormatTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "format",
            "width",
            "height"
        });
        this.delegateLookupImpl = new ArtWorkImageFormatDelegateLookupImpl(this);
    }

    public int getFormatOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtWorkImageFormat", ordinal, "format");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getFormatTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getWidth(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("ArtWorkImageFormat", ordinal, "width");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getWidthBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("ArtWorkImageFormat", ordinal, "width");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getHeight(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ArtWorkImageFormat", ordinal, "height");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getHeightBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ArtWorkImageFormat", ordinal, "height");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ArtWorkImageFormatDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}