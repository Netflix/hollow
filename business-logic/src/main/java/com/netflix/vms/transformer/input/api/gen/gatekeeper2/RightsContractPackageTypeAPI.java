package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RightsContractPackageTypeAPI extends HollowObjectTypeAPI {

    private final RightsContractPackageDelegateLookupImpl delegateLookupImpl;

    public RightsContractPackageTypeAPI(Gk2StatusAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "primary",
            "hasRequiredStreams",
            "hasRequiredLanguage",
            "hasLocalText",
            "hasLocalAudio"
        });
        this.delegateLookupImpl = new RightsContractPackageDelegateLookupImpl(this);
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RightsContractPackage", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RightsContractPackage", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getPrimary(int ordinal) {
        if(fieldIndex[1] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "primary"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]));
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "primary");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public boolean getHasRequiredStreams(int ordinal) {
        if(fieldIndex[2] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasRequiredStreams"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]));
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasRequiredStreams");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[2]);
    }



    public boolean getHasRequiredLanguage(int ordinal) {
        if(fieldIndex[3] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasRequiredLanguage"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]));
    }

    public Boolean getHasRequiredLanguageBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasRequiredLanguage");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public boolean getHasLocalText(int ordinal) {
        if(fieldIndex[4] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasLocalText"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]));
    }

    public Boolean getHasLocalTextBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasLocalText");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public boolean getHasLocalAudio(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasLocalAudio"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getHasLocalAudioBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("RightsContractPackage", ordinal, "hasLocalAudio");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public RightsContractPackageDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public Gk2StatusAPI getAPI() {
        return (Gk2StatusAPI) api;
    }

}