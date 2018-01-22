package com.netflix.vms.transformer.octobersky;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.launch.common.Country;
import com.netflix.launch.common.LaunchConfiguration;
import com.netflix.launch.common.NamespaceLaunchConfiguration;
import com.netflix.vms.transformer.common.config.OctoberSkyData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class OctoberSkyDataImpl implements OctoberSkyData {

    private static final String BEEHIVE_NAMESPACE = "beehive";
    private static final String IS_MIN_METADATA_PRESENT = "isMinMetadataPresent";
    private static final String CATALOG_LANGUAGES_COLUMN = "catalogLanguages";

    private final LaunchConfiguration octoberSky;

    private Set<String> supportedCountries;
    private Map<String, Set<String>> multilanguageCountryCatalogLocales;

    @Inject
    public OctoberSkyDataImpl(LaunchConfiguration octoberSky) {
        this.octoberSky = octoberSky;
        refresh();
    }

    @Override
    public Set<String> getSupportedCountries() {
        return supportedCountries;
    }

    @Override
    public Set<String> getCatalogLanguages(String country) {
        return multilanguageCountryCatalogLocales.get(country);
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
        Map<String, Set<String>> multiLanguageCountryCatalog = new HashMap<>();

        List<Country> countries = octoberSky.getCountries();
        for (Country country : countries) {

            Set<String> languages = new HashSet<>();
            // format of string is ["en", "es"]
            String supportedLanguages = country.fetchProperty(CATALOG_LANGUAGES_COLUMN);

            StringBuilder supportedLocaleBuilder = new StringBuilder();
            char[] chars = supportedLanguages.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != '[' && chars[i] != ']' && chars[i] != '\"') {
                    if (chars[i] == ',') {
                        languages.add(supportedLocaleBuilder.toString());
                        supportedLocaleBuilder = new StringBuilder();
                    } else supportedLocaleBuilder.append(chars[i]);
                }
            }
            String remainingLocale = supportedLocaleBuilder.toString();
            if (remainingLocale.length() > 0) languages.add(remainingLocale);

            // if has supported languages
            if (languages.size() > 0) {
                multiLanguageCountryCatalog.put(country.getCode(), languages);
            }

        }
        return multiLanguageCountryCatalog;
    }

}
