package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadataDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhaseLocalizedMetadataDelegate {

    private final String SUPPLEMENTAL_MESSAGE;
    private final int SUPPLEMENTAL_MESSAGEOrdinal;
    private final String MERCH_OVERRIDE_MESSAGE;
    private final int MERCH_OVERRIDE_MESSAGEOrdinal;
    private final String POSTPLAY_OVERRIDE_MESSAGE;
    private final int POSTPLAY_OVERRIDE_MESSAGEOrdinal;
    private final String ODP_OVERRIDE_MESSAGE;
    private final int ODP_OVERRIDE_MESSAGEOrdinal;
    private final String POSTPLAY_ALT;
    private final int POSTPLAY_ALTOrdinal;
    private final String POSTPLAY_COMPLETION;
    private final int POSTPLAY_COMPLETIONOrdinal;
    private final String TAGLINE;
    private final int TAGLINEOrdinal;
    private RolloutPhaseLocalizedMetadataTypeAPI typeAPI;

    public RolloutPhaseLocalizedMetadataDelegateCachedImpl(RolloutPhaseLocalizedMetadataTypeAPI typeAPI, int ordinal) {
        this.SUPPLEMENTAL_MESSAGEOrdinal = typeAPI.getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        int SUPPLEMENTAL_MESSAGETempOrdinal = SUPPLEMENTAL_MESSAGEOrdinal;
        this.SUPPLEMENTAL_MESSAGE = SUPPLEMENTAL_MESSAGETempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(SUPPLEMENTAL_MESSAGETempOrdinal);
        this.MERCH_OVERRIDE_MESSAGEOrdinal = typeAPI.getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        int MERCH_OVERRIDE_MESSAGETempOrdinal = MERCH_OVERRIDE_MESSAGEOrdinal;
        this.MERCH_OVERRIDE_MESSAGE = MERCH_OVERRIDE_MESSAGETempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(MERCH_OVERRIDE_MESSAGETempOrdinal);
        this.POSTPLAY_OVERRIDE_MESSAGEOrdinal = typeAPI.getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        int POSTPLAY_OVERRIDE_MESSAGETempOrdinal = POSTPLAY_OVERRIDE_MESSAGEOrdinal;
        this.POSTPLAY_OVERRIDE_MESSAGE = POSTPLAY_OVERRIDE_MESSAGETempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(POSTPLAY_OVERRIDE_MESSAGETempOrdinal);
        this.ODP_OVERRIDE_MESSAGEOrdinal = typeAPI.getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
        int ODP_OVERRIDE_MESSAGETempOrdinal = ODP_OVERRIDE_MESSAGEOrdinal;
        this.ODP_OVERRIDE_MESSAGE = ODP_OVERRIDE_MESSAGETempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ODP_OVERRIDE_MESSAGETempOrdinal);
        this.POSTPLAY_ALTOrdinal = typeAPI.getPOSTPLAY_ALTOrdinal(ordinal);
        int POSTPLAY_ALTTempOrdinal = POSTPLAY_ALTOrdinal;
        this.POSTPLAY_ALT = POSTPLAY_ALTTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(POSTPLAY_ALTTempOrdinal);
        this.POSTPLAY_COMPLETIONOrdinal = typeAPI.getPOSTPLAY_COMPLETIONOrdinal(ordinal);
        int POSTPLAY_COMPLETIONTempOrdinal = POSTPLAY_COMPLETIONOrdinal;
        this.POSTPLAY_COMPLETION = POSTPLAY_COMPLETIONTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(POSTPLAY_COMPLETIONTempOrdinal);
        this.TAGLINEOrdinal = typeAPI.getTAGLINEOrdinal(ordinal);
        int TAGLINETempOrdinal = TAGLINEOrdinal;
        this.TAGLINE = TAGLINETempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(TAGLINETempOrdinal);
        this.typeAPI = typeAPI;
    }

    public String getSUPPLEMENTAL_MESSAGE(int ordinal) {
        return SUPPLEMENTAL_MESSAGE;
    }

    public boolean isSUPPLEMENTAL_MESSAGEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return SUPPLEMENTAL_MESSAGE == null;
        return testValue.equals(SUPPLEMENTAL_MESSAGE);
    }

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal) {
        return SUPPLEMENTAL_MESSAGEOrdinal;
    }

    public String getMERCH_OVERRIDE_MESSAGE(int ordinal) {
        return MERCH_OVERRIDE_MESSAGE;
    }

    public boolean isMERCH_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return MERCH_OVERRIDE_MESSAGE == null;
        return testValue.equals(MERCH_OVERRIDE_MESSAGE);
    }

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return MERCH_OVERRIDE_MESSAGEOrdinal;
    }

    public String getPOSTPLAY_OVERRIDE_MESSAGE(int ordinal) {
        return POSTPLAY_OVERRIDE_MESSAGE;
    }

    public boolean isPOSTPLAY_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return POSTPLAY_OVERRIDE_MESSAGE == null;
        return testValue.equals(POSTPLAY_OVERRIDE_MESSAGE);
    }

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return POSTPLAY_OVERRIDE_MESSAGEOrdinal;
    }

    public String getODP_OVERRIDE_MESSAGE(int ordinal) {
        return ODP_OVERRIDE_MESSAGE;
    }

    public boolean isODP_OVERRIDE_MESSAGEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return ODP_OVERRIDE_MESSAGE == null;
        return testValue.equals(ODP_OVERRIDE_MESSAGE);
    }

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal) {
        return ODP_OVERRIDE_MESSAGEOrdinal;
    }

    public String getPOSTPLAY_ALT(int ordinal) {
        return POSTPLAY_ALT;
    }

    public boolean isPOSTPLAY_ALTEqual(int ordinal, String testValue) {
        if(testValue == null)
            return POSTPLAY_ALT == null;
        return testValue.equals(POSTPLAY_ALT);
    }

    public int getPOSTPLAY_ALTOrdinal(int ordinal) {
        return POSTPLAY_ALTOrdinal;
    }

    public String getPOSTPLAY_COMPLETION(int ordinal) {
        return POSTPLAY_COMPLETION;
    }

    public boolean isPOSTPLAY_COMPLETIONEqual(int ordinal, String testValue) {
        if(testValue == null)
            return POSTPLAY_COMPLETION == null;
        return testValue.equals(POSTPLAY_COMPLETION);
    }

    public int getPOSTPLAY_COMPLETIONOrdinal(int ordinal) {
        return POSTPLAY_COMPLETIONOrdinal;
    }

    public String getTAGLINE(int ordinal) {
        return TAGLINE;
    }

    public boolean isTAGLINEEqual(int ordinal, String testValue) {
        if(testValue == null)
            return TAGLINE == null;
        return testValue.equals(TAGLINE);
    }

    public int getTAGLINEOrdinal(int ordinal) {
        return TAGLINEOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhaseLocalizedMetadataTypeAPI) typeAPI;
    }

}