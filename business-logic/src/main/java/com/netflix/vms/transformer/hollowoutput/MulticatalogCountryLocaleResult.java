package com.netflix.vms.transformer.hollowoutput;

import java.util.Collections;
import java.util.List;

public class MulticatalogCountryLocaleResult {
    
    public ISOCountry country = null;
    public NFLocale language = null;
    public List<VMSAvailabilityWindow> availabilityWindows = Collections.emptyList();
    public boolean hasLocalAudio = false;
    public boolean hasLocalText = false;
    public boolean isSearchOnly = false;
    public boolean hasNewContent = false;
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((availabilityWindows == null) ? 0 : availabilityWindows.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + (hasLocalAudio ? 1231 : 1237);
        result = prime * result + (hasLocalText ? 1231 : 1237);
        result = prime * result + (hasNewContent ? 1231 : 1237);
        result = prime * result + (isSearchOnly ? 1231 : 1237);
        result = prime * result + ((language == null) ? 0 : language.hashCode());
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
        MulticatalogCountryLocaleResult other = (MulticatalogCountryLocaleResult) obj;
        if (availabilityWindows == null) {
            if (other.availabilityWindows != null)
                return false;
        } else if (!availabilityWindows.equals(other.availabilityWindows))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (hasLocalAudio != other.hasLocalAudio)
            return false;
        if (hasLocalText != other.hasLocalText)
            return false;
        if (hasNewContent != other.hasNewContent)
            return false;
        if (isSearchOnly != other.isSearchOnly)
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MulticatalogCountryLocaleResult [country=" + country + ", language=" + language + ", availabilityWindows=" + availabilityWindows + ", hasLocalAudio=" + hasLocalAudio
                + ", hasLocalText=" + hasLocalText + ", isSearchOnly=" + isSearchOnly + ", hasNewContent=" + hasNewContent + "]";
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;

}
