package com.netflix.vms.transformer.fastproperties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ClientPinningUtil {

    private static final String propertyNameSuffix = "com.netflix.vmsconfig.pin.version";
    
	private static String discoveryUrl = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/gutenbergservice-consumer";
	private static String urlPath = "/REST/consume-service/publish/info/vms.hollow.blob.";

	public static Map<RegionEnum, String> getAnnouncedVersions(String vip) {
		CloseableHttpClient httpClient  = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpGet get = new HttpGet(discoveryUrl + urlPath + vip);
			CloseableHttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity);
			return consumeJson(json);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();				
			} catch(Exception e) {
				// ignore
			}
		}
		
		// Return a version
		return null;
	}


    public static void unpinClients(String vipName, RegionEnum region) throws IOException {
    	String key = propertyNameSuffix + "." + vipName;
    	EnvironmentEnum env = NetflixConfiguration.getEnvironmentEnum();
		PersistedPropertiesUtil.deleteProperty(key, null, env, region, null, null, null, null, null);
		String keyNoStreams = propertyNameSuffix + "." + vipName + "_nostreams";
		PersistedPropertiesUtil.deleteProperty(keyNoStreams, null, env, region, null, null, null, null, null);

		// after deleting this FP, announcement watcher will get a null value
		// null values are ignored, resulting in unpin action
		String newFP = "hollow.pin.vms-" + vipName;
		PersistedPropertiesUtil.deleteProperty(newFP, null, env, region, null, null, null, null, null);

		String noStreamsFP = "hollow.pin.vms-" + vipName + "_nostreams";
		PersistedPropertiesUtil.deleteProperty(noStreamsFP, null, env, region, null, null, null, null, null);
    }

    /**
     * Pins client in a vip to a specific version.
     * Pinning is done by creating / updating fast properties through odin
     * @param vipName
     * @param blobVersion
     * @throws IOException
     */
    public static void pinClients(String vipName, String blobVersion, RegionEnum region) throws IOException {

    	String key = propertyNameSuffix + "." + vipName;
        pinUsingFastProperty(key, blobVersion, region);

        String keyNoStreams = propertyNameSuffix + "." + vipName + "_nostreams";
        pinUsingFastProperty(keyNoStreams, blobVersion, region);

        // This FP is used in announcement watcher to look for pinned versions.
		// VMS-client is transitioning to use HollowConsumer.
		// Choosing to use FP style pinning, since Gutenberg API's do not support including region in the scope for pin topics.
        String newFP = "hollow.pin.vms-" + vipName;
        pinUsingFastProperty(newFP, blobVersion, region);

		String noStreamsFP = "hollow.pin.vms-" + vipName + "_nostreams";
		pinUsingFastProperty(noStreamsFP, blobVersion, region);
    }

    private static void pinUsingFastProperty(String key, String value, RegionEnum region) throws IOException {
		EnvironmentEnum env = NetflixConfiguration.getEnvironmentEnum();
		PersistedPropertiesUtil.upsertProperty(key, value, null, env, region, null, null, null, null, null);
	}


    public static String getPinnedVersion(String vipName, RegionEnum region) throws IOException {
    	String key = propertyNameSuffix + "." + vipName;
    	EnvironmentEnum env = NetflixConfiguration.getEnvironmentEnum();
    	if(!PersistedPropertiesUtil.propertyExists(key, null, env, region, null, null, null, null, null))
    		return null;
    	return PersistedPropertiesUtil.getPropertyValue(key, null, env, region, null, null, null, null, null);
    }

	private static Map<RegionEnum, String> consumeJson(String json) {
		Map<RegionEnum, String> announcedVersions = new Hashtable<RegionEnum, String>();
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		JsonArray publishInfos = jsonObject.get("topicPublishInfos").getAsJsonArray();
		for(int i = 0; i < publishInfos.size(); i++) {
			JsonObject publishInfo = publishInfos.get(i).getAsJsonObject();
			// Extract region
			JsonObject publishTag = publishInfo.get("publishTag").getAsJsonObject();
			String publishTagValue = publishTag.get("value").getAsString();
			String regionStr = getRegionFromPublishTag(publishTagValue);
			RegionEnum region = RegionEnum.toEnum(regionStr);
			
			// Extract announced version
			JsonObject metadata = publishInfo.get("metadata").getAsJsonObject();
			String hermesDataPointerJson = metadata.get("__hermes_datapointer__").getAsString();
			String version = getDataVersionFromHermesDataPointer(hermesDataPointerJson);
			
			announcedVersions.put(region, version);
		}
		return announcedVersions;
	}
	
	private static String getDataVersionFromHermesDataPointer(String json) {
		JsonObject dataPointer = new JsonParser().parse(json).getAsJsonObject();
		String dataString = dataPointer.get("dataString").getAsString();
		
		JsonObject data = new JsonParser().parse(dataString).getAsJsonObject();
		JsonObject dataAttributes = data.get("attributes").getAsJsonObject();
		String dataVersion = dataAttributes.get("dataVersion").getAsString();
		
		return dataVersion;
	}
	
	private static String getRegionFromPublishTag(String tagValue) {
		String[] tokens = tagValue.split("\\|");
		return tokens[1];
	}


}