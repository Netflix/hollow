package com.netflix.vms.transformer.index;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.TransformerContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VMSTransformerIndexer {

    private final Map<IndexSpec, Object> indexMap;
    private final TransformerContext ctx;

    public VMSTransformerIndexer(HollowReadStateEngine stateEngine, TransformerContext ctx) {
    	this.ctx = ctx;
        ExecutorService executor = new SimultaneousExecutor(getClass(), "index");

        try {
            Map<IndexSpec, Object> indexMap = new HashMap<IndexSpec, Object>();

            submitIndexingJobs(stateEngine, executor, indexMap);
            gatherResultsFromIndexingJobs(indexMap);

            this.indexMap = indexMap;
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    public HollowPrimaryKeyIndex getPrimaryKeyIndex(IndexSpec spec) {
        return (HollowPrimaryKeyIndex) indexMap.get(spec);
    }

    public HollowHashIndex getHashIndex(IndexSpec spec) {
        return (HollowHashIndex) indexMap.get(spec);
    }

    private void submitIndexingJobs(HollowReadStateEngine stateEngine, ExecutorService executor, Map<IndexSpec, Object> indexMap) {
        for(IndexSpec spec : IndexSpec.values()) {
            switch(spec.getIndexType()) {
            case PRIMARY_KEY:
                indexMap.put(spec, primaryKeyIdx(executor, stateEngine, spec));
                break;
            case HASH:
                indexMap.put(spec, hashIdx(executor, stateEngine, spec));
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void gatherResultsFromIndexingJobs(Map<IndexSpec, Object> indexMap) throws InterruptedException, ExecutionException {
        for(Map.Entry<IndexSpec, Object> futureEntry : indexMap.entrySet()) {
            Future<Object> future = (Future<Object>) futureEntry.getValue();
            futureEntry.setValue(future.get());
        }
    }

    private Future<HollowHashIndex> hashIdx(ExecutorService executor, final HollowReadStateEngine stateEngine, final IndexSpec spec) {
        return executor.submit(new Callable<HollowHashIndex>() {
            public HollowHashIndex call() {
                return new HollowHashIndex(stateEngine,
                        spec.getParameters()[0],
                        spec.getParameters()[1],
                        Arrays.copyOfRange(spec.getParameters(), 2, spec.getParameters().length));
            }
        });
    }

    private Future<HollowPrimaryKeyIndex> primaryKeyIdx(ExecutorService executor, final HollowReadStateEngine stateEngine, final IndexSpec spec) {
        return executor.submit(new Callable<HollowPrimaryKeyIndex>() {
            public HollowPrimaryKeyIndex call() {
                try {
                     HollowPrimaryKeyIndex hollowPrimaryKeyIndex = new HollowPrimaryKeyIndex(stateEngine,
                            spec.getParameters()[0],
                            Arrays.copyOfRange(spec.getParameters(), 1, spec.getParameters().length));
                     return hollowPrimaryKeyIndex;
                }catch(Throwable th) {
                    throw new RuntimeException(th);
                }
            }
        });
    }

}
