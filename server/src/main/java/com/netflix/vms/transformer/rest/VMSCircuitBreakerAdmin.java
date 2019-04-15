package com.netflix.vms.transformer.rest;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.CIRCUITBREAKER_STATS;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.archaius.api.Config;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.fastproperties.PersistedPropertiesUtil;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.HollowCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.job.impl.HollowBlobCircuitBreakerJob;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/vms/cb")
public class VMSCircuitBreakerAdmin {

    private static final String APP_ID = "vmstransformer";

    private final TransformerConfig transformerConfig;
    private final Config config;
    private final TransformerCassandraColumnFamilyHelper cassandraHelper;
    private final OctoberSkyData ocData;

    private final String vip;

    @Inject
    public VMSCircuitBreakerAdmin(TransformerConfig transformerConfig, Config config, OctoberSkyData ocData, TransformerCassandraHelper cassandraHelper) {
        this.transformerConfig = transformerConfig;
        this.config = config;
        this.cassandraHelper = cassandraHelper.getColumnFamilyHelper(CIRCUITBREAKER_STATS);
        this.vip = transformerConfig.getTransformerVip();
        this.ocData = ocData;
    }
    
    @GET
    @Path("/countries")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSupportedCountries() {
    	Set<String> countries = ocData.getSupportedCountries();
    	return new Gson().toJson(countries);
    }
    
    @GET
    @Path("/names")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCircuitBreakers() {
        Map<String, String> circuitBreakerNameToIsCountrySpecific = new HashMap<>();
        
        for(HollowCircuitBreaker circuitBreaker : getCircuitBreakerJobs()) {
            circuitBreakerNameToIsCountrySpecific.put(circuitBreaker.getRuleName(), String.valueOf(circuitBreaker.isCountrySpecific()));
        }
        
        return new Gson().toJson(circuitBreakerNameToIsCountrySpecific);
    }

    @GET
    @Path("/properties")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCircuitBreakerPropertyValues() {
        Map<String, String> circuitBreakerProperties = new HashMap<>();

        circuitBreakerProperties.put("vms.circuitBreakersEnabled", String.valueOf(transformerConfig.isCircuitBreakersEnabled()));

        for (HollowCircuitBreaker circuitBreaker : getCircuitBreakerJobs()) {
            String circuitBreakerName = circuitBreaker.getRuleName();
            
            String enabledPropertyName = "vms.circuitBreakerEnabled." + circuitBreakerName;
            String thresholdPropertyName = "vms.circuitBreakerThreshold." + circuitBreakerName;

            circuitBreakerProperties.put(enabledPropertyName, String.valueOf(transformerConfig.isCircuitBreakerEnabled(circuitBreakerName)));
            circuitBreakerProperties.put(thresholdPropertyName, String.valueOf(transformerConfig.getCircuitBreakerThreshold(circuitBreakerName)));

            addPrefixedProperties(circuitBreakerProperties, enabledPropertyName);
            addPrefixedProperties(circuitBreakerProperties, thresholdPropertyName);
        }

        // Convert this into JSON
        return new Gson().toJson(circuitBreakerProperties);
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

    @POST
	@Path("/reset")
	public String resetCircuitBreakerBaseline(@FormParam("name") String circuitBreakerName, @FormParam("country") String country) throws ConnectionException {
        String cassandraRowName = country == null ? circuitBreakerName : circuitBreakerName + "_" + country;

        cassandraHelper.deleteVipKeyValuePair(vip, cassandraRowName);
		
		return "Deleted Cassandra Row: " + cassandraRowName; 
	}

    private List<HollowCircuitBreaker> getCircuitBreakerJobs() {
        List<HollowCircuitBreaker> circuitBreakers = Arrays.asList(
                HollowBlobCircuitBreakerJob.createCircuitBreakerRules(null,
                        transformerConfig.getTransformerVip(), -1L, -1L));
        circuitBreakers.sort(Comparator.comparing(HollowCircuitBreaker::getRuleName));
        return circuitBreakers;
    }
}
