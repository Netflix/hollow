package com.netflix.vms.transformer.rest;

import com.google.inject.Inject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.NetflixConfiguration;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import java.util.Set;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vms/elasticsearchadmin")
public final class VMSElasticSearchAdmin {

    private final TransformerConfig config;
    private final ElasticSearchClient esClient;

    @Inject
    public VMSElasticSearchAdmin(TransformerConfig config, ElasticSearchClient esClient) {
        this.config = config;
        this.esClient = esClient;
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public Response getGreeting(@QueryParam("query") String query) {


        switch(query.toLowerCase()) {
        case "elasticsearchhost":
            if (!config.isElasticSearchLoggingEnabled()) {
                return Response.ok("es disabled").build();
            }

            final Set<InstanceInfo> instances = esClient.getElasticSearchClientBridge().getInstances();
            if (instances.size() > 0) {
                final String hostname = instances.iterator().next().getHostName();
                return Response.ok(hostname.toString()).build();
            }
            return Response.ok("No elastic search instances found").build();

        case "nflx-env":
            return Response.ok(NetflixConfiguration.getEnvironment()).build();

        case "vip-address":
            return Response.ok(config.getTransformerVip()).build();

        case "data-namespace":
            return Response.ok(config.getConverterVip()).build();

        }

        //TODO: timt: Do something with this.
        return Response.ok("unkown option: " + query).build();
    }
}
