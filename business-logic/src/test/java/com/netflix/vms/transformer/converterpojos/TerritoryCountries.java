package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="TerritoryCountries")
public class TerritoryCountries implements Cloneable {

    public String territoryCode = null;
    @HollowTypeName(name="ISOCountryList")
    public List<ISOCountry> countryCodes = null;

    public TerritoryCountries setTerritoryCode(String territoryCode) {
        this.territoryCode = territoryCode;
        return this;
    }
    public TerritoryCountries setCountryCodes(List<ISOCountry> countryCodes) {
        this.countryCodes = countryCodes;
        return this;
    }
    public TerritoryCountries addToCountryCodes(ISOCountry iSOCountry) {
        if (this.countryCodes == null) {
            this.countryCodes = new ArrayList<ISOCountry>();
        }
        this.countryCodes.add(iSOCountry);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TerritoryCountries))
            return false;

        TerritoryCountries o = (TerritoryCountries) other;
        if(o.territoryCode == null) {
            if(territoryCode != null) return false;
        } else if(!o.territoryCode.equals(territoryCode)) return false;
        if(o.countryCodes == null) {
            if(countryCodes != null) return false;
        } else if(!o.countryCodes.equals(countryCodes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (territoryCode == null ? 1237 : territoryCode.hashCode());
        hashCode = hashCode * 31 + (countryCodes == null ? 1237 : countryCodes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TerritoryCountries{");
        builder.append("territoryCode=").append(territoryCode);
        builder.append(",countryCodes=").append(countryCodes);
        builder.append("}");
        return builder.toString();
    }

    public TerritoryCountries clone() {
        try {
            TerritoryCountries clone = (TerritoryCountries)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}