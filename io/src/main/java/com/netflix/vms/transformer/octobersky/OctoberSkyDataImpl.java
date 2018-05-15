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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

@Singleton
public class OctoberSkyDataImpl implements OctoberSkyData {

    private static final String BEEHIVE_NAMESPACE = "beehive";
    private static final String IS_MIN_METADATA_PRESENT = "isMinMetadataPresent";

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
        return multilanguageCountryCatalogLocales.get(country);
    }

    @Override
    public Set<String> getMultiLanguageCatalogCountries() {
        return multilanguageCountryCatalogLocales.keySet();
    }

    // refresh is called every cycle begin, so each cycle has a consistent view of data
    @Override
    public void refresh() {
        this.supportedCountries = findCountriesWithMinMetadata(octoberSky);
        this.multilanguageCountryCatalogLocales = findMultilanguageCountryCatalogLocales(config, octoberSky);
    }

    private static Set<String> findCountriesWithMinMetadata(LaunchConfiguration octoberSky) {
        Set<String> minMetadataCountries = new HashSet<String>();

        NamespaceLaunchConfiguration beehiveNamespace = octoberSky.forNamespace(BEEHIVE_NAMESPACE);
        for (String cStr : octoberSky.getCountryCodes()) {
            String countryCodeStr = cStr.trim();
            Country country = beehiveNamespace.getCountry(countryCodeStr);
            if (country != null && Boolean.valueOf(country.fetchProperty(IS_MIN_METADATA_PRESENT))) { minMetadataCountries.add(countryCodeStr); }
        }
        return minMetadataCountries;
    }

    private static Map<String, Set<String>> findMultilanguageCountryCatalogLocales(TransformerConfig config, LaunchConfiguration octoberSky) {

        // use default october sky namespace to check for countries that uses multi-lingual catalogs
        List<String> countries;
        if (config.isUseOctoberSkyForMultiLanguageCatalogCountries()) {

            if (!config.getOctoberSkyNamespace().isEmpty()) {
                return getMultiCatalogLanguages(octoberSky, config.getOctoberSkyNamespace(), "catalogs");
            } else {
                countries = octoberSky.getCountries().stream().map(c -> c.getCode()).collect(Collectors.toList());
            }

        } else {
            countries = Arrays.stream(config.getMultilanguageCatalogCountries().split(",")).collect(Collectors.toList());
        }

        Map<String, Set<String>> multilanguageCountryCatalogLocales = new HashMap<>();

        for (String country : countries) {
            Country octoberSkyCountry = octoberSky.getCountry(country);
            for (Catalog catalog : octoberSkyCountry.getCatalogs()) {

                if (catalog.hasLanguage()) {
                    Set<String> set = multilanguageCountryCatalogLocales.get(country);
                    if (set == null) {
                        set = new HashSet<>();
                        multilanguageCountryCatalogLocales.put(country, set);
                    }
                    set.add(catalog.getLanguage().getLanguage());
                }
            }
        }

        return multilanguageCountryCatalogLocales;
    }

    private static Map<String, Set<String>> getMultiCatalogLanguages(LaunchConfiguration launchConfiguration, String namespace, String
            property) {
        Map<String, Set<String>> tempMap = Maps.newHashMap();
        ObjectMapper om = new ObjectMapper();
        NamespaceLaunchConfiguration beehiveNS = launchConfiguration.forNamespace(namespace);
        for (Country country : beehiveNS.getCountries()) {
            String value = country.fetchProperty(property);
            Set<String> catalogLanguages = Sets.newHashSet();

            try {
                if (StringUtils.isNotEmpty(value)) {

                    List<String> elements = om.readValue(value, new TypeReference<List<String>>() {});
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

}
