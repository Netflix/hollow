package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ArtworkDerivativeTypeAPI extends HollowObjectTypeAPI {

    private final ArtworkDerivativeDelegateLookupImpl delegateLookupImpl;

    ArtworkDerivativeTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "recipeName",
            "cdnOriginServerId",
            "width",
            "cdnDirectory",
            "cdnId",
            "recipeDescriptor",
            "imageType",
            "cdnOriginServer",
            "height"
        });
        this.delegateLookupImpl = new ArtworkDerivativeDelegateLookupImpl(this);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "recipeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRecipeNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCdnOriginServerIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "cdnOriginServerId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCdnOriginServerIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getWidth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ArtworkDerivative", ordinal, "width");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getWidthBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ArtworkDerivative", ordinal, "width");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCdnDirectoryOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "cdnDirectory");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getCdnDirectoryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCdnIdOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "cdnId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getCdnIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRecipeDescriptorOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "recipeDescriptor");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getRecipeDescriptorTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCdnOriginServerOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("ArtworkDerivative", ordinal, "cdnOriginServer");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getCdnOriginServerTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getHeight(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleLong("ArtworkDerivative", ordinal, "height");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
    }

    public Long getHeightBoxed(int ordinal) {
        long l;
        if(fieldIndex[8] == -1) {
            l = missingDataHandler().handleLong("ArtworkDerivative", ordinal, "height");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ArtworkDerivativeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}