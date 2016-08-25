package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.util.OverrideVipNameUtil;

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
    }

    /**
     * Submit Jobs to be processed asynchronously
     */
    public synchronized void submitJobsToProcessASync(Set<String> overrideTitleSpecs) throws Exception {
        // Execute them in parallel
        activeJobs = processSpecs(overrideTitleSpecs);
        for (TitleOverrideProcessorJob job : activeJobs.values()) {
            mainExecutor.execute(job);
        }
    }

    /**
     * Return the result of the complete job
     */
    public List<HollowReadStateEngine> getResults(boolean isWaitForAllJobs) throws InterruptedException, ExecutionException {
        if (isWaitForAllJobs) {
            mainExecutor.awaitSuccessfulCompletionOfCurrentTasks();
        }

        List<HollowReadStateEngine> resultList = processResults(true);
        ctx.getLogger().info(TransformerLogTag.TitleOverride, "Misc Stat completedJobs={} currJobs={} results={} waitedForAllJobs={}", completedJobs.size(), activeJobs.size(), resultList.size(), isWaitForAllJobs);
        return resultList;
    }

    private synchronized List<HollowReadStateEngine> processResults(boolean isPropagateFailure) throws ExecutionException {
        // Collect Results on sorted Order
        List<HollowReadStateEngine> resultList = new ArrayList<>();
        for (TitleOverrideJobSpec jobSpec : sortJobSpecs(activeJobs.keySet())) {
            TitleOverrideProcessorJob job = activeJobs.get(jobSpec);
            if (!job.getStatus().isCompleted()) continue;

            if (job.isCompletedSuccessfully()) {
                resultList.add(job.getResult());
            } else {
                if (isPropagateFailure) {
                    throw new ExecutionException("TitleOverrideProcessorJob failure: " + jobSpec, job.getFailure());
                }
            }
        }
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

        if (overrideTitleSpecs != null) {
            for (String spec : overrideTitleSpecs) {
                String parts[] = spec.split(":");
                int topNode = Integer.parseInt(parts[0]);
                long version = Long.parseLong(parts[1]);
                boolean isInputBased = false;
                if (parts.length >= 3) {
                    isInputBased = "in".equals(parts[2]);
                }

                TitleOverrideJobSpec p = new TitleOverrideJobSpec(version, topNode, isInputBased);
                TitleOverrideProcessorJob job = getExistingJob(p); // reuse last cycle's job if the same - to avoid re-processing
                if (job == null) {
                    // prior result not found so create new job
                    job = createNewProcessJob(p);
                }
                currJobs.put(p, job);
            }
        }

        return currJobs;
    }

    private TitleOverrideProcessorJob getExistingJob(TitleOverrideJobSpec spec) {
        TitleOverrideProcessorJob job = completedJobs.get(spec);
        if (job!=null) return job;

        job = activeJobs.get(spec);
        return job;
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
        String vip = outputDataVip != null ? outputDataVip : OverrideVipNameUtil.getTitleOverrideTransformerVip(ctx.getConfig());
        if (fileStore != null) {
            return new OutputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new OutputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    private interface CompleteJobCallback {
        void completedJob(TitleOverrideJobSpec jobSpec, TitleOverrideProcessorJob job, boolean isSuccessfull);
    }

    public enum JobStatus {
        PENDING(false), RUNNING(false), COMPLETED_SUCC(true), COMPLETED_FAIL(true);

        private boolean isCompleted;

        JobStatus(boolean isCompleted) {
            this.isCompleted = isCompleted;
        }

        public boolean isCompleted() {
            return this.isCompleted;
        }
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
        private JobStatus status = JobStatus.PENDING;

        TitleOverrideProcessorJob(TitleOverrideProcessor processor, TitleOverrideJobSpec jobSpec, TransformerContext ctx, CompleteJobCallback callback) {
            this.processor = processor;
            this.jobSpec = jobSpec;
            this.ctx = ctx;
            this.callback = callback;
        }

        @Override
        public synchronized void run() {
            if (isCompletedSuccessfully()) return;

            try {
                reset();
                status = JobStatus.RUNNING;
                resultStateEngine = processor.process(jobSpec.version, jobSpec.topNode);

                status = JobStatus.COMPLETED_SUCC;
            } catch (Throwable e) {
                status = JobStatus.COMPLETED_FAIL;
                ctx.getLogger().error(TransformerLogTag.TitleOverride, "Failed to process override title={} for version={} and vip={}", jobSpec.topNode, jobSpec.version, processor.getVip());
                failure = new Exception("Failed to process topNode=" + jobSpec.version + " for version=" + jobSpec.topNode + "\t on vip=" + processor.getVip(), e);
            } finally {
                callback.completedJob(jobSpec, this, isCompletedSuccessfully());
            }
        }

        public JobStatus getStatus() {
            return status;
        }

        public boolean isCompletedSuccessfully() {
            return status == JobStatus.COMPLETED_SUCC;
        }

        public HollowReadStateEngine getResult() {
            return resultStateEngine;
        }

        public void reset() {
            status = JobStatus.PENDING;
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