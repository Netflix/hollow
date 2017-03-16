package com.netflix.vms.transformer.rest.blobinfo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.config.NetflixConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Singleton
@Path("/vms/diffhistory")
public class VMSDiffHistoryProxy {

    private static final String DISCOVERY_PROD_BASE = "http://discovery.cloud.netflix.net:7001/";
    private static final String DISCOVERY_TEST_BASE = "http://discovery.cloudqa.netflix.net:7001/";
    private static final String DISCOVERY_PATH = "discovery/resolver/cluster/";

    private static final int CONNECTION_TIME_OUT = 5 * 1000;

    @Inject
    public VMSDiffHistoryProxy() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDiffHistoryOverviewJson(@QueryParam("vip") String vip) {
        String out = "";

        // Get the environment
        String env = NetflixConfiguration.getEnvironment();

        String diffClusterName = String.format("vmstransformerhistory-%s", vip);

        String diffOverviewUrl = null;
        if(env.equals("prod")) {
            diffOverviewUrl = DISCOVERY_PROD_BASE + DISCOVERY_PATH + diffClusterName + "/REST/history";
        } else {
            diffOverviewUrl = DISCOVERY_TEST_BASE + DISCOVERY_PATH + diffClusterName + "/REST/history";
        }
        diffOverviewUrl += "?format=json";

        Map<String, String> errorData = new HashMap<String, String>();
        Gson gson = new Gson();

        CloseableHttpClient client = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIME_OUT)
                .setConnectTimeout(CONNECTION_TIME_OUT)
                .setSocketTimeout(CONNECTION_TIME_OUT)
                .build();

        HttpGet get = new HttpGet(diffOverviewUrl);
        get.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                out += EntityUtils.toString(response.getEntity());
            } else {
                errorData.put("error", "diff server not found");
                out = gson.toJson(errorData);
            }
        } catch(ClientProtocolException e) {
            errorData.put("error", e.getMessage());
            return gson.toJson(errorData);
        } catch(IOException e) {
            errorData.put("error", e.getMessage());
            return gson.toJson(errorData);
        } finally {
            try {
                client.close();
            } catch(IOException e) {
                //ignore
            }
        }

        return out;
    }
}
