package com.netflix.vms.transformer.override;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.input.VMSInputDataClient;
import com.netflix.vms.transformer.override.PinTitleManager.PinTitleProcessorJob;

public class PinTitleManagerTest {
    private static final String BASE_PROXY = VMSInputDataClient.TEST_PROXY_URL;
    private static final String LOCAL_BLOB_STORE = "/space/title-pinning";

    private SimpleTransformerContext ctx;
    private PinTitleManager mgr;

    @Before
    public void setup() {
        ctx = new SimpleTransformerContext();
        mgr = createNewMgr();
    }

    private PinTitleManager createNewMgr() {
        return Mockito.spy(new PinTitleManager(BASE_PROXY, "boson", "berlin", LOCAL_BLOB_STORE, ctx));
    }

    @Test
    public void testProcessSpecs() throws Exception {
        String spec = "20160829111536238:60029157";
        PinTitleJobSpec expectedJobSpec = mgr.createJobSpec(spec);

        Map<PinTitleJobSpec, PinTitleProcessorJob> processSpecs = mgr.processSpecs(new HashSet<>(Arrays.asList(spec)));
        Assert.assertEquals(1, processSpecs.size());
        Assert.assertTrue(processSpecs.containsKey(expectedJobSpec));
    }

    @Test
    public void testProcessSpecWithMerge() throws Exception {
        String spec1 = "20160829111536238:60029157";
        String spec2 = "20160829111536238:60029158";
        PinTitleJobSpec jSpec1 = mgr.createJobSpec(spec1);
        PinTitleJobSpec jSpec2 = mgr.createJobSpec(spec2);

        PinTitleJobSpec expectedJobSpec = jSpec1.merge(jSpec2);
        Assert.assertEquals(1, jSpec1.topNodes.length);
        Assert.assertEquals(1, jSpec2.topNodes.length);
        Assert.assertEquals(2, expectedJobSpec.topNodes.length);

        Map<PinTitleJobSpec, PinTitleProcessorJob> processSpecs = mgr.processSpecs(new HashSet<>(Arrays.asList(spec1, spec2)));
        Assert.assertEquals(1, processSpecs.size());
        Assert.assertTrue(processSpecs.containsKey(expectedJobSpec));
    }

    @Test
    public void testProcessSpecWithMergeWithDupes() throws Exception {
        String spec1 = "20160829111536238:60029157,60029100";
        String spec2 = "20160829111536238:60029158, 60029157";
        PinTitleJobSpec jSpec1 = mgr.createJobSpec(spec1);
        PinTitleJobSpec jSpec2 = mgr.createJobSpec(spec2);

        PinTitleJobSpec expectedJobSpec = jSpec1.merge(jSpec2);
        Assert.assertEquals(2, jSpec1.topNodes.length);
        Assert.assertEquals(2, jSpec2.topNodes.length);
        Assert.assertEquals(3, expectedJobSpec.topNodes.length);

        Map<PinTitleJobSpec, PinTitleProcessorJob> processSpecs = mgr.processSpecs(new HashSet<>(Arrays.asList(spec1, spec2)));
        Assert.assertEquals(1, processSpecs.size());
        Assert.assertTrue(processSpecs.containsKey(expectedJobSpec));
    }

    @Test
    public void testProcessSpecMultiCycle() throws Exception {
        String spec1 = "20160829111536238:60029157, 60029100";
        String spec2 = "20160829111536238:60029158,60029157";
        PinTitleJobSpec jSpec1 = mgr.createJobSpec(spec1);
        PinTitleJobSpec jSpec2 = mgr.createJobSpec(spec2);

        PinTitleJobSpec expectedJobSpec = jSpec1.merge(jSpec2);
        Assert.assertEquals(2, jSpec1.topNodes.length);
        Assert.assertEquals(2, jSpec2.topNodes.length);
        Assert.assertEquals(3, expectedJobSpec.topNodes.length);
        Mockito.when(mgr.createOutputBasedProcessor()).thenReturn(new DummyProcessor());

        { // cycle 1
            mgr.submitJobsToProcessASync(new HashSet<>(Arrays.asList(spec1)));
            List<HollowReadStateEngine> results = mgr.getResults(true);
            Map<PinTitleJobSpec, PinTitleProcessorJob> completedjobs = mgr.getCompletedJobs();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(1, completedjobs.size());
            Assert.assertTrue(completedjobs.containsKey(jSpec1));
        }

        { // cycle 2 - not merge since duplicate are from from different cycles
            mgr.prepareForNextCycle();
            mgr.submitJobsToProcessASync(new HashSet<>(Arrays.asList(spec1, spec2)));
            List<HollowReadStateEngine> results = mgr.getResults(true);
            Map<PinTitleJobSpec, PinTitleProcessorJob> completedjobs = mgr.getCompletedJobs();
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(2, completedjobs.size());
            Assert.assertTrue(completedjobs.containsKey(jSpec1));
            Assert.assertTrue(completedjobs.containsKey(jSpec2));
            Assert.assertFalse(completedjobs.containsKey(expectedJobSpec));
        }

        mgr.reset(); // Simulate startup - with merge multiple spec of same blob version
        {
            mgr.prepareForNextCycle();
            mgr.submitJobsToProcessASync(new HashSet<>(Arrays.asList(spec1, spec2)));
            List<HollowReadStateEngine> results = mgr.getResults(true);
            Map<PinTitleJobSpec, PinTitleProcessorJob> completedjobs = mgr.getCompletedJobs();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(1, completedjobs.size());
            Assert.assertTrue(completedjobs.containsKey(expectedJobSpec));
        }

    }

    public static class DummyProcessor implements PinTitleProcessor {

        @Override
        public HollowReadStateEngine process(long dataVersion, int... topNodes) throws Throwable {
            return null;
        }

        @Override
        public String getVip() {
            return null;
        }
    }
}