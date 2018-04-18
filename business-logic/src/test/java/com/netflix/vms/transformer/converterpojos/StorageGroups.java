package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="StorageGroups")
public class StorageGroups implements Cloneable {

    public String id = null;
    public long cdnId = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="ISOCountryList")
    public List<ISOCountry> countries = null;

    public StorageGroups setId(String id) {
        this.id = id;
        return this;
    }
    public StorageGroups setCdnId(long cdnId) {
        this.cdnId = cdnId;
        return this;
    }
    public StorageGroups setCountries(List<ISOCountry> countries) {
        this.countries = countries;
        return this;
    }
    public StorageGroups addToCountries(ISOCountry iSOCountry) {
        if (this.countries == null) {
            this.countries = new ArrayList<ISOCountry>();
        }
        this.countries.add(iSOCountry);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StorageGroups))
            return false;

        StorageGroups o = (StorageGroups) other;
        if(o.id == null) {
            if(id != null) return false;
        } else if(!o.id.equals(id)) return false;
        if(o.cdnId != cdnId) return false;
        if(o.countries == null) {
            if(countries != null) return false;
        } else if(!o.countries.equals(countries)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (id == null ? 1237 : id.hashCode());
        hashCode = hashCode * 31 + (int) (cdnId ^ (cdnId >>> 32));
        hashCode = hashCode * 31 + (countries == null ? 1237 : countries.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StorageGroups{");
        builder.append("id=").append(id);
        builder.append(",cdnId=").append(cdnId);
        builder.append(",countries=").append(countries);
        builder.append("}");
        return builder.toString();
    }

    public StorageGroups clone() {
        try {
            StorageGroups clone = (StorageGroups)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}