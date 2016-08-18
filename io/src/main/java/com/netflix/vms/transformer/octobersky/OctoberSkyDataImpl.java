package com.netflix.vms.transformer.octobersky;

import com.netflix.launch.common.Catalog;
import java.util.HashMap;
import java.util.Map;
import com.netflix.vms.transformer.common.config.TransformerConfig;
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
    public Set<String> getMultilanguageCatalogLocalesForCountry(String country) {
        return multilanguageCountryCatalogLocales.get(country);
    }
    
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
            if (country != null && Boolean.valueOf(country.fetchProperty(IS_MIN_METADATA_PRESENT)))
                minMetadataCountries.add(countryCodeStr);
        }
        return minMetadataCountries;
    }
    
    private static Map<String, Set<String>> findMultilanguageCountryCatalogLocales(TransformerConfig config, LaunchConfiguration octoberSky) {
        String multilangCountries[] = config.getMultilanguageCatalogCountries().split(",");
        Map<String, Set<String>> multilanguageCountryCatalogLocales = new HashMap<>();
        NamespaceLaunchConfiguration beehiveNamespace = octoberSky.forNamespace(BEEHIVE_NAMESPACE);
        
        for(String country : multilangCountries) {
            Country octoberSkyCountry = beehiveNamespace.getCountry(country);
            for(Catalog catalog : octoberSkyCountry.getCatalogs()) {
                
                if(catalog.hasLanguage()) {
                    Set<String> set = multilanguageCountryCatalogLocales.get(country);
                    if(set == null) {
                        set = new HashSet<>();
                        multilanguageCountryCatalogLocales.put(country, set);
                    }
                    set.add(catalog.getLanguage().getLanguage());
                }
            }
        }
        
        return multilanguageCountryCatalogLocales;
    }
    
}
