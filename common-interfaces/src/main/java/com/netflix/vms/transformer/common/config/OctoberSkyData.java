package com.netflix.vms.transformer.common.config;

import java.util.Set;

public interface OctoberSkyData {

	Set<String> getSupportedCountries();
	
    Set<String> getCatalogLanguages(String country);

    Set<String> getMultiLanguageCatalogCountries();

    /**
     * Look-up a set of language-variants/locales that are present in the given country and language catalog.
     * Example: catalog "HK:zh" supports following variants/locales "zh-Hant", "yue" and "zh"
     */
    Set<String> getLanguageVariants(String country, String language);

    /**
     * Look up additional supported language-variants
     */
    Set<String> getOtherLanguageVariants(String country, String language);
    
    void refresh();

}
