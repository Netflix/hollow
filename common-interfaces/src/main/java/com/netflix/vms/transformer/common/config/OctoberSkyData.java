package com.netflix.vms.transformer.common.config;

import java.util.Set;

public interface OctoberSkyData {

    Set<String> getMultiLingualCountries();

	public Set<String> getSupportedCountries();
	
    public Set<String> getCatalogLanguages(String country);
    
    public void refresh();

}
