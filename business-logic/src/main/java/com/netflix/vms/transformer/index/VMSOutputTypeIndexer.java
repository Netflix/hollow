package com.netflix.vms.transformer.index;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VMSOutputTypeIndexer {
    private final String name;
    private final HollowReadStateEngine stateEngine;
    private final Map<String, Object> indexMap; // Type name to Index

    public VMSOutputTypeIndexer(String name, HollowReadStateEngine stateEngine) {
        this.name = name;
        this.stateEngine = stateEngine;

        ExecutorService executor = new SimultaneousExecutor();

        try {
            Map<String, Object> indexMap = new HashMap<String, Object>();

            submitIndexingJobs(stateEngine, executor, indexMap);
            gatherResultsFromIndexingJobs(indexMap);

            this.indexMap = indexMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    public String getName() {
        return name;
    }

    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    public HollowPrimaryKeyIndex getPrimaryKeyIndex(String type) {
        return (HollowPrimaryKeyIndex) indexMap.get(type);
    }

    private void submitIndexingJobs(HollowReadStateEngine stateEngine, ExecutorService executor, Map<String, Object> indexMap) {
        for (OutputTypeConfig spec : OutputTypeConfig.values()) {
            indexMap.put(spec.getType(), primaryKeyIdx(executor, stateEngine, spec));
        }
    }

    @SuppressWarnings("unchecked")
    private void gatherResultsFromIndexingJobs(Map<String, Object> indexMap) throws InterruptedException, ExecutionException {
        for (Map.Entry<String, Object> futureEntry : indexMap.entrySet()) {
            Future<Object> future = (Future<Object>) futureEntry.getValue();
            futureEntry.setValue(future.get());
        }
    }

    private Future<HollowPrimaryKeyIndex> primaryKeyIdx(ExecutorService executor, final HollowReadStateEngine stateEngine, final OutputTypeConfig spec) {
        return executor.submit(new Callable<HollowPrimaryKeyIndex>() {
            @Override
            public HollowPrimaryKeyIndex call() {
                return new HollowPrimaryKeyIndex(stateEngine,
                        spec.getType(),
                        spec.getKeyFieldPaths());
            }
        });
    }
}