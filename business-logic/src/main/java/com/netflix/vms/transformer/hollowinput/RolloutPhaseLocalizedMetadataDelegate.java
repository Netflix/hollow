package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseLocalizedMetadataDelegate extends HollowObjectDelegate {

    public int getSUPPLEMENTAL_MESSAGEOrdinal(int ordinal);

    public int getMERCH_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public int getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public int getODP_OVERRIDE_MESSAGEOrdinal(int ordinal);

    public int getTAGLINEOrdinal(int ordinal);

    public RolloutPhaseLocalizedMetadataTypeAPI getTypeAPI();

}