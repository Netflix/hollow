package com.netflix.vms.transformer.octobersky;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.launch.common.Country;
import com.netflix.launch.common.LaunchConfiguration;
import com.netflix.launch.common.NamespaceLaunchConfiguration;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class OctoberSkyDataImpl implements OctoberSkyData {

    private static final Logger LOGGER = LoggerFactory.getLogger(OctoberSkyDataImpl.class);

//    private static final String CROSS_PLATFORM_NAMESPACE = "cross-platform-ui";
//    private static final String SUPPORTED_LOCALE_PROPERTY = "productPreferredLanguages_catalog";

    private static final String BEEHIVE_NAMESPACE = "beehive";
    private static final String IS_MIN_METADATA_PRESENT = "isMinMetadataPresent";
    private static final String SUPPORTED_LOCALE_PROPERTY = "supportedLanguages";

    private final LaunchConfiguration octoberSky;
    private final TransformerConfig config;

    private Set<String> supportedCountries;
    private Map<String, Set<String>> multilanguageCountryCatalogLocales;

    @Inject
    public OctoberSkyDataImpl(LaunchConfiguration octoberSky, TransformerConfig config) {
        this.octoberSky = octoberSky;
        this.config = config;
        refresh();
    }

    @Override
    public Set<String> getSupportedCountries() {
        return supportedCountries;
    }

    @Override
    public Set<String> getCatalogLanguages(String country) {
        return multilanguageCountryCatalogLocales.get(country.toUpperCase());
    }

    @Override
    public void refresh() {
        this.supportedCountries = findCountriesWithMinMetadata(octoberSky);
        this.multilanguageCountryCatalogLocales = findMultilanguageCountryCatalogLocales(octoberSky);
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

    private static Map<String, Set<String>> findMultilanguageCountryCatalogLocales(LaunchConfiguration octoberSky) {

        Map<String, Set<String>> multilanguageCountryCatalogLocales = new HashMap<>();

        NamespaceLaunchConfiguration namespaceLaunchConfiguration = octoberSky.forNamespace(BEEHIVE_NAMESPACE);
        List<Country> countries = namespaceLaunchConfiguration.getCountries();
        for (Country country : countries) {

            String supportedLocales = country.fetchProperty(SUPPORTED_LOCALE_PROPERTY);
            if (supportedLocales.contains("["))
                supportedLocales = supportedLocales.replace("[", "");

            if (supportedLocales.contains("]"))
                supportedLocales = supportedLocales.replace("]", "");

            if (supportedLocales.contains("\""))
                supportedLocales = supportedLocales.replace("\"", "");

            String[] locales = supportedLocales.split(",");
            Set<String> countryLocales = multilanguageCountryCatalogLocales.get(country.getCode());
            if (countryLocales == null) {
                countryLocales = new HashSet<>();
                multilanguageCountryCatalogLocales.put(country.getCode(), countryLocales);
            }
            for (String locale : locales)
                countryLocales.add(locale);
            LOGGER.info("Country={} supportedLocales={}", country.getCode(), Arrays.deepToString(countryLocales.toArray()));
        }

        return multilanguageCountryCatalogLocales;
    }

}
