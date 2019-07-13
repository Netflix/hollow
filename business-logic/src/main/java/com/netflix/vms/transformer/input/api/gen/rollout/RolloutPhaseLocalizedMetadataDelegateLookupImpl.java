package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhaseLocalizedMetadataDelegate {

    private final RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateLookupImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getSUPPLEMENTAL_MESSAGE(int ordinal) {
        ordinal = typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isSUPPLEMENTAL_MESSAGEEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
    }

    public String getMERCH_OVERRIDE_MESSAGE(int ordinal) {
        ordinal = typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isMERCH_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public String getPOSTPLAY_OVERRIDE_MESSAGE(int ordinal) {
        ordinal = typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPOSTPLAY_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public String getODP_OVERRIDE_MESSAGE(int ordinal) {
        ordinal = typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isODP_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
    }

    public String getPOSTPLAY_ALT(int ordinal) {
        ordinal = typeAPI.getPOSTPLAY_ALTOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPOSTPLAY_ALTEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPOSTPLAY_ALTOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPOSTPLAY_ALTOrdinal(int ordinal) {
        return typeAPI.getPOSTPLAY_ALTOrdinal(ordinal);
    }

    public String getPOSTPLAY_COMPLETION(int ordinal) {
        ordinal = typeAPI.getPOSTPLAY_COMPLETIONOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isPOSTPLAY_COMPLETIONEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getPOSTPLAY_COMPLETIONOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getPOSTPLAY_COMPLETIONOrdinal(int ordinal) {
        return typeAPI.getPOSTPLAY_COMPLETIONOrdinal(ordinal);
    }

    public String getTAGLINE(int ordinal) {
        ordinal = typeAPI.getTAGLINEOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isTAGLINEEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getTAGLINEOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getTAGLINEOrdinal(int ordinal) {
        return typeAPI.getTAGLINEOrdinal(ordinal);
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI() {
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