package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Collections;
import java.util.Map;

@HollowPrimaryKey(fields={"videoId", "country"})
public class MulticatalogCountryData {
    
    public Video videoId = null;
    public ISOCountry country = null;
    public Map<NFLocale, MulticatalogCountryLocaleData> languageData = Collections.emptyMap();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((languageData == null) ? 0 : languageData.hashCode());
        result = prime * result + ((videoId == null) ? 0 : videoId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MulticatalogCountryData other = (MulticatalogCountryData) obj;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (languageData == null) {
            if (other.languageData != null)
                return false;
        } else if (!languageData.equals(other.languageData))
            return false;
        if (videoId == null) {
            if (other.videoId != null)
                return false;
        } else if (!videoId.equals(other.videoId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MulticatalogCountryData [videoId=" + videoId + ", country=" + country + ", languageData=" + languageData + "]";
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;

}
