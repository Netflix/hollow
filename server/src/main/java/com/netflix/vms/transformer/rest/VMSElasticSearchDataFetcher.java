package com.netflix.vms.transformer.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Collection of static methods to get data from elastic search regarding vmsserver
 * 
 * @author jatins
 *
 */
public class VMSElasticSearchDataFetcher {

	/**
	 * Gets the last completed cycle for a particula vip
	 * @param esHostName 
	 * @param vipName
	 * @return cycleId as a string, null if no complete cycle found
	 */
	public static String getLastCycle(String esHostName, String vipName) {
		// Get list of indices for this vip
		List<String> indices = getIndices(esHostName, vipName);
		
		String cycleId = null;
		for(String index : indices) {
			cycleId = getLastCompletedCycle(esHostName, index);
			if(cycleId != null)
				break;
		}
		
		return cycleId;
	}
	
	/**
	 * Query the daily cycle index and get the last successful cycle
	 * @param esHostName
	 * @param index
	 * @return
	 */
	private static String getLastCompletedCycle(String esHostName, String index) {
		URI uri = null;
		try {
			uri =new URIBuilder()
				.setScheme("http")
				.setHost(esHostName)
				.setPort(7104)
				.setPath("/" + index + "/vmsserver/_search")
				.addParameter("q", "eventInfo.tag:WaitForNextCycle")
				.addParameter("size", "720")
				.addParameter("sort", "eventInfo.timestamp:desc")
				.addParameter("pretty", "true")
				.build();
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		String responseJson = executeHttpGet(uri);
		if(responseJson != null)
			return getLastCycleIdFromJson(responseJson);
		
		return null;
	}
	
	/**
	 * From the JSON extract the last cycle which finished
	 * @param json
	 * @return
	 */
	private static String getLastCycleIdFromJson(String json) {
		JsonObject searchResult = new JsonParser().parse(json).getAsJsonObject();
		JsonObject hits = searchResult.get("hits").getAsJsonObject();
		int searchResultCount = hits.get("total").getAsInt();
		if(searchResultCount == 0)
			return null;
		JsonArray cycles = hits.get("hits").getAsJsonArray();
		JsonObject result = cycles.get(0).getAsJsonObject();
		JsonObject lastCycleInfo = result.get("_source").getAsJsonObject().get("eventInfo").getAsJsonObject();
		return lastCycleInfo.get("currentCycle").getAsString();
	}

	
	/**
	 * Return the list of indices which can be queryied later for a particular vipname
	 * Returns all the indices the elastic search knows about for this vip
	 * @param esHostName elastic search hostname
	 * @param vipName vipname
	 * @return list of indices. (empty arraylist if no indices found, or if there was an error fetching
	 * data from elastic search)
	 */
	private static List<String> getIndices(String esHostName, String vipName) {
		// Build Url
		URI uri = null;
		try {
			uri = new URIBuilder()
						.setScheme("http")
						.setHost(esHostName)
						.setPort(7104)
						.setPath("/_stats")
						.build();			
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		String responseJson = executeHttpGet(uri);
		if(responseJson != null)
			return getIndicesForVip(responseJson, vipName);
		
		return new ArrayList<String>();
	}
	
	
	

	public static String getCycleStatus(String esHostName, String vipName, String cycleId) {
		URI successUri = null;
		URI failedUri = null;
		try {
			successUri = new URIBuilder()
					.setScheme("http")
					.setHost(esHostName)
					.setPort(7104)
					.setPath("/vms-" + vipName + "-cyc_" + cycleId.substring(0, 8) +"/_search")
					.addParameter("q", "eventInfo.currentCycle:" + cycleId + " AND eventInfo.tag:TransformCycleSuccess")
					.build();
			failedUri = new URIBuilder()
					.setScheme("http")
					.setHost(esHostName)
					.setPort(7104)
					.setPath("/vms-" + vipName + "-cyc_" + cycleId.substring(0, 8) +"/_search")
					.addParameter("q", "eventInfo.currentCycle:" + cycleId + " AND eventInfo.tag:TransformCycleFailed")
					.build();
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		String successJson = executeHttpGet(successUri);
		if(successJson != null) {
			int hits = getHitsCountFromJson(successJson);
			if(hits > 0)
				return "success";
		}
		
		String failureJson = executeHttpGet(failedUri);
		if(failureJson != null) {
			int hits = getHitsCountFromJson(failureJson);
			if(hits > 0)
				return "failed";
		}
		
		return "unknown";
	}
	
	
	
	public static Map<String, List<String>> getPlaybackMonkeyInfo(String esHostName, String vipName, String cycleId) {
		URI uri = null;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost(esHostName)
					.setPort(7104)
					.setPath("/vms-" + vipName + "-cyc_" + cycleId.substring(0, 8) +"/_search")
					.addParameter("q", "eventInfo.currentCycle:" + cycleId + " AND playbackmonkey")
					.addParameter("size", "200")
					.addParameter("sort", "eventInfo.timestamp:asc")
					.addParameter("pretty", "true")
					.build();
					
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
		String responseJson = executeHttpGet(uri);
		if(responseJson != null)
			return getPBMMessagesFromJson(responseJson);

		return new HashMap<String, List<String>>();
	}
	

	
	/**
	 * Executes the HTTP get request
	 * @param uri The uri to GET
	 * @return null if the response is something other than 200 (or exception), returns the response
	 * as a string otherwise
	 */
	private static String executeHttpGet(URI uri) {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(uri);
		try {
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200)
				return null;
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Extract all the indices which matches for this vip from the json response
	 * @param statsJson
	 * @param vipName
	 * @return list of indices for this Vip
	 */
	private static List<String> getIndicesForVip(String statsJson, String vipName) {
		JsonElement jelement = new JsonParser().parse(statsJson);
		JsonObject jobj = jelement.getAsJsonObject();
		Set<Entry<String, JsonElement>> indices = jobj.get("indices").getAsJsonObject().entrySet();
		List<String> ret = new ArrayList<String>();
		for(Entry<String, JsonElement> index : indices) {
			String indexName = index.getKey();
			if(indexName.startsWith("vms-" + vipName + "-cyc"))
				ret.add(indexName);
		}
		Collections.sort(ret);
		Collections.reverse(ret);
		return ret;
	}
	
	/**
	 * Get pbm messages from the json
	 * @param json
	 * @return map keyed by error, info and warn each of which keys provide a list of messages which 
	 * fall in that bucket
	 */
	private static Map<String, List<String>> getPBMMessagesFromJson(String json) {
		JsonObject searchResults = new JsonParser().parse(json).getAsJsonObject();
		JsonArray hits = searchResults.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
		
		Map<String, List<String>> messages = new HashMap<String, List<String>>();
		
		for(int i = 0; i < hits.size(); i++) {
			JsonObject searchResultInfo = hits.get(i).getAsJsonObject().get("_source").getAsJsonObject();
			String message = searchResultInfo.get("message").getAsString();
			String logLevel = searchResultInfo.get("eventInfo").getAsJsonObject().get("logLevel").getAsString();
			if(!messages.containsKey(logLevel)) {
				messages.put(logLevel, new ArrayList<String>());
			}
			messages.get(logLevel).add(message);
		}
		
		return messages;
	}
	
	/**
	 * Get the number of search results returned from elastic search from the JSON response of the query
	 * @param json
	 * @return
	 */
	private static int getHitsCountFromJson(String json) {
		JsonObject esResponse = new JsonParser().parse(json).getAsJsonObject();
		JsonObject hitsObject = esResponse.get("hits").getAsJsonObject();
		int hits = hitsObject.get("total").getAsInt();
		return hits;
		
	}
	 	 
}
