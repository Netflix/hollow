package com.netflix.vms.transformer.index;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.read.engine.HollowTypeReadState;
import com.netflix.hollow.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VMSOutputTypeIndexer {
    private final String name;
    private final HollowReadStateEngine stateEngine;
    private final Set<OutputTypeConfig> types;
    private final Map<String, Object> indexMap; // Type name to Index

    public VMSOutputTypeIndexer(String name, HollowReadStateEngine stateEngine) {
        this(name, stateEngine, EnumSet.allOf(OutputTypeConfig.class));
    }

    public VMSOutputTypeIndexer(String name, HollowReadStateEngine stateEngine, Set<OutputTypeConfig> types) {
        this.name = name;
        this.stateEngine = stateEngine;
        this.types = types;

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
        for (OutputTypeConfig spec : types) {
            String typeName = spec.getType();
            HollowTypeReadState typeState = stateEngine.getTypeState(typeName);
            if (typeState == null) continue;

            indexMap.put(typeName, primaryKeyIdx(executor, stateEngine, spec));
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