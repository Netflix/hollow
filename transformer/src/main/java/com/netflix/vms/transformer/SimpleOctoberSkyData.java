package com.netflix.vms.transformer;

import java.util.Collections;

import java.util.Set;
import com.netflix.vms.transformer.common.config.OctoberSkyData;

public class SimpleOctoberSkyData implements OctoberSkyData {

	public static SimpleOctoberSkyData INSTANCE = new SimpleOctoberSkyData();
	
	private SimpleOctoberSkyData() { }
	
	@Override
	public Set<String> getSupportedCountries() {
		return Collections.singleton("US");
	}

}
