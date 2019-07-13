package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackageDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate,
        RightsContractPackageDelegate {

    private final Long packageId;
    private final Boolean primary;
    private final Boolean hasRequiredStreams;
    private final Boolean hasRequiredLanguage;
    private final Boolean hasLocalText;
    private final Boolean hasLocalAudio;
    private RightsContractPackageTypeAPI typeAPI;

    public RightsContractPackageDelegateCachedImpl(RightsContractPackageTypeAPI typeAPI, int ordinal) {
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.primary = typeAPI.getPrimaryBoxed(ordinal);
        this.hasRequiredStreams = typeAPI.getHasRequiredStreamsBoxed(ordinal);
        this.hasRequiredLanguage = typeAPI.getHasRequiredLanguageBoxed(ordinal);
        this.hasLocalText = typeAPI.getHasLocalTextBoxed(ordinal);
        this.hasLocalAudio = typeAPI.getHasLocalAudioBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getPackageId(int ordinal) {
        if(packageId == null)
            return Long.MIN_VALUE;
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public boolean getPrimary(int ordinal) {
        if(primary == null)
            return false;
        return primary.booleanValue();
    }

    public Boolean getPrimaryBoxed(int ordinal) {
        return primary;
    }

    public boolean getHasRequiredStreams(int ordinal) {
        if(hasRequiredStreams == null)
            return false;
        return hasRequiredStreams.booleanValue();
    }

    public Boolean getHasRequiredStreamsBoxed(int ordinal) {
        return hasRequiredStreams;
    }

    public boolean getHasRequiredLanguage(int ordinal) {
        if(hasRequiredLanguage == null)
            return false;
        return hasRequiredLanguage.booleanValue();
    }

    public Boolean getHasRequiredLanguageBoxed(int ordinal) {
        return hasRequiredLanguage;
    }

    public boolean getHasLocalText(int ordinal) {
        if(hasLocalText == null)
            return false;
        return hasLocalText.booleanValue();
    }

    public Boolean getHasLocalTextBoxed(int ordinal) {
        return hasLocalText;
    }

    public boolean getHasLocalAudio(int ordinal) {
        if(hasLocalAudio == null)
            return false;
        return hasLocalAudio.booleanValue();
    }

    public Boolean getHasLocalAudioBoxed(int ordinal) {
        return hasLocalAudio;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RightsContractPackageTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RightsContractPackageTypeAPI) typeAPI;
    }

}