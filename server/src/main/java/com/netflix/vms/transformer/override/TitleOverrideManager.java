package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.util.VipUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TitleOverrideManager {
    private String proxyURL;
    private String inputDataVip;
    private String outputDataVip;

    private final String localBlobStore;
    private final TransformerContext ctx;
    private final FileStore fileStore;

    private final SimultaneousExecutor mainExecutor = new SimultaneousExecutor();
    private Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> completedJobs = new HashMap<TitleOverrideJobSpec, TitleOverrideProcessorJob>();
    private Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> activeJobs = new HashMap<TitleOverrideJobSpec, TitleOverrideProcessorJob>();

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

    public synchronized void clear() {
        completedJobs.clear();
        activeJobs.clear();
    }

    public synchronized void prepareForNextCycle() {
        // cleanup completed jobs that are no longer needed
        Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> neededJobs = new HashMap<TitleOverrideJobSpec, TitleOverrideProcessorJob>();
        for(Map.Entry<TitleOverrideJobSpec, TitleOverrideProcessorJob> entry : completedJobs.entrySet()) {
            TitleOverrideJobSpec spec = entry.getKey();
            if (activeJobs.containsKey(spec)) {
                neededJobs.put(spec, entry.getValue());
            }
        }
        completedJobs = neededJobs;

        // reset active Jobs
        activeJobs.clear();
    }

    /**
     * Process the title override for specific spec asynchronously
     *
     * NOTE: call waitForResults to fetch the result
     */
    public synchronized void processASync(Set<String> overrideTitleSpecs) throws Exception {
        if (overrideTitleSpecs == null || overrideTitleSpecs.isEmpty()) return;

        // Execute them in parallel
        Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> currJobs = processSpecs(overrideTitleSpecs);
        for (Map.Entry<TitleOverrideJobSpec, TitleOverrideProcessorJob> entry : currJobs.entrySet()) {
            TitleOverrideJobSpec spec = entry.getKey();
            TitleOverrideProcessorJob job = entry.getValue();

            activeJobs.put(spec, job);
            mainExecutor.execute(job);
        }
    }

    /**
     * Just return the completed results without waiting pending ones
     */
    public List<HollowReadStateEngine> getCompletedResults() throws InterruptedException, ExecutionException {
        return getResults("COMPLETED JOBS", false);
    }

    /**
     * Return the result of the complete job
     */
    public List<HollowReadStateEngine> waitForResults() throws InterruptedException, ExecutionException {
        mainExecutor.awaitSuccessfulCompletionOfCurrentTasks();
        return getResults("ALL JOBS", true);
    }

    private synchronized List<HollowReadStateEngine> getResults(String label, boolean isPropagateFailure) throws InterruptedException, ExecutionException {
        // Collect Results on sorted Order
        List<HollowReadStateEngine> resultList = new ArrayList<>();
        for (TitleOverrideJobSpec jobSpec : sortJobSpecs(activeJobs.keySet())) {
            TitleOverrideProcessorJob job = activeJobs.get(jobSpec);

            if (job.isCompletedSuccessfully()) {
                resultList.add(job.getResult());
            } else {
                if (isPropagateFailure) throw new ExecutionException("TitleOverrideProcessorJob failure", job.getFailure());
            }
        }

        ctx.getLogger().info(TransformerLogTag.TitleOverride, "[{}] Misc Stat completedJobs={} currJobs={} results={}", label, completedJobs.size(), activeJobs.size(), resultList.size());

        return resultList;
    }

    private static List<TitleOverrideJobSpec> sortJobSpecs(Collection<TitleOverrideJobSpec> specs) {
        List<TitleOverrideJobSpec> result = new ArrayList<>(specs);
        Collections.sort(result); // Sort based on TitleOverrideJobSpec ordering where olderst blob version ist first
        return result;
    }

    // Convert the spec Strings into TitleOverrideProcessorJob
    private Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> processSpecs(Set<String> overrideTitleSpecs) throws InterruptedException, ExecutionException {
        Map<TitleOverrideJobSpec, TitleOverrideProcessorJob> currJobs = new HashMap<>();
        for (String spec : overrideTitleSpecs) {
            String parts[] = spec.split(":");
            int topNode = Integer.parseInt(parts[0]);
            long version = Long.parseLong(parts[1]);
            boolean isInputBased = false;
            if (parts.length >= 3) {
                isInputBased = "in".equals(parts[2]);
            }

            TitleOverrideJobSpec p = new TitleOverrideJobSpec(version, topNode, isInputBased);
            TitleOverrideProcessorJob job = completedJobs.get(p); // reuse last cycle's job if the same - to avoid re-processing
            if (job == null) {
                // prior result not found so create new job
                job = createNewProcessJob(p);
            }
            currJobs.put(p, job);

        }

        return currJobs;
    }

    // Create Job
    private TitleOverrideProcessorJob createNewProcessJob(TitleOverrideJobSpec jobSpec) {
        TitleOverrideProcessor processor;
        if (jobSpec.isInputBased) {
            processor = createInputBasedProcessor();
        } else {
            processor = createOutputBasedProcessor();
        }

        return new TitleOverrideProcessorJob(processor, jobSpec, ctx, new CompleteJobCallback() {
            @Override
            public void completedJob(TitleOverrideJobSpec jobSpec, TitleOverrideProcessorJob job, boolean isSuccessfull) {
                if (isSuccessfull) {
                    completedJobs.put(jobSpec, job);
                }
            }

        });
    }

    // Create Input Based Processor
    private TitleOverrideProcessor createInputBasedProcessor() {
        String vip = inputDataVip != null ? inputDataVip : ctx.getConfig().getConverterVip();
        if (fileStore != null) {
            return new InputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new InputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    // Create Output Based Processor
    private TitleOverrideProcessor createOutputBasedProcessor() {
        String vip = outputDataVip != null ? outputDataVip : VipUtil.getTitleOverrideTransformerVip(ctx.getConfig());
        if (fileStore != null) {
            return new OutputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new OutputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    private interface CompleteJobCallback {
        void completedJob(TitleOverrideJobSpec jobSpec, TitleOverrideProcessorJob job, boolean isSuccessfull);
    }

    /**
     * Processor Job
     */
    private static class TitleOverrideProcessorJob implements Runnable, Comparable<TitleOverrideProcessorJob> {
        private final TitleOverrideProcessor processor;
        private final TitleOverrideJobSpec jobSpec;
        private final TransformerContext ctx;
        private final CompleteJobCallback callback;
        private HollowReadStateEngine resultStateEngine;
        private Throwable failure;

        TitleOverrideProcessorJob(TitleOverrideProcessor processor, TitleOverrideJobSpec jobSpec, TransformerContext ctx, CompleteJobCallback callback) {
            this.processor = processor;
            this.jobSpec = jobSpec;
            this.ctx = ctx;
            this.callback = callback;
        }

        @Override
        public void run() {
            if (isCompletedSuccessfully()) return;

            try {
                reset();
                resultStateEngine = processor.process(jobSpec.version, jobSpec.topNode);
            } catch (Throwable e) {
                ctx.getLogger().error(TransformerLogTag.TitleOverride, "Failed to process override title={} for version={} and vip={}", jobSpec.topNode, jobSpec.version, processor.getVip());
                failure = new Exception("Failed to process topNode=" + jobSpec.version + " for version=" + jobSpec.topNode + "\t on vip=" + processor.getVip(), e);
            } finally {
                callback.completedJob(jobSpec, this, isCompletedSuccessfully());
            }
        }

        public boolean isCompletedSuccessfully() {
            return resultStateEngine != null && failure == null;
        }

        public HollowReadStateEngine getResult() {
            return resultStateEngine;
        }

        public void reset() {
            failure = null;
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