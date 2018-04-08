package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ArtworkLocale")
public class ArtworkLocale implements Cloneable {

    @HollowTypeName(name="LocaleTerritoryCodeList")
    public List<LocaleTerritoryCode> territoryCodes = null;
    public String bcp47Code = null;
    public Date effectiveDate = null;
    public ArtworkAttributes attributes = null;

    public ArtworkLocale setTerritoryCodes(List<LocaleTerritoryCode> territoryCodes) {
        this.territoryCodes = territoryCodes;
        return this;
    }
    public ArtworkLocale setBcp47Code(String bcp47Code) {
        this.bcp47Code = bcp47Code;
        return this;
    }
    public ArtworkLocale setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }
    public ArtworkLocale setAttributes(ArtworkAttributes attributes) {
        this.attributes = attributes;
        return this;
    }
    public ArtworkLocale addToTerritoryCodes(LocaleTerritoryCode localeTerritoryCode) {
        if (this.territoryCodes == null) {
            this.territoryCodes = new ArrayList<LocaleTerritoryCode>();
        }
        this.territoryCodes.add(localeTerritoryCode);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkLocale))
            return false;

        ArtworkLocale o = (ArtworkLocale) other;
        if(o.territoryCodes == null) {
            if(territoryCodes != null) return false;
        } else if(!o.territoryCodes.equals(territoryCodes)) return false;
        if(o.bcp47Code == null) {
            if(bcp47Code != null) return false;
        } else if(!o.bcp47Code.equals(bcp47Code)) return false;
        if(o.effectiveDate == null) {
            if(effectiveDate != null) return false;
        } else if(!o.effectiveDate.equals(effectiveDate)) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (territoryCodes == null ? 1237 : territoryCodes.hashCode());
        hashCode = hashCode * 31 + (bcp47Code == null ? 1237 : bcp47Code.hashCode());
        hashCode = hashCode * 31 + (effectiveDate == null ? 1237 : effectiveDate.hashCode());
        hashCode = hashCode * 31 + (attributes == null ? 1237 : attributes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkLocale{");
        builder.append("territoryCodes=").append(territoryCodes);
        builder.append(",bcp47Code=").append(bcp47Code);
        builder.append(",effectiveDate=").append(effectiveDate);
        builder.append(",attributes=").append(attributes);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkLocale clone() {
        try {
            ArtworkLocale clone = (ArtworkLocale)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}