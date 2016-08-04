package com.netflix.vms.transformer.fastproperties;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import java.io.IOException;

public class ClientPinningUtil {

    private static final String propertyNameSuffix = "com.netflix.vmsconfig.pin.version";
    private static final String hermesAnnouncementPropertyNameSuffix = "hermesns.vms.hollow.blob";
    
    
    public static String getLastAnnouncedVersion(String vipName, RegionEnum region) throws IOException {
    	String key = hermesAnnouncementPropertyNameSuffix + "." + vipName;
    	String json = PersistedPropertiesUtil.getFastPropertyValue(key, 
			    			null, 
			    			NetflixConfiguration.getEnvironmentEnum(), 
			    			region, 
			    			null, 
			    			null, 
			    			null);
		JsonElement jelement =  new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		String dataStringJson = jobject.get("dataString").getAsString();
		// Parse it again
		jelement = new JsonParser().parse(dataStringJson);
		String version = jelement
							.getAsJsonObject()
							.get("attributes")
							.getAsJsonObject()
							.get("dataVersion")
							.getAsString();
    	
    	return version;
    }

    public static void unpinClients(String vipName, RegionEnum region) throws IOException {

        // Figure out the key of the property
        String key = propertyNameSuffix + "." + vipName;
        PersistedPropertiesUtil.deleteFastProperty(key,
                null,                                         // no appId
                NetflixConfiguration.getEnvironmentEnum(),
                region,
                null,                                         // no serverId
                null,                                         // no stack
                null);                                        // no countries
    }

    /**
     * Pins client in a vip to a specific version.
     * Pinning is done by creating / updating fast properties through odin
     * @param vipName
     * @param blobVersion
     * @throws PersistedPropertiesException
     */
    public static void pinClients(String vipName, String blobVersion, RegionEnum region) throws IOException {

        // Determine if the key already exists
        String key = propertyNameSuffix + "." + vipName;
        EnvironmentEnum env = NetflixConfiguration.getEnvironmentEnum();
        boolean exists = PersistedPropertiesUtil.fastPropertyExists(key, null, env, region, null, null, null);
        if(exists) {
            // Update property
            PersistedPropertiesUtil.updateFastProperty(key, blobVersion, null, env, region, null, null, null);
        } else {
            // create property
            PersistedPropertiesUtil.createFastProperty(key, blobVersion, null, env, region, null, null, null);
        }

    }


    public static String getPinnedVersion(String vipName, RegionEnum region) throws IOException {
        String key = propertyNameSuffix + "." + vipName;
        EnvironmentEnum env = NetflixConfiguration.getEnvironmentEnum();
        if(!PersistedPropertiesUtil.fastPropertyExists(key, null, env, region, null, null, null))
            return null;
        return PersistedPropertiesUtil.getFastPropertyValue(key, null, env, region, null, null, null);
    }


}