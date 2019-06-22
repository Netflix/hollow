package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackageDelegateLookupImpl extends HollowObjectAbstractDelegate implements RightsContractPackageDelegate {

    private final RightsContractPackageTypeAPI typeAPI;

    public RightsContractPackageDelegateLookupImpl(RightsContractPackageTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        return typeAPI.getPackageId(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        return typeAPI.getPackageIdBoxed(ordinal);
    }

    public boolean getPrimary(int ordinal) {
        return typeAPI.getPrimary(ordinal);
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        return typeAPI.getPrimaryBoxed(ordinal);
    }

    public boolean getHasRequiredStreams(int ordinal) {
        return typeAPI.getHasRequiredStreams(ordinal);
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        return typeAPI.getHasRequiredStreamsBoxed(ordinal);
    }

    public boolean getHasRequiredLanguage(int ordinal) {
        return typeAPI.getHasRequiredLanguage(ordinal);
    }

    public Boolean getHasRequiredLanguageBoxed(int ordinal) {
        return typeAPI.getHasRequiredLanguageBoxed(ordinal);
    }

    public boolean getHasLocalText(int ordinal) {
        return typeAPI.getHasLocalText(ordinal);
    }

    public Boolean getHasLocalTextBoxed(int ordinal) {
        return typeAPI.getHasLocalTextBoxed(ordinal);
    }

    public boolean getHasLocalAudio(int ordinal) {
        return typeAPI.getHasLocalAudio(ordinal);
    }

    public Boolean getHasLocalAudioBoxed(int ordinal) {
        return typeAPI.getHasLocalAudioBoxed(ordinal);
    }

    public RightsContractPackageTypeAPI getTypeAPI() {
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