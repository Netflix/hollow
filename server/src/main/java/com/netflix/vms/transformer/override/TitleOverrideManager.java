package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.util.VipUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class TitleOverrideManager {
    private String proxyURL;
    private String inputDataVip;
    private String outputDataVip;

    private final String localBlobStore;
    private final TransformerContext ctx;
    private final FileStore fileStore;
    private final SimultaneousExecutor mainExecutor = new SimultaneousExecutor();
    private final List<HollowReadStateEngine> results = new ArrayList<>();

    private TreeMap<TitleOverrideJobSpec, TitleOverrideProcessorJob> lastJobs = new TreeMap<>();

    public TitleOverrideManager(FileStore fileStore, TransformerContext ctx) {
        this.fileStore = fileStore;
        this.localBlobStore = null;
        this.ctx = ctx;
    }

    public TitleOverrideManager(String proxyURL, String inputDataVip, String outputDataVip, String localBlobStore, TransformerContext ctx) {
        this.fileStore = null;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;

        this.proxyURL = proxyURL;
        this.inputDataVip = inputDataVip;
        this.outputDataVip = outputDataVip;
    }

    public synchronized List<HollowReadStateEngine> runJobs(TitleOverrideJobSpec... jobSpecs) throws InterruptedException, ExecutionException {
        List<HollowReadStateEngine> resultList = new ArrayList<>();
        runJobs(new SimultaneousExecutor(), resultList, jobSpecs);
        return resultList;
    }

    public synchronized void processASync(Set<String> overrideTitleSpecs) throws Exception {
        results.clear();
        runJobs(mainExecutor, results, overrideTitleSpecs);
    }


    public List<HollowReadStateEngine> waitForResults() throws InterruptedException, ExecutionException {
        mainExecutor.awaitSuccessfulCompletion();
        return results;
    }

    private void runJobs(SimultaneousExecutor executor, List<HollowReadStateEngine> resultList, Set<String> overrideTitleSpecs) throws InterruptedException, ExecutionException {
        if (overrideTitleSpecs == null || overrideTitleSpecs.isEmpty()) return;

        int i = 0;
        TitleOverrideJobSpec[] jobSpecs = new TitleOverrideJobSpec[overrideTitleSpecs.size()];
        for (String spec : overrideTitleSpecs) {
            String parts[] = spec.split(":");
            int topNode = Integer.parseInt(parts[0]);
            long version = Long.parseLong(parts[1]);
            boolean isInputBased = false;
            if (parts.length >= 3) {
                isInputBased = "in".equals(parts[2]);
            }
            jobSpecs[i++] = new TitleOverrideJobSpec(version, topNode, isInputBased);
        }

        runJobs(executor, resultList, jobSpecs);
    }

    private synchronized void runJobs(SimultaneousExecutor executor, List<HollowReadStateEngine> resultList, TitleOverrideJobSpec... jobSpecs) throws InterruptedException, ExecutionException {
        // Determine whether it could use prior results
        TreeMap<TitleOverrideJobSpec, TitleOverrideProcessorJob> currJobs = new TreeMap<>();
        for (TitleOverrideJobSpec p : jobSpecs) {
            TitleOverrideProcessorJob job = lastJobs.get(p);
            if (job==null) {
                // prior result not found so create new job
                job = createNewProcessJob(p);
            }
            currJobs.put(p, job);
        }

        // Execute them in parallel
        for (TitleOverrideProcessorJob job : currJobs.values()) {
            executor.execute(job);
        }
        executor.awaitSuccessfulCompletion();

        // Collect Results on sorted Order
        for (TitleOverrideJobSpec p : currJobs.keySet()) {
            TitleOverrideProcessorJob job = currJobs.get(p);
            if (job.isSuccessfull()) {
                resultList.add(job.getResult());
            } else {
                throw new ExecutionException("TitleOverrideProcessorJob failure", job.getFailure());
            }
        }

        lastJobs = currJobs;
    }

    private TitleOverrideProcessorJob createNewProcessJob(TitleOverrideJobSpec jobSpec) {
        TitleOverrideProcessor processor;
        if (jobSpec.isInputBased) {
            processor = createInputBasedProcessor();
        } else {
            processor = createOutputBasedProcessor();
        }

        return new TitleOverrideProcessorJob(processor, jobSpec, ctx);
    }

    private TitleOverrideProcessor createInputBasedProcessor() {
        String vip = inputDataVip != null ? inputDataVip : ctx.getConfig().getConverterVip();
        if (fileStore != null) {
            return new InputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new InputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    private TitleOverrideProcessor createOutputBasedProcessor() {
        String vip = outputDataVip != null ? outputDataVip : VipUtil.getTitleOverrideTransformerVip(ctx.getConfig());
        if (fileStore != null) {
            return new OutputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new OutputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    /**
     * Processor Job
     */
    private static class TitleOverrideProcessorJob implements Runnable, Comparable<TitleOverrideProcessorJob> {
        private final TitleOverrideProcessor processor;
        private final TitleOverrideJobSpec jobSpec;
        private final TransformerContext ctx;
        private HollowReadStateEngine resultStateEngine;
        private Throwable failure;

        TitleOverrideProcessorJob(TitleOverrideProcessor processor, TitleOverrideJobSpec jobSpec, TransformerContext ctx) {
            this.processor = processor;
            this.jobSpec = jobSpec;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            // skip if result is already available
            if (resultStateEngine != null) {
                System.out.println("**** REUSING RESULT for" + jobSpec);
                return;
            }

            try {
                resultStateEngine = processor.process(jobSpec.version, jobSpec.topNode);
            } catch (Throwable e) {
                ctx.getLogger().error(TransformerLogTag.OverrideTitle, "Failed to process override title={} for version={} and vip={}", jobSpec.topNode, jobSpec.version, processor.getVip());
                failure = new Exception("Failed to process topNode=" + jobSpec.version + " for version=" + jobSpec.topNode + "\t on vip=" + processor.getVip(), e);
            }
        }

        public HollowReadStateEngine getResult() {
            return resultStateEngine;
        }

        public boolean isSuccessfull() {
            return failure == null;
        }

        public Throwable getFailure() {
            return failure;
        }

        @Override
        public int compareTo(TitleOverrideProcessorJob o) {
            return this.jobSpec.compareTo(o.jobSpec);
        }

        @Override
        public String toString() {
            return "TitleOverrideProcessorJob " + jobSpec;
        }
    }
}