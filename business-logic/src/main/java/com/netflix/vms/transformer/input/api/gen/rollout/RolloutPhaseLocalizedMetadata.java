package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RolloutPhaseLocalizedMetadata extends HollowObject {

    public RolloutPhaseLocalizedMetadata(RolloutPhaseLocalizedMetadataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getSUPPLEMENTAL_MESSAGE() {
        return delegate().getSUPPLEMENTAL_MESSAGE(ordinal);
    }

    public boolean isSUPPLEMENTAL_MESSAGEEqual(String testValue) {
        return delegate().isSUPPLEMENTAL_MESSAGEEqual(ordinal, testValue);
    }

    public HString getSUPPLEMENTAL_MESSAGEHollowReference() {
        int refOrdinal = delegate().getSUPPLEMENTAL_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getMERCH_OVERRIDE_MESSAGE() {
        return delegate().getMERCH_OVERRIDE_MESSAGE(ordinal);
    }

    public boolean isMERCH_OVERRIDE_MESSAGEEqual(String testValue) {
        return delegate().isMERCH_OVERRIDE_MESSAGEEqual(ordinal, testValue);
    }

    public HString getMERCH_OVERRIDE_MESSAGEHollowReference() {
        int refOrdinal = delegate().getMERCH_OVERRIDE_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getPOSTPLAY_OVERRIDE_MESSAGE() {
        return delegate().getPOSTPLAY_OVERRIDE_MESSAGE(ordinal);
    }

    public boolean isPOSTPLAY_OVERRIDE_MESSAGEEqual(String testValue) {
        return delegate().isPOSTPLAY_OVERRIDE_MESSAGEEqual(ordinal, testValue);
    }

    public HString getPOSTPLAY_OVERRIDE_MESSAGEHollowReference() {
        int refOrdinal = delegate().getPOSTPLAY_OVERRIDE_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getODP_OVERRIDE_MESSAGE() {
        return delegate().getODP_OVERRIDE_MESSAGE(ordinal);
    }

    public boolean isODP_OVERRIDE_MESSAGEEqual(String testValue) {
        return delegate().isODP_OVERRIDE_MESSAGEEqual(ordinal, testValue);
    }

    public HString getODP_OVERRIDE_MESSAGEHollowReference() {
        int refOrdinal = delegate().getODP_OVERRIDE_MESSAGEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getPOSTPLAY_ALT() {
        return delegate().getPOSTPLAY_ALT(ordinal);
    }

    public boolean isPOSTPLAY_ALTEqual(String testValue) {
        return delegate().isPOSTPLAY_ALTEqual(ordinal, testValue);
    }

    public HString getPOSTPLAY_ALTHollowReference() {
        int refOrdinal = delegate().getPOSTPLAY_ALTOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getPOSTPLAY_COMPLETION() {
        return delegate().getPOSTPLAY_COMPLETION(ordinal);
    }

    public boolean isPOSTPLAY_COMPLETIONEqual(String testValue) {
        return delegate().isPOSTPLAY_COMPLETIONEqual(ordinal, testValue);
    }

    public HString getPOSTPLAY_COMPLETIONHollowReference() {
        int refOrdinal = delegate().getPOSTPLAY_COMPLETIONOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getTAGLINE() {
        return delegate().getTAGLINE(ordinal);
    }

    public boolean isTAGLINEEqual(String testValue) {
        return delegate().isTAGLINEEqual(ordinal, testValue);
    }

    public HString getTAGLINEHollowReference() {
        int refOrdinal = delegate().getTAGLINEOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public RolloutAPI api() {
        return typeApi().getAPI();
    }

    public RolloutPhaseLocalizedMetadataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RolloutPhaseLocalizedMetadataDelegate delegate() {
        return (RolloutPhaseLocalizedMetadataDelegate)delegate;
    }

}