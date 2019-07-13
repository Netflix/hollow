package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class IPLDerivativeGroupTypeAPI extends HollowObjectTypeAPI {

    private final IPLDerivativeGroupDelegateLookupImpl delegateLookupImpl;

    public IPLDerivativeGroupTypeAPI(MceImageV3API api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "externalId",
            "submission",
            "imageType",
            "derivatives"
        });
        this.delegateLookupImpl = new IPLDerivativeGroupDelegateLookupImpl(this);
    }

    public int getExternalIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLDerivativeGroup", ordinal, "externalId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getExternalIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSubmission(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("IPLDerivativeGroup", ordinal, "submission");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getSubmissionBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("IPLDerivativeGroup", ordinal, "submission");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLDerivativeGroup", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDerivativesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("IPLDerivativeGroup", ordinal, "derivatives");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public IPLDerivativeSetTypeAPI getDerivativesTypeAPI() {
        return getAPI().getIPLDerivativeSetTypeAPI();
    }

    public IPLDerivativeGroupDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MceImageV3API getAPI() {
        return (MceImageV3API) api;
    }

}