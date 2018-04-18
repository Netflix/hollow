package com.netflix.vms.transformer.fastproperties;

import com.google.common.base.Strings;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class PersistedPropertiesUtil {

    private static final int PORT = 7001;
    private static final String ENDPOINT_HOSTNAME_PROD = "platformservice.%s.dynprod.netflix.net";
    private static final String ENDPOINT_HOSTNAME_TEST = "platformservice.%s.dyntest.netflix.net";
    private static final String ENDPOINT_PATH = "/platformservice/REST/v1/props/property";
    private static final String GET_PATH = ENDPOINT_PATH + "/getPropertyById";
    private static final String DELETE_PATH = ENDPOINT_PATH + "/removePropertyById";

    // Some defaults
    private static final String DEFAULT_SOURCE = "vms-utils";
    private static final String DEFAULT_UPDATED_BY = "vms-team";
    private static final String DEFAULT_CMC = "CMC-1234";

    public static boolean fastPropertyExists(String key, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) throws IOException {
        String fastPropertyId = getFastPropertyId(key, appId, env, region, serverId, stack, countries);
        String myRegion = NetflixConfiguration.getRegion();
        if(myRegion == null)
            myRegion = RegionEnum.US_EAST_1.toString();
        String host = null;
        if(env == EnvironmentEnum.test) host = String.format(ENDPOINT_HOSTNAME_TEST, myRegion);
        else if(env == EnvironmentEnum.prod) host = String.format(ENDPOINT_HOSTNAME_PROD, myRegion);
        else throw new IllegalArgumentException(env + " is not a valid environment. Valid environments are: [test | prod]");

        URI uri = null;
        try {
            uri = new URIBuilder()
            .setScheme("http")
            .setHost(host)
            .setPort(PORT)
            .setPath(GET_PATH)
            .setParameter("id", fastPropertyId)
            .build();
        } catch(URISyntaxException e) {
            throw new IllegalArgumentException("Error building valid uri to query fast property existance.");
        }

        // Query the fast property
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        try {
            System.out.println("Querying: " + uri);
            CloseableHttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) return true;
            else if(statusCode == 404) return false;
            else throw new IOException("Server returned with unexpected return code: " + statusCode);
        } catch(ClientProtocolException e) {
            throw new IOException("Failed to query for property: " + fastPropertyId, e);
        } catch(IOException e) {
            throw new IOException("Failed to query for property: " + fastPropertyId, e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }


    public static String getFastPropertyValue(String key, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) throws IOException {
        String fastPropertyId = getFastPropertyId(key, appId, env, region, serverId, stack, countries);

        String myRegion = NetflixConfiguration.getRegion();
        if(myRegion == null)
            myRegion = RegionEnum.US_EAST_1.toString();
        String host = null;
        if(env == EnvironmentEnum.test) host = String.format(ENDPOINT_HOSTNAME_TEST, myRegion);
        else if(env == EnvironmentEnum.prod) host = String.format(ENDPOINT_HOSTNAME_PROD, myRegion);
        else throw new IllegalArgumentException(env + " is not a valid environment. Valid environments are: [test | prod]");

        URI uri = null;
        try {
            uri = new URIBuilder()
            .setScheme("http")
            .setHost(host)
            .setPort(PORT)
            .setPath(GET_PATH)
            .setParameter("id", fastPropertyId)
            .build();
        } catch(URISyntaxException e) {
            throw new IllegalArgumentException("Error building valid uri to query fast property existance.");
        }

        // Query the fast property
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        get.setHeader("accept", "application/json");
        try {
            System.out.println("Querying: " + uri);
            CloseableHttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String resp = EntityUtils.toString(response.getEntity());

            if(statusCode == 200) {
                // extract the value from the response
                return extractValueFromResponseJson(resp);
            } else {
                // throw the exception
                throw new IOException("Server responded with status code: " + statusCode + ".\n And sent the following response\n" + resp);
            }
        } catch(ClientProtocolException e) {
            throw new IOException("Failed to query for property: " + fastPropertyId, e);
        } catch(IOException e) {
            throw new IOException("Failed to query for property: " + fastPropertyId, e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static void createOrUpdateFastProperty(String key, String value, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) throws IOException {
        if(fastPropertyExists(key, appId, env, region, serverId, stack, countries)) {
            updateFastProperty(key, value, appId, env, region, serverId, stack, countries);
        } else {
            createFastProperty(key, value, appId, env, region, serverId, stack, countries);
        }
    }
    
    public static void createFastProperty(String key, String value, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) throws IOException {

        // Create the fast propertyID
        String fastPropertyId = getFastPropertyId(key, appId, env, region, serverId, stack, countries);
        System.out.println("Will be creating FP: " + fastPropertyId + " with value: " + value);

        // Construct the xml data that will be posted to the server
        String postDataXml = constructCreatePropertyXml(key, value, appId, env, region, serverId, stack, countries);

        String myRegion = NetflixConfiguration.getRegion();
        if(myRegion == null)
            myRegion = RegionEnum.US_EAST_1.toString();
        String host = null;
        if(env == EnvironmentEnum.test) host = String.format(ENDPOINT_HOSTNAME_TEST, myRegion);
        else if(env == EnvironmentEnum.prod) host = String.format(ENDPOINT_HOSTNAME_PROD, myRegion);
        else throw new IllegalArgumentException(env + " is not a valid environment. Valid environments are: [test | prod]");

        // Build the post uri
        URI uri = null;
        try {
            uri = new URIBuilder()
            .setScheme("http")
            .setHost(host)
            .setPort(PORT)
            .setPath(ENDPOINT_PATH)
            .build();
        } catch(URISyntaxException e) {
            throw new IOException("Error building POST uri to create fast property.", e);
        }

        System.out.println("POSTing to: " + uri.toString());
        System.out.println("POST data:\n" + postDataXml);

        // Do a POST request to create the fast property
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/xml");
        HttpEntity entity = new ByteArrayEntity(postDataXml.getBytes());
        post.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200)
                System.out.println("Property created successfully");
            else {
                String resp = EntityUtils.toString(response.getEntity());
                throw new IOException("Server responded with status code: " + statusCode +
                        ".\n And sent the following response\n" + resp);
            }
        } catch(ClientProtocolException e) {
            throw new IOException("Error creating fast property: " + fastPropertyId, e);
        } catch (IOException e) {
            throw new IOException("Error creating fast property: " + fastPropertyId, e);
        } finally {
            try {
                httpClient.close();
            } catch(IOException e) {
                // ignore
            }
        }

    }

    public static void updateFastProperty(String key, String value, String appId,
            EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) throws IOException {

        // First construct the fast propertyId and from that derive the post data which will be sent to the server
        String fastPropertyId = getFastPropertyId(key, appId, env, region, serverId, stack, countries);
        String postDataXml = constructUpdatePropertyXml(fastPropertyId, value);

        String myRegion = NetflixConfiguration.getRegion();
        if(myRegion == null)
            myRegion = RegionEnum.US_EAST_1.toString();
        String host = null;
        if(env == EnvironmentEnum.test) host = String.format(ENDPOINT_HOSTNAME_TEST, myRegion);
        else if(env == EnvironmentEnum.prod) host = String.format(ENDPOINT_HOSTNAME_PROD, myRegion);
        else throw new IllegalArgumentException(env + " is not a valid environment. Valid environments are: [test | prod]");

        // Build the post uri
        URI uri = null;
        try {
            uri = new URIBuilder()
            .setScheme("http")
            .setHost(host)
            .setPort(PORT)
            .setPath(ENDPOINT_PATH)
            .build();
        } catch(URISyntaxException e) {
            throw new IOException("Error building POST uri to create fast property.", e);
        }

        System.out.println("POSTing to: " + uri.toString());
        System.out.println("POST data:\n" + postDataXml);

        // Do a POST request to create the fast property
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/xml");
        HttpEntity entity = new ByteArrayEntity(postDataXml.getBytes());
        post.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200)
                System.out.println("Property updated successfully");
            else {
                String resp = EntityUtils.toString(response.getEntity());
                throw new IOException("Server responded with status code: " + statusCode +
                        ".\n And sent the following response\n" + resp);
            }
        } catch(ClientProtocolException e) {
            throw new IOException("Error creating fast property: " + fastPropertyId, e);
        } catch (IOException e) {
            throw new IOException("Error creating fast property: " + fastPropertyId, e);
        } finally {
            try {
                httpClient.close();
            } catch(IOException e) {
                // ignore
            }
        }

    }

    public static void deleteFastProperty(String key, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries)
            throws IOException {
        // Create the fast propertyID
        String fastPropertyId = getFastPropertyId(key, appId, env, region, serverId, stack, countries);

        // First check if the property already exists
        // If the property does not exists, do not do anything
        if(!fastPropertyExists(key, appId, env, region, serverId, stack, countries)) {
            System.out.println("Nothing to delete as property does not exist: " + fastPropertyId);
            return;
        }
        System.out.println("Property Exists -- Deleting ...");

        String myRegion = NetflixConfiguration.getRegion();
        if(myRegion == null)
            myRegion = RegionEnum.US_EAST_1.toString();
        String host = null;
        if(env == EnvironmentEnum.test) host = String.format(ENDPOINT_HOSTNAME_TEST, myRegion);
        else if(env == EnvironmentEnum.prod) host = String.format(ENDPOINT_HOSTNAME_PROD, myRegion);
        else throw new IllegalArgumentException(env + " is not a valid environment. Valid environments are: [test | prod]");

        URI uri = null;
        try {
            uri = new URIBuilder()
            .setScheme("http")
            .setHost(host)
            .setPort(PORT)
            .setPath(DELETE_PATH)
            .setParameter("id", fastPropertyId)
            .setParameter("source", DEFAULT_SOURCE)
            .setParameter("updatedBy", DEFAULT_UPDATED_BY)
            .setParameter("cmcTicket", DEFAULT_CMC)
            .build();
        } catch(URISyntaxException e) {
            throw new IllegalArgumentException("Error building valid uri to delete fast property.");
        }

        // Do a get request to delete the property
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200)
                System.out.println("Successfully deleted property: " + fastPropertyId);
            else
                throw new IOException("Server returned with unexpected status code: " + statusCode);
        } catch(ClientProtocolException e) {
            throw new IOException("Failed to delete the property: " + fastPropertyId, e);
        } catch(IOException e) {
            throw new IOException("Failed to delete the property: " + fastPropertyId, e);
        } finally {
            try {
                httpClient.close();
            } catch(IOException e) {
                // ignore
            }
        }



    }

    private static String getFastPropertyId(String key, String appId, EnvironmentEnum env, RegionEnum region, String serverId, String stack, String countries) {
        // Key is mandatory, if not provided throw IllegalArgument exception
        if(Strings.isNullOrEmpty(key)) throw new IllegalArgumentException("FastProperty key is mandatory");

        // Valid environments are test and prod. Other env enums (dev and tools) are invalid
        if(env == null) throw new IllegalArgumentException("fastProperty env is mandatory");
        if((env != EnvironmentEnum.test) && (env != EnvironmentEnum.prod))
            throw new IllegalArgumentException(env.toString() + " is not a valid environment. Only [test, prod] are valid");

        // Following arguments if null are treated as empty strings
        // appId, serverId, stack, countries
        if(appId == null) appId = "";
        if(serverId == null) serverId = "";
        if(stack == null) stack = "";
        if(countries == null) countries = "";

        String regionStr = (region == null) ? "" : region.toString();


        return (key + "|" + appId + "|" + env.toString() + "|" + regionStr + "|" + serverId + "|" + stack + "|" + countries);
    }

    private static String constructCreatePropertyXml(String key, String value, String appId, EnvironmentEnum env,
            RegionEnum region, String serverId, String stack, String countries) {

        // Key must be provided
        if(Strings.isNullOrEmpty(key)) throw new IllegalArgumentException("Key must be provided while creating fast property");

        // Env must be provided and must be either test or prod
        if(env == null) throw new IllegalArgumentException("env must be provided while creating fast property");
        if((env != EnvironmentEnum.test) && (env != EnvironmentEnum.prod))
            throw new IllegalArgumentException(env.toString() + " is not a valid environment. Only [test, prod] are valid");

        // Value must be provided as well
        if(Strings.isNullOrEmpty(value)) throw new IllegalArgumentException("Value must be provided while creating fast property");

        // Construct the timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timestamp = dateFormat.format(new Date()).replace(" ","T") + "Z";

        StringBuffer buffer = new StringBuffer();
        buffer.append("<property>");
        buffer.append("<key>" + key + "</key>");
        buffer.append("<value>" + value + "</value>");
        if(!Strings.isNullOrEmpty(appId))
            buffer.append("<appId>" + appId + "</appId>");
        buffer.append("<env>" + env.toString() + "</env>");
        if(region != null)
            buffer.append("<region>" + region.toString() + "</region>");
        if(!Strings.isNullOrEmpty(serverId))
            buffer.append("<serverId>" + serverId + "</serverId>");
        if(!Strings.isNullOrEmpty(stack))
            buffer.append("<stack>" + stack + "</stack>");
        if(!Strings.isNullOrEmpty(countries))
            buffer.append("<countries>" + countries +  "</countries>");
        buffer.append("<updatedBy>" + DEFAULT_UPDATED_BY + "</updatedBy>");
        buffer.append("<sourceOfUpdate>" + DEFAULT_SOURCE + "</sourceOfUpdate>");
        buffer.append("<ts>" + timestamp + "</ts>");
        buffer.append("<cmcTicket>" + DEFAULT_CMC + "</cmcTicket>");
        buffer.append("</property>");
        return buffer.toString();
    }

    private static String constructUpdatePropertyXml(String fastPropertyId, String value) {
        // We validate that both the arguments are non null
        if(Strings.isNullOrEmpty(fastPropertyId)) throw new IllegalArgumentException("fastPropertyId is mandatory.");
        if(Strings.isNullOrEmpty(value)) throw new IllegalArgumentException("value is mandatory");

        StringBuffer buffer = new StringBuffer();
        buffer.append("<property>");
        buffer.append("<propertyId>" + fastPropertyId + "</propertyId>");
        buffer.append("<value>" + value + "</value>");
        buffer.append("<updatedBy>" + DEFAULT_UPDATED_BY + "</updatedBy>");
        buffer.append("<sourceOfUpdate>" + DEFAULT_SOURCE + "</sourceOfUpdate>");
        buffer.append("<cmcTicket>" + DEFAULT_CMC + "</cmcTicket>");
        buffer.append("</property>");
        return buffer.toString();
    }

    private static String extractValueFromResponseJson(String responseJson) throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(responseJson, JsonNode.class);
        String value = rootNode.get("property").get("value").asText();

        return value;
    }

}
