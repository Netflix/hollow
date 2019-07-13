package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class IPLArtworkDerivativeTypeAPI extends HollowObjectTypeAPI {

    private final IPLArtworkDerivativeDelegateLookupImpl delegateLookupImpl;

    public IPLArtworkDerivativeTypeAPI(MceImageV3API api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "recipeName",
            "widthInPixels",
            "heightInPixels",
            "targetWidthInPixels",
            "targetHeightInPixels",
            "recipeDescriptor",
            "cdnId",
            "languageCode",
            "modifications",
            "overlayTypes"
        });
        this.delegateLookupImpl = new IPLArtworkDerivativeDelegateLookupImpl(this);
    }

    public int getRecipeNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "recipeName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getRecipeNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getWidthInPixels(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "widthInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "widthInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getHeightInPixels(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "heightInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "heightInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getTargetWidthInPixels(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "targetWidthInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "targetWidthInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getTargetHeightInPixels(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "targetHeightInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[4] == -1) {
            i = missingDataHandler().handleInt("IPLArtworkDerivative", ordinal, "targetHeightInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getRecipeDescriptorOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "recipeDescriptor");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getRecipeDescriptorTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCdnIdOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "cdnId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StringTypeAPI getCdnIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "languageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getModificationsOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "modifications");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public ListOfDerivativeTagTypeAPI getModificationsTypeAPI() {
        return getAPI().getListOfDerivativeTagTypeAPI();
    }

    public int getOverlayTypesOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLArtworkDerivative", ordinal, "overlayTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public ListOfDerivativeTagTypeAPI getOverlayTypesTypeAPI() {
        return getAPI().getListOfDerivativeTagTypeAPI();
    }

    public IPLArtworkDerivativeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MceImageV3API getAPI() {
        return (MceImageV3API) api;
    }

}