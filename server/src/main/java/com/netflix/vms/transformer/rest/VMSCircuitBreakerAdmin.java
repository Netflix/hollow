package com.netflix.vms.transformer.rest;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.archaius.api.Config;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowBlobCircuitBreakerJob;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/vms/cb")
@Singleton
public class VMSCircuitBreakerAdmin {

	private final TransformerConfig transformerConfig;
	private final Config config;
	
	@Inject
	public VMSCircuitBreakerAdmin(TransformerConfig transformerConfig, Config config) {
		this.transformerConfig = transformerConfig;
		this.config = config;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String show() {
		Map<String, String> circuitBreakerProperties = new HashMap<String, String>();
		
		circuitBreakerProperties.put("vms.circuitBreakersEnabled", String.valueOf(transformerConfig.isCircuitBreakersEnabled()));
		
		for(String circuitBreakerName : HollowBlobCircuitBreakerJob.CIRCUIT_BREAKER_NAMES) {
			String enabledPropertyName = "vms.circuitBreakerEnabled." + circuitBreakerName;
			String thresholdPropertyName = "vms.circuitBreakerThreshold." + circuitBreakerName;
			
			circuitBreakerProperties.put(enabledPropertyName, String.valueOf(transformerConfig.isCircuitBreakerEnabled(circuitBreakerName)));
			circuitBreakerProperties.put(thresholdPropertyName, String.valueOf(transformerConfig.getCircuitBreakerThreshold(circuitBreakerName)));

			addPrefixedProperties(circuitBreakerProperties, enabledPropertyName);
			addPrefixedProperties(circuitBreakerProperties, thresholdPropertyName);
		}

		// Convert this into JSON
		Gson gson = new Gson();
		return gson.toJson(circuitBreakerProperties);

	}

	private void addPrefixedProperties(Map<String, String> circuitBreakerProperties, String prefix) {
		Iterator<String> keyIter = config.getKeys(prefix);
		while(keyIter.hasNext()) {
			String propertyName = keyIter.next();
			circuitBreakerProperties.put(propertyName, config.getString(propertyName));
		}
	}
}
