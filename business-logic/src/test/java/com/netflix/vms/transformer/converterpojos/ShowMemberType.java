package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="ShowMemberType")
public class ShowMemberType implements Cloneable {

    @HollowTypeName(name="ISOCountryList")
    public List<ISOCountry> countryCodes = null;
    public long sequenceLabelId = java.lang.Long.MIN_VALUE;

    public ShowMemberType setCountryCodes(List<ISOCountry> countryCodes) {
        this.countryCodes = countryCodes;
        return this;
    }
    public ShowMemberType setSequenceLabelId(long sequenceLabelId) {
        this.sequenceLabelId = sequenceLabelId;
        return this;
    }
    public ShowMemberType addToCountryCodes(ISOCountry iSOCountry) {
        if (this.countryCodes == null) {
            this.countryCodes = new ArrayList<ISOCountry>();
        }
        this.countryCodes.add(iSOCountry);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ShowMemberType))
            return false;

        ShowMemberType o = (ShowMemberType) other;
        if(o.countryCodes == null) {
            if(countryCodes != null) return false;
        } else if(!o.countryCodes.equals(countryCodes)) return false;
        if(o.sequenceLabelId != sequenceLabelId) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (countryCodes == null ? 1237 : countryCodes.hashCode());
        hashCode = hashCode * 31 + (int) (sequenceLabelId ^ (sequenceLabelId >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ShowMemberType{");
        builder.append("countryCodes=").append(countryCodes);
        builder.append(",sequenceLabelId=").append(sequenceLabelId);
        builder.append("}");
        return builder.toString();
    }

    public ShowMemberType clone() {
        try {
            ShowMemberType clone = (ShowMemberType)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}