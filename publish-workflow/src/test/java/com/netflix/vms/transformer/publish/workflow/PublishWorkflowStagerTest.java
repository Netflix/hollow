package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.Dataset.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.Dataset.GATEKEEPER2;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.common.publish.workflow.VipAnnouncer;
import com.netflix.vms.transformer.publish.status.PublishWorkflowStatusIndicator;
import com.netflix.vms.transformer.publish.workflow.job.impl.TestDefaultHollowPublishJobCreator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PublishWorkflowStagerTest {
    private static final String VIP = "magicvip";
    private static final long CURRENT_VERSION = 2L;
    private static final long INPUT_DATA_VERSION = 1L;
    private static final long GK2_INPUT_DATA_VERSION = 1L;
    private static final long LATEST_VERSION = 3L;

    private HollowPublishWorkflowStager stager;
    private List<File> tempFilesCreated;
    private TestDefaultHollowPublishJobCreator jobCreator;
    private CycleInputs testCycleInputs;

    @Mock private HollowConsumer mockConsumerMuon;
    @Mock private HollowConsumer mockConsumerGk2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        TransformerCassandraHelper cassandraHelper = mock(TransformerCassandraHelper.class);
        VipAnnouncer vipAnnouncer = mock(VipAnnouncer.class);
        TransformerConfig config = mock(TransformerConfig.class);
        when(config.isCircuitBreakersEnabled()).thenReturn(true);
        TaggingLogger logger = mock(TaggingLogger.class);

        PublishRegionProvider regionProvider = mock(PublishRegionProvider.class);
        when(regionProvider.getPrimaryRegion()).thenReturn(PublishRegionProvider.ALL_REGIONS.get(0));
        // restrict to one region for simplicity
        when(regionProvider.getNonPrimaryRegions()).thenReturn(new ArrayList<>());

        PublishWorkflowContext publishContext = mock(PublishWorkflowContext.class);
        when(publishContext.getVipAnnouncer()).thenReturn(vipAnnouncer);
        when(publishContext.getConfig()).thenReturn(config);
        when(publishContext.getLogger()).thenReturn(logger);
        when(publishContext.getCassandraHelper()).thenReturn(cassandraHelper);
        when(publishContext.getStatusIndicator()).thenReturn(new PublishWorkflowStatusIndicator(mock(TransformerMetricRecorder.class)));

        TransformerContext transformerContext = mock(TransformerContext.class);
        when(transformerContext.getPublicationHistoryConsumer()).thenReturn(c -> {});
        when(transformerContext.getCassandraHelper()).thenReturn(cassandraHelper);

        Map<UpstreamDatasetHolder.Dataset, InputState> testInputs = new HashMap<>();
        testInputs.put(CONVERTER, new InputState(mockConsumerMuon));
        testInputs.put(GATEKEEPER2, new InputState(mockConsumerGk2));
        testCycleInputs = new CycleInputs(testInputs, 1l);
        when(mockConsumerMuon.getCurrentVersionId()).thenReturn(INPUT_DATA_VERSION);
        when(mockConsumerGk2.getCurrentVersionId()).thenReturn(GK2_INPUT_DATA_VERSION);

        jobCreator = new TestDefaultHollowPublishJobCreator(publishContext, transformerContext, VIP);
        tempFilesCreated = createTempFiles();
        stager = new HollowPublishWorkflowStager(transformerContext, jobCreator, VIP);
        stager.injectPublishRegionProvider(regionProvider);
    }

    @After
    public void tearDown() {
        // clean up created temporary files
        if (tempFilesCreated != null) {
            tempFilesCreated.forEach(f -> {
                if (f.exists()) {
                    f.delete();
                }
            });
        }
    }

    /**
     * Tests that the delete job waits for the circuit breaker job.
     */
    @Test
    public void testDeleteAfterCircuitBreaker() throws Throwable {
        AtomicBoolean deleteJobRun = new AtomicBoolean(false);
        CompletableFuture<Throwable> f = new CompletableFuture<>();

        jobCreator.setDeleteJobExecute(() -> {
            deleteJobRun.set(true);
            return true;
        });

        jobCreator.setCircuitBreakerJobExecute(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1)); // a little brittle, but ¯\_(ツ)_/¯
                if (deleteJobRun.get()) {
                    fail("Delete job ran before circuit breaker");
                }
                f.complete(null);
            } catch (Throwable e) {
                // extract exception from worker thread - we will throw on main thread
                f.complete(e);
            }
            return true;
        });

        stager.triggerPublish(testCycleInputs, CURRENT_VERSION, LATEST_VERSION).awaitStatus();
        // grab exception from worker thread, throw here
        Throwable thrown = f.get();
        if (thrown != null) {
            throw thrown;
        }
    }

    private List<File> createTempFiles() {
        HollowBlobFileNamer fn = new HollowBlobFileNamer(VIP);
        return Stream.of(
                fn.getSnapshotFileName(LATEST_VERSION),
                fn.getDeltaFileName(CURRENT_VERSION, LATEST_VERSION),
                fn.getReverseDeltaFileName(LATEST_VERSION, CURRENT_VERSION)).map(this::createFileNoThrow)
            .collect(Collectors.toList());
    }

    private File createFileNoThrow(String fileName) {
        try {
            File f = new File(fileName);
            f.createNewFile();
            return f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
