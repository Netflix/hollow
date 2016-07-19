package com.netflix.vms.transformer.index;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VMSOutputTypeIndexer {
    private final HollowReadStateEngine stateEngine;
    private final Map<OutputTypeConfig, Object> indexMap;

    public VMSOutputTypeIndexer(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;

        ExecutorService executor = new SimultaneousExecutor();

        try {
            Map<OutputTypeConfig, Object> indexMap = new HashMap<OutputTypeConfig, Object>();

            submitIndexingJobs(stateEngine, executor, indexMap);
            gatherResultsFromIndexingJobs(indexMap);

            this.indexMap = indexMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    public HollowPrimaryKeyIndex getPrimaryKeyIndex(OutputTypeConfig spec) {
        return (HollowPrimaryKeyIndex) indexMap.get(spec);
    }

    private void submitIndexingJobs(HollowReadStateEngine stateEngine, ExecutorService executor, Map<OutputTypeConfig, Object> indexMap) {
        for (OutputTypeConfig spec : OutputTypeConfig.values()) {
            indexMap.put(spec, primaryKeyIdx(executor, stateEngine, spec));
        }
    }

    @SuppressWarnings("unchecked")
    private void gatherResultsFromIndexingJobs(Map<OutputTypeConfig, Object> indexMap) throws InterruptedException, ExecutionException {
        for (Map.Entry<OutputTypeConfig, Object> futureEntry : indexMap.entrySet()) {
            Future<Object> future = (Future<Object>) futureEntry.getValue();
            futureEntry.setValue(future.get());
        }
    }

    private Future<HollowPrimaryKeyIndex> primaryKeyIdx(ExecutorService executor, final HollowReadStateEngine stateEngine, final OutputTypeConfig spec) {
        return executor.submit(new Callable<HollowPrimaryKeyIndex>() {
            @Override
            public HollowPrimaryKeyIndex call() {
                return new HollowPrimaryKeyIndex(stateEngine,
                        spec.getKeyFieldPaths()[0],
                        Arrays.copyOfRange(spec.getKeyFieldPaths(), 1, spec.getKeyFieldPaths().length));
            }
        });
    }
}
