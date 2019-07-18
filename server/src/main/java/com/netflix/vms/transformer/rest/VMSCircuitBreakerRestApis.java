package com.netflix.vms.transformer.rest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.NetflixConfiguration;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.fastproperties.PersistedPropertiesUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/vms/circuitbreaker")
public class VMSCircuitBreakerRestApis {

	
	private final TransformerConfig transformerConfig;
	private final ElasticSearchClient elasticSearchClient;
	private final OctoberSkyData octoberSkyData;
	
	@Inject
	public VMSCircuitBreakerRestApis(TransformerConfig transformerConfig, 
			ElasticSearchClient elasticSearchClient,
			OctoberSkyData octoberSkyData) {
		this.transformerConfig = transformerConfig;
		this.elasticSearchClient = elasticSearchClient;
		this.octoberSkyData = octoberSkyData;
	}
	
	@GET
	@Path("/cycles")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCycles(@QueryParam("vip") String vipName) {
		// If vip name is null, use the default vip
		if(vipName == null)
			vipName = transformerConfig.getTransformerVip();
		
		// Get elastic search hostname for query
		String esHostName = getElasticSearchHostname();
		if(esHostName == null)
			return "[]";
		
		// Get the list of cycles for this vip
		List<String> cycles = VMSElasticSearchDataFetcher.getCycles(esHostName, vipName, 100);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		return gson.toJson(cycles);
	}
	
	@GET
	@Path("/status/{cbname}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCircuitBreakerStatus(@PathParam("cbname") String circuitBreakerName, 
			@QueryParam("vip") String vipName, 
			@QueryParam("cycleId") String cycleId,
			@QueryParam("size") String sizeStr) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> errorResponse = new HashMap<String, String>();
		// We are only supporting playback monkey as of now
		if(!circuitBreakerName.equals("playbackmonkey")) {
			errorResponse.put("error", "Getting status for " + circuitBreakerName + " not supported");
			return gson.toJson(errorResponse);
		}
		
		// If vipname is null, use the defualt vip
		if(vipName == null)
			vipName = transformerConfig.getTransformerVip();
		
		// Get elastic search hostname to query
		String esHostname = getElasticSearchHostname();
		if(esHostname == null)
			return "{}";
		
		// Determine the size
		int size = 1;
		if(sizeStr != null)
			size = Integer.parseInt(sizeStr);
		
		// If the size is greater than 5, defayult it to 5
		if(size > 5)
			size = 5;
		
		// Determine the cycles
		List<String> cycles = VMSElasticSearchDataFetcher.getCycles(esHostname, vipName, size + 1);
		
		// Ignore the first cycle
		cycles = cycles.subList(1, cycles.size());
		
