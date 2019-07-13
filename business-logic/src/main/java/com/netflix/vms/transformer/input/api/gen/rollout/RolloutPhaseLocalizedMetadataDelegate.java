package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseLocalizedMetadataDelegate extends HollowObjectDelegate {

    public String getSUPPLEMENTAL_MESSAGE(int ordinal);

    public boolean isSUPPLEMENTAL_MESSAGEEqual(int ordinal, String testValue);

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal);

    public String getMERCH_OVERRIDE_MESSAGE(int ordinal);

    public boolean isMERCH_OVERRIDE_MESSAGEEqual(int ordinal, String testValue);

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public String getPOSTPLAY_OVERRIDE_MESSAGE(int ordinal);

    public boolean isPOSTPLAY_OVERRIDE_MESSAGEEqual(int ordinal, String testValue);

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public String getODP_OVERRIDE_MESSAGE(int ordinal);

    public boolean isODP_OVERRIDE_MESSAGEEqual(int ordinal, String testValue);

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public String getPOSTPLAY_ALT(int ordinal);

    public boolean isPOSTPLAY_ALTEqual(int ordinal, String testValue);

    public int getPOSTPLAY_ALTOrdinal(int ordinal);

    public String getPOSTPLAY_COMPLETION(int ordinal);

    public boolean isPOSTPLAY_COMPLETIONEqual(int ordinal, String testValue);

    public int getPOSTPLAY_COMPLETIONOrdinal(int ordinal);

    public String getTAGLINE(int ordinal);

    public boolean isTAGLINEEqual(int ordinal, String testValue);

    public int getTAGLINEOrdinal(int ordinal);

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI();

}