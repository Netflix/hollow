package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="RolloutPhaseLocalizedMetadata")
public class RolloutPhaseLocalizedMetadata implements Cloneable {

    public String SUPPLEMENTAL_MESSAGE = null;
    public String MERCH_OVERRIDE_MESSAGE = null;
    public String POSTPLAY_OVERRIDE_MESSAGE = null;
    public String ODP_OVERRIDE_MESSAGE = null;
    public String TAGLINE = null;

    public RolloutPhaseLocalizedMetadata setSUPPLEMENTAL_MESSAGE(String SUPPLEMENTAL_MESSAGE) {
        this.SUPPLEMENTAL_MESSAGE = SUPPLEMENTAL_MESSAGE;
        return this;
    }
    public RolloutPhaseLocalizedMetadata setMERCH_OVERRIDE_MESSAGE(String MERCH_OVERRIDE_MESSAGE) {
        this.MERCH_OVERRIDE_MESSAGE = MERCH_OVERRIDE_MESSAGE;
        return this;
    }
    public RolloutPhaseLocalizedMetadata setPOSTPLAY_OVERRIDE_MESSAGE(String POSTPLAY_OVERRIDE_MESSAGE) {
        this.POSTPLAY_OVERRIDE_MESSAGE = POSTPLAY_OVERRIDE_MESSAGE;
        return this;
    }
    public RolloutPhaseLocalizedMetadata setODP_OVERRIDE_MESSAGE(String ODP_OVERRIDE_MESSAGE) {
        this.ODP_OVERRIDE_MESSAGE = ODP_OVERRIDE_MESSAGE;
        return this;
    }
    public RolloutPhaseLocalizedMetadata setTAGLINE(String TAGLINE) {
        this.TAGLINE = TAGLINE;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutPhaseLocalizedMetadata))
            return false;

        RolloutPhaseLocalizedMetadata o = (RolloutPhaseLocalizedMetadata) other;
        if(o.SUPPLEMENTAL_MESSAGE == null) {
            if(SUPPLEMENTAL_MESSAGE != null) return false;
        } else if(!o.SUPPLEMENTAL_MESSAGE.equals(SUPPLEMENTAL_MESSAGE)) return false;
        if(o.MERCH_OVERRIDE_MESSAGE == null) {
            if(MERCH_OVERRIDE_MESSAGE != null) return false;
        } else if(!o.MERCH_OVERRIDE_MESSAGE.equals(MERCH_OVERRIDE_MESSAGE)) return false;
        if(o.POSTPLAY_OVERRIDE_MESSAGE == null) {
            if(POSTPLAY_OVERRIDE_MESSAGE != null) return false;
        } else if(!o.POSTPLAY_OVERRIDE_MESSAGE.equals(POSTPLAY_OVERRIDE_MESSAGE)) return false;
        if(o.ODP_OVERRIDE_MESSAGE == null) {
            if(ODP_OVERRIDE_MESSAGE != null) return false;
        } else if(!o.ODP_OVERRIDE_MESSAGE.equals(ODP_OVERRIDE_MESSAGE)) return false;
        if(o.TAGLINE == null) {
            if(TAGLINE != null) return false;
        } else if(!o.TAGLINE.equals(TAGLINE)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (SUPPLEMENTAL_MESSAGE == null ? 1237 : SUPPLEMENTAL_MESSAGE.hashCode());
        hashCode = hashCode * 31 + (MERCH_OVERRIDE_MESSAGE == null ? 1237 : MERCH_OVERRIDE_MESSAGE.hashCode());
        hashCode = hashCode * 31 + (POSTPLAY_OVERRIDE_MESSAGE == null ? 1237 : POSTPLAY_OVERRIDE_MESSAGE.hashCode());
        hashCode = hashCode * 31 + (ODP_OVERRIDE_MESSAGE == null ? 1237 : ODP_OVERRIDE_MESSAGE.hashCode());
        hashCode = hashCode * 31 + (TAGLINE == null ? 1237 : TAGLINE.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutPhaseLocalizedMetadata{");
        builder.append("SUPPLEMENTAL_MESSAGE=").append(SUPPLEMENTAL_MESSAGE);
        builder.append(",MERCH_OVERRIDE_MESSAGE=").append(MERCH_OVERRIDE_MESSAGE);
        builder.append(",POSTPLAY_OVERRIDE_MESSAGE=").append(POSTPLAY_OVERRIDE_MESSAGE);
        builder.append(",ODP_OVERRIDE_MESSAGE=").append(ODP_OVERRIDE_MESSAGE);
        builder.append(",TAGLINE=").append(TAGLINE);
        builder.append("}");
        return builder.toString();
    }

    public RolloutPhaseLocalizedMetadata clone() {
        try {
            RolloutPhaseLocalizedMetadata clone = (RolloutPhaseLocalizedMetadata)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}