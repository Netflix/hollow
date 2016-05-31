package com.netflix.vms.transformer.rest;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import javax.ws.rs.QueryParam;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.util.TransformerServerCassandraHelper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.archaius.api.Config;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.fastproperties.PersistedPropertiesUtil;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.HollowCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowBlobCircuitBreakerJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/vms/cb")
@Singleton
public class VMSCircuitBreakerAdmin {

    private static final String APP_ID = "vmstransformer";

    private final TransformerConfig transformerConfig;
    private final Config config;
    private final TransformerServerCassandraHelper cassandraHelper;

    private final String vip;

    @Inject
    public VMSCircuitBreakerAdmin(TransformerConfig transformerConfig, Config config, TransformerPlatformLibraries platformLibs) {
        this.transformerConfig = transformerConfig;
        this.config = config;
        this.cassandraHelper = new TransformerServerCassandraHelper(platformLibs.getAstyanax(), "cass_dpt", "hollow_publish_workflow", "hollow_validation_stats");
        this.vip = transformerConfig.getTransformerVip();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String show() {
        Map<String, String> circuitBreakerProperties = new HashMap<>();

        circuitBreakerProperties.put("vms.circuitBreakersEnabled", String.valueOf(transformerConfig.isCircuitBreakersEnabled()));

        for (String circuitBreakerName : getCircuitBreakerNames()) {
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
        while (keyIter.hasNext()) {
            String propertyName = keyIter.next();
            circuitBreakerProperties.put(propertyName, config.getString(propertyName));
        }
    }

    @POST
    @Path("/createorupdate")
    public String createOrUpdateFastProperty(@FormParam("key") String key, @FormParam("value") String value) {
        RegionEnum region = RegionEnum.toEnum(transformerConfig.getAwsRegion());
        EnvironmentEnum env = EnvironmentEnum.toEnum(transformerConfig.getNetflixEnvironment());
        String vip = transformerConfig.getTransformerVip();

        try {
            if (PersistedPropertiesUtil.fastPropertyExists(key, APP_ID, env, region, null, vip, null)) {
                PersistedPropertiesUtil.updateFastProperty(key, value, APP_ID, env, region, null, vip, null);
                return "Updated property: " + key;
            } else {
                PersistedPropertiesUtil.createFastProperty(key, value, APP_ID, env, region, null, vip, null);
                return "Created Property: " + key;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

    }

    @POST
    @Path("/delete")
    public String deletefastProperty(@FormParam("key") String key) {
        RegionEnum region = RegionEnum.toEnum(transformerConfig.getAwsRegion());
        EnvironmentEnum env = EnvironmentEnum.toEnum(transformerConfig.getNetflixEnvironment());
        String vip = transformerConfig.getTransformerVip();

        try {
            if (PersistedPropertiesUtil.fastPropertyExists(key, APP_ID, env, region, null, vip, null)) {
                PersistedPropertiesUtil.deleteFastProperty(key, APP_ID, env, region, null, vip, null);
                return "Deleted: " + key;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

        return "NotFound: " + key;
    }

    @GET
	@Path("/reset")
	public String resetCircuitBreakerBaseline(@QueryParam("name") String circuitBreakerName, @QueryParam("country") String country) throws ConnectionException {
        String cassandraRowName = country == null ? circuitBreakerName : circuitBreakerName + "_" + country;

        cassandraHelper.deleteVipKeyValuePair(vip, cassandraRowName);
		
		return "Deleted Cassandra Row: " + cassandraRowName; 
	}

    private List<String> getCircuitBreakerNames() {
        // / adding items to the below list will allow them to show up in the UI
        // *before* the first
        // / cycle has completed.
        List<String> circuitBreakerNames = new ArrayList<String>();

        for (HollowCircuitBreaker circuitBreaker : HollowBlobCircuitBreakerJob.createCircuitBreakerRules(null, -1L, -1L)) {
            circuitBreakerNames.add(circuitBreaker.getRuleName());
        }

        Collections.sort(circuitBreakerNames);

        return circuitBreakerNames;
    }
}
