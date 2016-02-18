package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonArtworkDerivativeTypeAPI extends HollowObjectTypeAPI {

    private final PersonArtworkDerivativeDelegateLookupImpl delegateLookupImpl;

    PersonArtworkDerivativeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "recipeName",
            "fileName",
            "imageId",
            "cdnOriginServerId",
            "width",
            "cdnDirectory",
            "imageType",
            "cdnOriginServer",
            "height"
        });
        this.delegateLookupImpl = new PersonArtworkDerivativeDelegateLookupImpl(this);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "recipeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRecipeNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getFileNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "fileName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getFileNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getImageId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "imageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getImageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "imageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCdnOriginServerIdOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "cdnOriginServerId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCdnOriginServerIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getWidth(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "width");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getWidthBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "width");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCdnDirectoryOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "cdnDirectory");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getCdnDirectoryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCdnOriginServerOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtworkDerivative", ordinal, "cdnOriginServer");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getCdnOriginServerTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getHeight(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "height");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
    }

    public Long getHeightBoxed(int ordinal) {
        long l;
        if(fieldIndex[8] == -1) {
            l = missingDataHandler().handleLong("PersonArtworkDerivative", ordinal, "height");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public PersonArtworkDerivativeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}