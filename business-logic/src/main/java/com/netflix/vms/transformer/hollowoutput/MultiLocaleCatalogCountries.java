package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;

/**
 * This type contains a set of countries that have multi-locale catalog.
 */
public class MultiLocaleCatalogCountries {

    public Set<ISOCountry> countries;

    public MultiLocaleCatalogCountries(Set<ISOCountry> countries) {
        this.countries = countries;
    }


    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;


    @Override
    public String toString() {
        return "MultiLocaleCatalogCountries{" +
                "countries=" + countries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiLocaleCatalogCountries)) return false;

        MultiLocaleCatalogCountries that = (MultiLocaleCatalogCountries) o;

        return countries != null ? countries.equals(that.countries) : that.countries == null;
    }

    @Override
    public int hashCode() {
        return countries != null ? countries.hashCode() : 0;
    }
}
