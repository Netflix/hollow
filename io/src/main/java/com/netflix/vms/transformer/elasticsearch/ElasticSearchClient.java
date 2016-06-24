package com.netflix.vms.transformer.elasticsearch;

import com.google.inject.Inject;
import com.netflix.discovery.EurekaClient;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Singleton;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ElasticSearchClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClient.class);

    private static final ArrayList<Thread> transportThreads = new ArrayList<Thread>();
    private static final ArrayList<RunnableTransportClient> transportClients = new ArrayList<RunnableTransportClient>();
    private static final ConcurrentHashMap<Long, Long> threadTimeStats = new ConcurrentHashMap<Long, Long>();
    private static final ScheduledExecutorService queueInfoScheduler = Executors.newScheduledThreadPool(1);

    private static AtomicInteger kAddCounter = new AtomicInteger(0);

    private final int maxQueueSize;
    private final int maxTransportThreads;
    private final int queueTimeout;
    private final boolean doNotWaitForQueue;
    private final LinkedBlockingQueue<ElasticSearchLogMessage> indexDataQueue;
    private final TransformerConfig propertyManager;

    @Inject
    private ElasticSearchClient(TransformerConfig config, EurekaClient eureka) {
        this.propertyManager = config;
        this.maxQueueSize = config.getElasticSearchMaxQueueSize();
        this.maxTransportThreads = config.getElasticSearchMaxTransportThreads();
        this.queueTimeout = config.getElasticSearchQueueTimeoutMillis();
        this.doNotWaitForQueue = config.isElasticSearchNoWaitingEnabled();
        this.indexDataQueue = new LinkedBlockingQueue<ElasticSearchLogMessage>(maxQueueSize);

        if(config.isElasticSearchLoggingEnabled()) {

            for (int i = 0; i < maxTransportThreads; i++) {
                RunnableTransportClient client = new RunnableTransportClient(propertyManager, indexDataQueue, eureka);
                Thread clientThread = new Thread(client);
                transportClients.add(client);
                transportThreads.add(clientThread);
                clientThread.setDaemon(true);
                clientThread.start();
            }

            initialize();
        }
    }

    private void initialize() {
        final Runnable infoUpdater = new Runnable() {
            @Override
            public void run() {
                printStats();
            }
        };
        queueInfoScheduler.scheduleAtFixedRate(infoUpdater, 600, 600, TimeUnit.SECONDS);
    }

    public ElasticSearchClientBridge getElasticSearchClientBridge() {
        return transportClients.get(0).getClientBridge();
    }

    // overflow is dropped
    public void addData(String index, String type, String json) {

        ElasticSearchLogMessage logMessage = new ElasticSearchLogMessage(index, type, json);

        boolean success = true;
        if (doNotWaitForQueue) {
            success = indexDataQueue.offer(logMessage);
            kAddCounter.incrementAndGet();
        } else {
            try {
                final long t0 = System.nanoTime();
                success = indexDataQueue.offer(logMessage, queueTimeout, TimeUnit.MILLISECONDS);
                final Long dtMillis = (System.nanoTime() - t0) / 1000000;
                kAddCounter.incrementAndGet();
                final Long threadId = Thread.currentThread().getId();
                final Long prevDuration = threadTimeStats.putIfAbsent(threadId, dtMillis);
                if (prevDuration != null) {
                    threadTimeStats.put(threadId, prevDuration + dtMillis);
                }
            } catch (final InterruptedException e) {
                success = false;
            }
        }

        if (!success) {
            LOGGER.warn("DroppedFromElasticSearch: index=" + index + ", type=" + type);
            // Counter.increment(MetaDataTracer.COUNTER_ELASTICSEARCH_LOG_ERROR); // commented out during testing
        }
    }

    private void printStats() {
        LOGGER.info("ElasticSearch: Added " + kAddCounter.get() + ", Queue capacity=" + maxQueueSize + ", remaining="
                + indexDataQueue.remainingCapacity() + ", maxTimePenalty=" + getMaxTimePenaltyInSeconds() + "s");
    }

    private static Long getMaxTimePenaltyInSeconds() {
        Long maxTime = new Long(0);
        final Set<Long> threadIds = threadTimeStats.keySet();
        for (final Long threadId : threadIds) {
            final Long dt = threadTimeStats.get(threadId);
            if (maxTime < dt) {
                maxTime = dt;
            }
        }

        return maxTime / 1000;
    }

    static class RunnableTransportClient implements Runnable {
        private static final int MAX_WAIT_INITIALIZE_SECONDS = 60;

        private final LinkedBlockingQueue<ElasticSearchLogMessage> indexableItemQueue;
        private static ElasticSearchClientBridge esClientBridge = null;
        private static final Object initLock = new Object();
        private volatile Boolean runState = new Boolean(true);

        RunnableTransportClient(TransformerConfig config, LinkedBlockingQueue<ElasticSearchLogMessage> jsonQueue, EurekaClient eureka) {
            this.indexableItemQueue = jsonQueue;
            synchronized (initLock) {
                if (esClientBridge == null) {
                    esClientBridge = new ElasticSearchClientBridge(config, eureka);
                }
            }
        }

        public void quit() {
            runState = false;
        }

        public ElasticSearchClientBridge getClientBridge() {
            return esClientBridge;
        }

        private void waitForInitialization() {
            int numTries = 0;
            while (!esClientBridge.isConnected()) {
                if (++numTries > MAX_WAIT_INITIALIZE_SECONDS) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            LOGGER.info("ES initialization -- " + esClientBridge.isConnected() + ", numTries=" + numTries);
        }

        @Override
        public void run() {
            waitForInitialization();

            while (runState) {
                final HashSet<ElasticSearchLogMessage> items = new HashSet<ElasticSearchLogMessage>();
                try {
                    items.add(indexableItemQueue.take());
                } catch (final InterruptedException e) {
                    LOGGER.warn(e.getMessage(), e);
                    continue;
                }
                indexableItemQueue.drainTo(items, 200);
                final Client esTransportClient = esClientBridge.getClient();
                try {
                    final BulkRequestBuilder bulkRequest = esTransportClient.prepareBulk().setReplicationType(ReplicationType.ASYNC);

                    for (ElasticSearchLogMessage item : items) {
                        bulkRequest.add(esTransportClient.prepareIndex(item.getIndexName(), item.getIndexType()).setSource(item.getJsonData()));
                    }

                    final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                    if (bulkResponse.hasFailures()) {
                        LOGGER.warn("DroppedFromElasticSearch (bulkResponse) " + bulkResponse.buildFailureMessage());
                    }
                } catch (final Exception e) {
                    LOGGER.warn("DroppedFromElasticSearch (run) ", e);
                }
            }// while run
        } // run()
    } // class
}
