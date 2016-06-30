package com.netflix.vms.transformer.elasticsearch;

import com.google.common.collect.Sets;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchClientBridge {
    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClientBridge.class);

    private final EurekaClient eureka;
    private TransportClient client;
    private final Set<InstanceInfo> esServerSet = new HashSet<InstanceInfo>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> checkerHandle;
    private final String clusterName;
    private final int port;

    private final TransformerConfig config;

    public ElasticSearchClientBridge(TransformerConfig propertyManager, EurekaClient eureka) {
        this.eureka = eureka;
        this.config = propertyManager;
        this.clusterName = propertyManager.getElasticSearchClusterName();
        this.port = Integer.valueOf(propertyManager.getElasticSearchDataPort());
        initialize();
    }

    public Set<InstanceInfo> getInstances() {
        return Collections.unmodifiableSet(esServerSet);
    }

    public boolean isConnected() {
        return !esServerSet.isEmpty();
    }

    private void initialize() {
        final ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
        settingsBuilder.put("cluster.name", config.getElasticSearchClusterName());
        settingsBuilder.put("client.transport.tcp.connect_timeout", config.getElasticSearchTimeoutInSeconds());
        settingsBuilder.put("client.transport.ping_timeout", config.getElasticSearchTimeoutInSeconds());
        settingsBuilder.put("client.transport.nodes_sampler_interval", config.getElasticSearchSamplerIntervalInSeconds());
        if (System.getenv("EC2_INSTANCE_ID") != null) {
            settingsBuilder.put("client.transport.sniff", true);
        }
        client = new TransportClient(settingsBuilder.build());
        updateESRegistry();
        final Runnable checker = new Runnable() {
            @Override
            public void run() {
                updateESRegistry();
            }
        };
        checkerHandle = scheduler.scheduleAtFixedRate(checker, 60, 60, TimeUnit.SECONDS);
    }

    private void updateESRegistry() {
        try {
            final Application app = getApplication(clusterName);
            final List<InstanceInfo> instances = getInstances(clusterName, app);
            final Set<InstanceInfo> newServerSet = new HashSet<InstanceInfo>();

            for (final InstanceInfo instance : instances) {
                if (instance.getStatus() == InstanceInfo.InstanceStatus.UP) {
                    newServerSet.add(instance);
                }
            }
            //LOGGER.infof("UP Instances clusterName={}, app={}, num={} :{}", clusterName, app.getName(), newServerSet.size(), getHostNames(newServerSet));

            for (final InstanceInfo removed : Sets.difference(esServerSet, newServerSet)) {
                client.removeTransportAddress(new InetSocketTransportAddress(removed.getIPAddr(), port));
                esServerSet.remove(removed);
                LOGGER.info("Instance removed id={}, ip-address={}", removed.getId(), removed.getIPAddr());
            }

            SimultaneousExecutor executor = new SimultaneousExecutor(50);

            for (final InstanceInfo added : Sets.difference(newServerSet, esServerSet)) {
                executor.execute(() -> {
                    // TODO: ip-addre:7104/, parse status (200 is green)
                    long t0 = System.currentTimeMillis();
                    InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(added.getIPAddr(), port);
                    LOGGER.info("Instance added id={}, ip={}, dt={}ms", added.getId(), added.getIPAddr(), (System.currentTimeMillis() - t0));
                    client.addTransportAddress(transportAddress);
                    synchronized (esServerSet) {
                        esServerSet.add(added);
                    }
                    LOGGER.info("Instance added id={}, ip={}", added.getId(), added.getIPAddr());
                });
            }

            executor.awaitUninterruptibly();
        } catch (final Exception e) {
            LOGGER.error("Exception while updateESRegistry: {}", e, e.getMessage());
        }
    }

    private List<InstanceInfo> getInstances(final String clusterName, final Application app) {
        final List<InstanceInfo> ins = app.getInstances();
        if (ins == null) {
            throw new RuntimeException("Failed to get instances for discovery application \'" + clusterName.toUpperCase() + "\'");
        }
        return ins;
    }

    private Application getApplication(final String clusterName) {
        final Application app = eureka.getApplication(clusterName.toUpperCase());
        if (app == null) {
            throw new RuntimeException("Failed to get application \'" + clusterName.toUpperCase() + "\' from discovery");
        }

        return app;
    }

    public static boolean healthy(final String host, final String clusterName, final int port) {
        boolean isHealthy = false;
        TransportClient localClient = null;
        InetSocketTransportAddress address = new InetSocketTransportAddress(host, port);
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        try {

            localClient = new TransportClient(settings);
            localClient.addTransportAddress(address);
            ClusterAdminClient aClient = localClient.admin().cluster();
            ClusterHealthRequestBuilder request= aClient.prepareHealth().setTimeout("1000");
            final ClusterHealthStatus healthStatus = request.execute().get().getStatus();
            if (healthStatus == ClusterHealthStatus.GREEN || healthStatus == ClusterHealthStatus.YELLOW) {
                isHealthy = true;
            }
            LOGGER.debug("Health Check: host={}, clusterName={}, port={}, isHealthy={}", host, clusterName, port, isHealthy);
        } catch (final Exception e) {
            LOGGER.error("Failed to check health status: host={}, clusterName={}, port={}, adddres={}, settings={}", e,
                    host, clusterName, port, address, settings.getAsMap());
        } finally {
            if (localClient != null) {
                localClient.close();
            }
        }

        return isHealthy;
    }

    public void shutdown() {
        client.close();
        checkerHandle.cancel(true);
    }

    public Client getClient() {
        return client;
    }
}