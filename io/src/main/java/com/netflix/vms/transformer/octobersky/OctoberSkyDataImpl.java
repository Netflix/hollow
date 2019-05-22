package com.netflix.vms.transformer.octobersky;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.i18n.NFLocale;
import com.netflix.launch.common.Catalog;
import com.netflix.launch.common.Country;
import com.netflix.launch.common.LaunchConfiguration;
import com.netflix.launch.common.NamespaceLaunchConfiguration;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OctoberSkyDataImpl implements OctoberSkyData {

    private static final Logger LOGGER = LoggerFactory.getLogger(OctoberSkyDataImpl.class);

    private static final String BEEHIVE_NAMESPACE = "beehive";
    private static final String LANGUAGE_VARIANTS_COLUMN = "merchCatalogs";
    private static final String COUNTRY_LANGUAGE_SEP = ":";
    private static final String IS_MIN_METADATA_PRESENT = "isMinMetadataPresent";

    private final LaunchConfiguration octoberSky;
    private final TransformerConfig config;
    private final ObjectMapper om;

    private Set<String> supportedCountries;
    private Map<String, Set<String>> supportedLanguageCatalogsCountryMap;
    // map of country:language catalog to supported language variants and other metadata
    // example "HK:en" -> languageCatalogMetadata
    private Map<String, LanguageCatalogMetadata> countryLanguageCatalogLanguageVariantsMap;

    @Inject
    public OctoberSkyDataImpl(LaunchConfiguration octoberSky, TransformerConfig config) {
        this.octoberSky = octoberSky;
        this.config = config;
        this.om = new ObjectMapper();
        refresh();
    }

    @Override
    public Set<String> getSupportedCountries() {
        return supportedCountries;
    }

    @Override
    public Set<String> getCatalogLanguages(String country) {
        return supportedLanguageCatalogsCountryMap.get(country);
    }

    @Override
    public Set<String> getMultiLanguageCatalogCountries() {
        return supportedLanguageCatalogsCountryMap.keySet();
    }

    @Override
    public Set<String> getLanguageVariants(String country, String language) {
        LanguageCatalogMetadata catalogMetadata = countryLanguageCatalogLanguageVariantsMap.get(country + COUNTRY_LANGUAGE_SEP + language);
        if (catalogMetadata != null && catalogMetadata.getCatalogLanguages() != null)
            return catalogMetadata.getCatalogLanguages().stream().collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @Override
    public Set<String> getOtherLanguageVariants(String country, String language) {
        LanguageCatalogMetadata otherLanguages = countryLanguageCatalogLanguageVariantsMap.get(country + COUNTRY_LANGUAGE_SEP + language);
        if (otherLanguages != null && otherLanguages.getOtherLanguagesIfOriginalLanguage() != null)
            return otherLanguages.getOtherLanguagesIfOriginalLanguage().stream().collect(Collectors.toSet());
        return Collections.emptySet();
    }

    // refresh is called every cycle begin, so each cycle has a consistent view of data
    @Override
    public void refresh() {
        this.supportedCountries = findCountriesWithMinMetadata();
        this.supportedLanguageCatalogsCountryMap = findMultiCatalogLanguages();
        this.countryLanguageCatalogLanguageVariantsMap = findLanguageVariants();
    }

    private Set<String> findCountriesWithMinMetadata() {
        Set<String> minMetadataCountries = new HashSet<>();

        NamespaceLaunchConfiguration beehiveNamespace = octoberSky.forNamespace(BEEHIVE_NAMESPACE);
        for (String cStr : octoberSky.getCountryCodes()) {
            String countryCodeStr = cStr.trim();
            Country country = beehiveNamespace.getCountry(countryCodeStr);
            if (country != null && Boolean.valueOf(country.fetchProperty(IS_MIN_METADATA_PRESENT))) {
                minMetadataCountries.add(countryCodeStr);
            }
        }
        return minMetadataCountries;
    }

    private Map<String, Set<String>> findMultiCatalogLanguages() {

        // use default october sky namespace to check for countries that uses multi-lingual catalogs
        List<String> countries;
        if (config.isUseOctoberSkyForMultiLanguageCatalogCountries()) {

            if (config.getOctoberSkyNamespace() != null && !config.getOctoberSkyNamespace().isEmpty()) {
                return getMultiCatalogLanguages(octoberSky, config.getOctoberSkyNamespace(), "catalogs");
            } else {
                countries = octoberSky.getCountries().stream().map(c -> c.getCode()).collect(Collectors.toList());
            }

        } else {
            countries = Arrays.stream(config.getMultilanguageCatalogCountries().split(","))
                              .collect(Collectors.toList());
        }

        Map<String, Set<String>> multiLanguageCountryCatalogLocales = new HashMap<>();

        for (String country : countries) {
            Country octoberSkyCountry = octoberSky.getCountry(country);
            for (Catalog catalog : octoberSkyCountry.getCatalogs()) {

                if (catalog.hasLanguage()) {
                    Set<String> set = multiLanguageCountryCatalogLocales.get(country);
                    if (set == null) {
                        set = new HashSet<>();
                        multiLanguageCountryCatalogLocales.put(country, set);
                    }
                    set.add(catalog.getLanguage().getLanguage());
                }
            }
        }

        return multiLanguageCountryCatalogLocales;
    }

    private Map<String, Set<String>> getMultiCatalogLanguages(LaunchConfiguration launchConfiguration, String namespace, String property) {
        Map<String, Set<String>> tempMap = Maps.newHashMap();
        NamespaceLaunchConfiguration beehiveNS = launchConfiguration.forNamespace(namespace);
        for (Country country : beehiveNS.getCountries()) {
            String value = country.fetchProperty(property);
            Set<String> catalogLanguages = Sets.newHashSet();

            try {
                if (!value.isEmpty()) {

                    List<String> elements = om.readValue(value, new TypeReference<List<String>>() {
                    });
                    catalogLanguages = elements.stream()
                                               .map(Catalog::new)
                                               .map(Catalog::getLanguage)
                                               .filter(Objects::nonNull)
                                               .map(NFLocale::getName)
                                               .collect(Collectors.toSet());
                }
            } catch (Exception ex) {
                return tempMap;
            }

            tempMap.put(country.getCode(), catalogLanguages);
        }
        return tempMap;
    }

    private Map<String, LanguageCatalogMetadata> findLanguageVariants() {

        Map<String, LanguageCatalogMetadata> languageCatalogMetadataMap = new HashMap<>();
        for (Country country : octoberSky.getCountries()) {
            List<LanguageCatalogMetadata> languageCatalogMetadata = getLanguageVariants(country);
            if (!languageCatalogMetadata.isEmpty()) {
                List<Catalog> supportedCatalogs = country.getCatalogs();
                for (Catalog catalog : supportedCatalogs) {

                    // if catalog does not have language, then skip it.
                    if (!catalog.hasLanguage()) continue;

                    String language = catalog.getLanguage().getLanguage();
                    String countryString = country.getCode();

                    LanguageCatalogMetadata metadata = languageCatalogMetadata.stream()
                                                                              .filter(variant -> variant.getCatalogName().equals(language))
                                                                              .findFirst().orElse(null);
                    languageCatalogMetadataMap.put(countryString + COUNTRY_LANGUAGE_SEP + language, metadata);
                }
            }
        }
        return languageCatalogMetadataMap;
    }

    private List<LanguageCatalogMetadata> getLanguageVariants(Country country) {

        String value = country.fetchProperty(LANGUAGE_VARIANTS_COLUMN);
        try {

            if (value != null && !value.isEmpty()) {

                return Arrays.asList(om.readValue(value, LanguageCatalogMetadata[].class));

            } else if (value == null || value.isEmpty()) {
                LOGGER.warn("Language variants not found for countryCode={}", country.getCode());
            }

        } catch (Exception ex) {
            LOGGER.error("Failed to read language variants for countryCode={} msg={}, ignoring failure",
                    country.getCode(), ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

}