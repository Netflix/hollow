package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.TransformerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class TitleOverrideManager {
    private final String proxyURL;
    private final String localBlobStore;
    private final TransformerContext ctx;
    private final FileStore fileStore;

    private TreeMap<TitleOverrideJobSpec, TitleOverrideProcessorJob> lastJobs = new TreeMap<>();

    public TitleOverrideManager(FileStore fileStore, String localBlobStore, TransformerContext ctx) {
        this.fileStore = fileStore;
        this.proxyURL = null;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;
    }

    public TitleOverrideManager(String proxyURL, String localBlobStore, TransformerContext ctx) {
        this.fileStore = null;
        this.proxyURL = proxyURL;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;
    }

    public synchronized List<HollowReadStateEngine> runJobs(TitleOverrideJobSpec... TitleOverrideJobSpecs) throws InterruptedException, ExecutionException {
        // Determine whether it could use prior results
        TreeMap<TitleOverrideJobSpec, TitleOverrideProcessorJob> currJobs = new TreeMap<>();
        for (TitleOverrideJobSpec p : TitleOverrideJobSpecs) {
            TitleOverrideProcessorJob job = lastJobs.get(p);
            if (job==null) {
                // prior result not found so create new job
                job = createNewProcessJob(p);
            }
            currJobs.put(p, job);
        }

        // Execute them in parallel
        SimultaneousExecutor executor = new SimultaneousExecutor();
        for (TitleOverrideProcessorJob job : currJobs.values()) {
            executor.execute(job);
        }
        executor.awaitSuccessfulCompletion();

        // Collect Results on sorted Order
        List<HollowReadStateEngine> resultList = new ArrayList<>();
        for (TitleOverrideJobSpec p : currJobs.keySet()) {
            TitleOverrideProcessorJob job = currJobs.get(p);
            if (job.isSuccessfull()) {
                resultList.add(job.getResult());
            } else {
                throw new ExecutionException("TitleOverrideProcessorJob failure", job.getFailure());
            }
        }

        lastJobs = currJobs;

        return resultList;
    }

    private TitleOverrideProcessorJob createNewProcessJob(TitleOverrideJobSpec jobSpec) {
        TitleOverrideProcessor processor;
        if (jobSpec.isInputBased) {
            processor = createInputBasedProcessor();
        } else {
            processor = createOutputBasedProcessor();
        }

        return new TitleOverrideProcessorJob(processor, jobSpec);
    }

    private TitleOverrideProcessor createInputBasedProcessor() {
        String vip = ctx.getConfig().getConverterVip();
        if (fileStore != null) {
            return new InputSliceTitleOverrideProcessor(vip, fileStore, localBlobStore, ctx);
        } else {
            return new InputSliceTitleOverrideProcessor(vip, proxyURL, localBlobStore, ctx);
        }
    }

    private TitleOverrideProcessor createOutputBasedProcessor() {
        String vip = ctx.getConfig().getTransformerVip();
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
        private HollowReadStateEngine resultStateEngine;
        private Throwable failure;

        TitleOverrideProcessorJob(TitleOverrideProcessor processor, TitleOverrideJobSpec jobSpec) {
            this.processor = processor;
            this.jobSpec = jobSpec;
        }

        @Override
        public void run() {
            // skip if result is already available
            if (resultStateEngine != null) return;

            try {
                resultStateEngine = processor.process(jobSpec.version, jobSpec.topNode);
            } catch (Throwable e) {
                failure = new Exception("Failed to process topNode=" + jobSpec.version + " for version=" + jobSpec.topNode, e);
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