		// If specific cycleId was specified, use it
		if(cycleId != null) {
			// Build the POJO
			CircuitBreakerStatus status = getPbmStatus(esHostname, vipName, cycleId);
			
			return gson.toJson(status, CircuitBreakerStatus.class);			
		} else {
			if(cycles.size() == 1) {
				CircuitBreakerStatus status = getPbmStatus(esHostname, vipName, cycles.get(0));
				return gson.toJson(status, CircuitBreakerStatus.class);
			} else {
				List<CircuitBreakerStatus> statuses = new ArrayList<>();
				for(String cycle : cycles) {
					CircuitBreakerStatus status = getPbmStatus(esHostname, vipName, cycle);
					statuses.add(status);
				}
				return gson.toJson(statuses);				
			}
		}
	}
	
	private CircuitBreakerStatus getPbmStatus(String esHostName, String vipName, String cycleId) {
		CircuitBreakerStatus status = new CircuitBreakerStatus();
		status.setVipName(vipName);
		status.setCycleId(cycleId);
		status.setCircuitBreakerName("playbackmonkey");
		
		// Get cycle status
		String cycleStatus = VMSElasticSearchDataFetcher.getCycleStatus(esHostName, vipName, cycleId);
		status.setCycleSuccess(cycleStatus);
		
		// Get pbm messages
		Map<String, List<String>> pbmMessages = VMSElasticSearchDataFetcher.getPlaybackMonkeyInfo(esHostName, vipName, cycleId);
		if(pbmMessages.containsKey("ERROR") && pbmMessages.get("ERROR").size() > 0)
			status.setCbSuccess(false);
		else
			status.setCbSuccess(true);
		status.setErrors(pbmMessages.get("ERROR"));
		status.setWarnings(pbmMessages.get("WARN"));
		
		// Extract failed videos
		Map<String, List<Integer>> failedVideos = new LinkedHashMap<String, List<Integer>>();
		if(pbmMessages.get("ERROR") != null && pbmMessages.get("ERROR").size() > 0) {
			for(String errorMessage : pbmMessages.get("ERROR")) {
				if(errorMessage.contains("FailedIds")) {
					failedVideos = getFailedVideoIds(errorMessage);
				}
			}
		}
		status.setFailedVideos(failedVideos);

		
		return status;
	}
	
	@GET
	@Path("/config/{cbname}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCircuitBreakerConfig(@PathParam("cbname") String circuitBreakerName) {
		Gson gson = new Gson();
		
		if(!circuitBreakerName.equals("playbackmonkey")) {
			Map<String, String> data = new HashMap<String, String>();			
			data.put("error", "Config data not available for: " + circuitBreakerName);
			return gson.toJson(data);
		}
		
		CircuitBreakerConfig config = new CircuitBreakerConfig();
		config.setVip(transformerConfig.getTransformerVip());
		config.setEnabled(transformerConfig.isPlaybackMonkeyEnabled());
		config.setAllowedOverrides(getListOfCountries());
		config.setEnabledOverrides(getCommaSeparatedStringToList(transformerConfig.getPlaybackMonkeyTestForCountries()));
		config.setDisabledOverrides(new ArrayList<String>());
		config.setExclusions(getVideoCountryExclusions());
		
		return gson.toJson(config, CircuitBreakerConfig.class);
	}
	
	@POST
	@Path("/config/{cbname}")
	@Consumes(MediaType.TEXT_PLAIN)
	public String updateCircuitBreakerConfig(@PathParam("cbname") String circuitBreakerName, String requestData) {
		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();
		
		// Currently only enabled for playback monkey
		if(!circuitBreakerName.equals("playbackmonkey")) {
			data.put("error", "Updating config for " + circuitBreakerName + " not supported");
			return gson.toJson(data);
		}

		// Parse the incoming request data (JSON)
		JsonElement jelement = new JsonParser().parse(requestData);
		JsonObject jobject = jelement.getAsJsonObject();
		
		// If we need to toggle enabled bit of this 
		if(jobject.has("enabled")) {
			boolean enabled = jobject.get("enabled").getAsBoolean();
			try {
				enableOrDisablePlaybackMonkey(enabled);
			} catch(Exception e) {
				e.printStackTrace();
				data.put("error", e.getMessage() + ": error setting enabled = " + enabled + "for playbackmonkey");
				return gson.toJson(data);
			}
			
		}
		
		// If we need to update the countries for which playback monkey is enabled
		if(jobject.has("enabledOverrides")) {
			JsonArray enabledCountries = jobject.get("enabledOverrides").getAsJsonArray();
			List<String> countries = new ArrayList<String>();
			for(int i = 0; i < enabledCountries.size(); i++) {
				String country = enabledCountries.get(i).getAsString();
				countries.add(country);
			}
			try {
				updatePlaybackMonkeyCountries(countries);
			} catch(Exception e) {
				e.printStackTrace();
				data.put("error", "Error creating fast property vms.playbackMonkeyTestForCountries with value : " 
				+ countries.toString());
				return gson.toJson(data);
			}
		}
		
		// If we need to update the video country exclusions
		if(jobject.has("exclusions")) {
			JsonObject exclusionObject = jobject.get("exclusions").getAsJsonObject();
			String exclusionStr = getExclusionStringFromJson(exclusionObject);
			try {
				PersistedPropertiesUtil.upsertProperty("vms.playbackMonkeyVideoCountryToExclude", 
						exclusionStr, 
						"vmstransformer", 
						NetflixConfiguration.getEnvironmentEnum(), 
						NetflixConfiguration.getRegionEnum(), 
						null, 
						transformerConfig.getTransformerVip(), 
						null, 
						null, 
						null);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private Map<String, List<Integer>> getFailedVideoIds(String message) {
		Map<String, List<Integer>> data = new LinkedHashMap<String, List<Integer>>();
		String substring = (message.substring(message.indexOf("FailedIds=") + "FailedId=".length() + 1));
		List<String> countryVideoIds = new ArrayList<String>();
		int i = substring.indexOf('[');
		int j = substring.indexOf(']');
		while(i != -1 && j != -1) {
			countryVideoIds.add(substring.substring(i + 1, j));
			substring = substring.substring(j + 1);
			i = substring.indexOf('[');
			j = substring.indexOf(']');
		}
		
		for(String countryVideoId : countryVideoIds) {
			countryVideoId = countryVideoId.trim();
			String[] tokens = countryVideoId.split(",");
			String  country = tokens[0].trim();
			int videoId = Integer.parseInt(tokens[1].trim());
			if(!data.containsKey(country)) {
				data.put(country, new ArrayList<Integer>());
			}
			data.get(country).add(videoId);
		}
		
		
		return data;
	}

	
	/**
	 * Construct the exclusion string from the incoming JSON
	 * @param obj Parsed Json object referring to exclusion list
	 * @return Fast property string of the format <country>:[videoIds];<country>:[videoIds]
	 */
	private String getExclusionStringFromJson(JsonObject obj) {
		// First collect all the countries
		Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
		
		// Return a space instead of empty string, fast property util complains if it is an empty string
		if(entries.size() == 0)
			return " ";
		
		// Iterate over the keys
		List<String> exclusionCountries = new ArrayList<String>();
		for(Map.Entry<String, JsonElement> entry : entries) {
			String country = entry.getKey();
			exclusionCountries.add(country);
		}
		
		// Get all the countries
		List<String> exclusions = new ArrayList<String>();
		for(String country : exclusionCountries) {
			JsonArray videoIds = obj.get(country).getAsJsonArray();
			// If there are no videos for this country in the exclusion list, ignore it
			if(videoIds.size() == 0) continue;
			List<Integer> videos = new ArrayList<Integer>();
			for(int i = 0; i < videoIds.size(); i++)
				videos.add(videoIds.get(i).getAsInt());
			String commaSeparatedVideoIds = Joiner.on(",").join(videos);
			exclusions.add(country + ":" + commaSeparatedVideoIds);
		}
		
		return Joiner.on(";").join(exclusions);
	}
	
	/**
	 * Get the elastic search host name for querying
	 * @return
	 */
	private String getElasticSearchHostname() {
		if(!transformerConfig.isElasticSearchLoggingEnabled())
			return null;
		Set<InstanceInfo> instances = elasticSearchClient.getElasticSearchClientBridge().getInstances();
		if(instances.size() > 0) {
			String hostname = instances.iterator().next().getHostName();
			return hostname;
		} else {
			return null;
		}
	}
	
	// Gets the list of countries
	private List<String> getListOfCountries() {
		Set<String> countries = octoberSkyData.getSupportedCountries();
		List<String> ret = new ArrayList<String>();
		ret.add("GLOBAL");
		ret.addAll(countries);
		return ret;
	}
	
	// Convert comma separated string to list of strings
	private List<String> getCommaSeparatedStringToList(String s) {
		return Splitter.on(',').trimResults().omitEmptyStrings().splitToList(s);
	}
	
	// Parse the playback monkey exclusions and convert it into a map of string to list of string
	private Map<String, List<String>> getVideoCountryExclusions() {
		String exclusionString = transformerConfig.getPlaybackMonkeyVideoCountryToExclude().trim();
		
		Map<String, List<String>> exclusions = new HashMap<String, List<String>>();
		
		// If the String is empty or null return
		if(exclusionString == null || exclusionString.length() == 0)
			return exclusions;
		
		// Tokenize it on semi colon
		String[] videoCountryTokens = exclusionString.split(";");
		for(String videoCountryToken : videoCountryTokens) {
			// Each token is of the form
			// country : <comma separated videoIds>
			String[] tokens = videoCountryToken.split(":");
			String country = tokens[0];
			String commaSeparatedVideoIds = tokens[1];
			List<String> videoIds = getCommaSeparatedStringToList(commaSeparatedVideoIds);
			exclusions.put(country, videoIds);
		}
		
		return exclusions;
	}
	
	// Enable or disable playback monkey for this Vip
	private void enableOrDisablePlaybackMonkey(boolean enabled) throws IOException {
		PersistedPropertiesUtil.upsertProperty("vms.playbackMonkeyEnabled", 
				Boolean.valueOf(enabled).toString(), 
				"vmstransformer", 
				NetflixConfiguration.getEnvironmentEnum(), 
				NetflixConfiguration.getRegionEnum(), 
				null, 
				transformerConfig.getTransformerVip(), 
				null, 
				null, 
				null);
	}
	
	// Update playback monkey enabled country for this Vip
	private void updatePlaybackMonkeyCountries(List<String> countries) throws IOException {
		String value = "";
		// First convert the list of countries into comma separated list of countries
		if(countries != null && countries.size() > 0)
			value = Joiner.on(",").join(countries);

		PersistedPropertiesUtil.upsertProperty("vms.playbackMonkeyTestForCountries", 
				value, 
				"vmstransformer", 
				NetflixConfiguration.getEnvironmentEnum(), 
				NetflixConfiguration.getRegionEnum(), 
				null, 
				transformerConfig.getTransformerVip(), 
				null, 
				null, 
				null);
	}
}
