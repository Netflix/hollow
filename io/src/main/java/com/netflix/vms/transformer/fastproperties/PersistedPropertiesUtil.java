package com.netflix.vms.transformer.fastproperties;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.metatron.ipc.security.MetatronSslContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class PersistedPropertiesUtil {

	private static final String PROTOCOL_SCHEME = "https";
	private static final String SPINNAKER_HOST = "api.spinnaker.mgmt.netflix.net";
	private static final int SPINNAKER_PORT = 7004;
    private static final String DEFAULT_CMC = "CMC-vms";
    private static final String DEFAULT_EMAIL = "vms-team@netflix.com";
    
    public static boolean propertyExists(String key, String appId, EnvironmentEnum env, RegionEnum region, 
    		String serverId, String stack, String asg, String cluster, String zone) throws IOException {
    	
    	//Create the fast property id
    	String fastPropertyId = getPropertyId(key, appId, env, region, serverId, stack, asg, cluster, zone);
    	
    	// Now construct the URI
    	URI uri = null;
    	try {
    		uri = new URIBuilder()
    				.setScheme(PROTOCOL_SCHEME)
    				.setHost(SPINNAKER_HOST)
    				.setPort(SPINNAKER_PORT)
    				.setPath("fastproperties/id")
    				.setParameter("propertyId", fastPropertyId)
    				.build();
    	} catch(URISyntaxException e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException("Exception while building URL: " + e.getMessage());
    	}

    	CloseableHttpClient client = null;
    	CloseableHttpResponse response = null;
    	// GET the above URI: 200 means that fp exists, 404 means it does not exist, everything else throws exception
    	try {
    		client = HttpClients.custom()
    				   .setSSLContext(MetatronSslContext.forClient("gate"))
    				   .setSSLHostnameVerifier(new NoopHostnameVerifier())
    				   .build();
    		HttpGet get = new HttpGet(uri);
    		response = client.execute(get);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if(statusCode == 200) return true;
    		else if(statusCode == 404) return false;
    		else throw new IOException(String.format("Invalid response (%d) when GETing %s", statusCode, uri.toString()));
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new IOException(e.getMessage() + "GET: " + uri.toString());
    	} finally {
    		response.close();
    		client.close();
    	}
    }
    
    // Gets the value of the fast property if it exists, otherwise return null (signals absence of fast property)
    public static String getPropertyValue(String key, String appId, EnvironmentEnum env, RegionEnum region, 
    		String serverId, String stack, String asg, String cluster, String zone) throws IOException {
    	//Create the fast property id
    	String fastPropertyId = getPropertyId(key, appId, env, region, serverId, stack, asg, cluster, zone);
    	
    	// Now construct the URI
    	URI uri = null;
    	try {
    		uri = new URIBuilder()
    				.setScheme(PROTOCOL_SCHEME)
    				.setHost(SPINNAKER_HOST)
    				.setPort(SPINNAKER_PORT)
    				.setPath("fastproperties/id")
    				.setParameter("propertyId", fastPropertyId)
    				.build();
    	} catch(URISyntaxException e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException("Exception while building URL: " + e.getMessage());
    	}

    	// GET the above URI: 200 means that fp exists (parse and return the value), 404 means it does not exist (return null), throw exception otherwise
    	CloseableHttpClient client = null;
    	CloseableHttpResponse response = null;
    	try {
    		client = HttpClients.custom()
    				   .setSSLContext(MetatronSslContext.forClient("gate"))
    				   .setSSLHostnameVerifier(new NoopHostnameVerifier())
    				   .build();
    		HttpGet get = new HttpGet(uri);
    		response = client.execute(get);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if(statusCode == 200) {
    			return extractValueFromResponseJson(EntityUtils.toString(response.getEntity()));
    		} else if(statusCode == 404) {
    			return null;
    		} else {
    			throw new IOException(String.format("Invalid response (%d) when GETing %s", statusCode, uri.toString())); 
    		}
    	} catch(IOException e) {
    		e.printStackTrace();
    		throw e;
    	} finally {
    		response.close();
    		client.close();
    	}
    }
    
    public static void upsertProperty(String key, String value, String appId, EnvironmentEnum env, RegionEnum region, 
    		String serverId, String stack, String asg, String cluster, String zone) throws IOException {
    	// Now construct the URI
    	URI uri = null;
    	try {
    		uri = new URIBuilder()
    				.setScheme(PROTOCOL_SCHEME)
    				.setHost(SPINNAKER_HOST)
    				.setPort(SPINNAKER_PORT)
    				.setPath("fastproperties/upsert")
    				.build();
    	} catch(URISyntaxException e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException("Exception while building URL: " + e.getMessage());
    	}
    	
        // Create a map to hold the parameters for the POST call, will be converted to JSON and packed into the request body
        Map<String, String> params = new LinkedHashMap<>();
        params.put("env", env.toString());
        if(!Strings.isNullOrEmpty(appId))
        	params.put("appId", appId);
        if(!Strings.isNullOrEmpty(stack))
        	params.put("stack", stack);
        if(region != null)
        	params.put("region", region.toString());
        if(!Strings.isNullOrEmpty(serverId))
        	params.put("serverId", serverId);
        if(!Strings.isNullOrEmpty(cluster))
        	params.put("cluster", cluster);
        if(!Strings.isNullOrEmpty(asg))
        	params.put("asg", asg);
        if(!Strings.isNullOrEmpty(zone))
        	params.put("zone", zone);
        params.put("key", key);
        params.put("value", value);
        params.put("cmcTicket", DEFAULT_CMC);
        params.put("email", DEFAULT_EMAIL);
        
        String requestBody = new Gson().toJson(params);
        
        // Make the POST request
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
        	client = HttpClients.custom()
        			.setSSLContext(MetatronSslContext.forClient("gate"))
        			.setSSLHostnameVerifier(new NoopHostnameVerifier())
        			.build();
        	
        	// create the post request, decorate it with request body and a required header for content-type
        	HttpPost post = new HttpPost(uri);
        	post.setHeader("content-type", "application/json;charset=UTF-8");
        	HttpEntity entity = new ByteArrayEntity(requestBody.getBytes());
        	post.setEntity(entity);
        	
        	// Execute the response and check return status
        	response = client.execute(post);
        	int statusCode = response.getStatusLine().getStatusCode();
        	
        	// In case of upsert 200 (OK) and 201 (Created) are valid, rest are invalid
        	if(statusCode != 200 && statusCode != 201) {
    			throw new IOException(String.format("Invalid response (%d) when POSTing %s with data: \n%S", statusCode, uri.toString(), requestBody)); 
        	}
        }catch(Exception e) {
        	//ignore at the moment
        } finally {
        	client.close();
        	response.close();
        }
    }
    
    public static void deleteProperty(String key, String appId, EnvironmentEnum env, RegionEnum region, 
    		String serverId, String stack, String asg, String cluster, String zone) throws IOException{
    	String fastPropertyId = getPropertyId(key, appId, env, region, serverId, stack, asg, cluster, zone);
    	
    	// Check if the property exists, if it does not return without fuss
    	if(!propertyExists(key, appId, env, region, serverId, stack, asg, cluster, zone)) {
    		return;
    	}

        // Create URI for the POST request
    	// Now construct the URI
    	URI uri = null;
    	try {
    		uri = new URIBuilder()
    				.setScheme(PROTOCOL_SCHEME)
    				.setHost(SPINNAKER_HOST)
    				.setPort(SPINNAKER_PORT)
    				.setPath("fastproperties/delete")
    				.setParameter("propId", fastPropertyId)  // use propertyId for get and propId for delete
    				.setParameter("cmcTicket", DEFAULT_CMC)
    				.build();
    	} catch(URISyntaxException e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException("Exception while building URL: " + e.getMessage());
    	}
    	
    	// GET the above URI: 200 means that fp exists (parse and return the value), 404 means it does not exist (return null), throw exception otherwise
    	CloseableHttpClient client = null;
    	CloseableHttpResponse response = null;
    	try {
    		client = HttpClients.custom()
    				   .setSSLContext(MetatronSslContext.forClient("gate"))
    				   .setSSLHostnameVerifier(new NoopHostnameVerifier())
    				   .build();
    		HttpDelete delete = new HttpDelete(uri);
    		response = client.execute(delete);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if(statusCode != 200 && statusCode != 404) {
    			// even though we check for the property's existance above, still guard against 404
    			throw new IOException(String.format("Invalid response (%d) when DELETINGing %s", statusCode, uri.toString())); 
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new IOException(e.getMessage());
    	} finally {
    		response.close();
    		client.close();
    	}
    }
    


    
    private static String getPropertyId(String key, String appId, EnvironmentEnum env, RegionEnum region, 
    		String serverId, String stack, String asg, String cluster, String zone) {
    	// Key is mandatory
    	if(Strings.isNullOrEmpty(key)) throw new IllegalArgumentException("key is mandatory");
    	
        // Valid environments are test and prod. Other env enums (dev and tools) are invalid
        if(env == null) throw new IllegalArgumentException("fastProperty env is mandatory");
        if((env != EnvironmentEnum.test) && (env != EnvironmentEnum.prod))
            throw new IllegalArgumentException(env.toString() + " is not a valid environment. Only [test, prod] are valid");
        
        // The following arguments if null area treated as empty strings
        // app, region, serverId, stack
        if(appId == null) appId = "";
        if(serverId == null) serverId = "";
        if(stack == null) stack = "";

        String regionStr = (region == null) ? "" : region.toString();
        
        // Build the property id using key, appId, env, region, serverId, stack and countries
        String propertyId = key + "|" + appId + "|" + env.toString() + "|" + regionStr + "|" + serverId + "|" + stack + "|";
        
        // Append asg, cluster and zone if they are there
        if(!Strings.isNullOrEmpty(asg))
        	propertyId += "|asg=" + asg;
        
        if(!Strings.isNullOrEmpty(cluster))
        	propertyId += "|cluster=" + cluster;
        
        if(!Strings.isNullOrEmpty(zone))
        	propertyId += "|zone=" + zone;
        
    	return propertyId;
    }


    private static String extractValueFromResponseJson(String responseJson) throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(responseJson, JsonNode.class);
        String value = rootNode.get("property").get("value").asText();

        return value;
    }

}
