package com.netflix.vms.transformer.octobersky;

import java.util.HashSet;

import com.netflix.launch.common.NamespaceLaunchConfiguration;
import com.netflix.launch.common.Country;
import com.netflix.launch.common.LaunchConfiguration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Set;
import com.netflix.vms.transformer.common.config.OctoberSkyData;

@Singleton
public class OctoberSkyDataImpl implements OctoberSkyData {

    private static final String BEEHIVE_NAMESPACE = "beehive";
    private static final String IS_MIN_METADATA_PRESENT = "isMinMetadataPresent";

    private final Set<String> supportedCountries;

    @Inject
    public OctoberSkyDataImpl(LaunchConfiguration octoberSky) {
        this.supportedCountries = findCountriesWithMinMetadata(octoberSky);
    }

    @Override
    public Set<String> getSupportedCountries() {
        return supportedCountries;
    }

    private static Set<String> findCountriesWithMinMetadata(LaunchConfiguration octoberSky) {
        Set<String> minMetadataCountries = new HashSet<String>();

        NamespaceLaunchConfiguration beehiveNamespace = octoberSky.forNamespace(BEEHIVE_NAMESPACE);
        for (String cStr : octoberSky.getCountryCodes()) {
            String countryCodeStr = cStr.trim();
            Country country = beehiveNamespace.getCountry(countryCodeStr);
            if (country != null && Boolean.valueOf(country.fetchProperty(IS_MIN_METADATA_PRESENT)))
                minMetadataCountries.add(countryCodeStr);
        }
        return minMetadataCountries;
    }

}
