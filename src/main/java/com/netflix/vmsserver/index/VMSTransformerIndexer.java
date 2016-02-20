package com.netflix.vmsserver.index;

import static com.netflix.vmsserver.index.IndexSpec.IndexType.HASH;
import static com.netflix.vmsserver.index.IndexSpec.IndexType.PRIMARY_KEY;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class VMSTransformerIndexer {

    public static final IndexSpec SUPPLEMENTAL = new IndexSpec(PRIMARY_KEY, "Trailer", "movieId");
    public static final IndexSpec VIDEO_RIGHTS = new IndexSpec(PRIMARY_KEY, "VideoRights", "movieId", "countryCode.value");
    public static final IndexSpec ROLLOUT_VIDEO_TYPE = new IndexSpec(PRIMARY_KEY, "Rollout", "movieId", "rolloutType.value");

    public static final IndexSpec VIDEO_COUNTRY_FUTURE = new IndexSpec(HASH, "VideoType", "type.element", "videoId", "type.element.countryCode.value");


    private final Map<IndexSpec, Object> indexMap;

    public VMSTransformerIndexer(HollowReadStateEngine stateEngine, ExecutorService executor) {
        try {
            List<IndexSpec> definedIndexSpecs = retrieveDefinedIndexSpecsViaReflection();

            Map<IndexSpec, Object> indexMap = new HashMap<IndexSpec, Object>();

            submitIndexingJobs(stateEngine, executor, definedIndexSpecs, indexMap);
            gatherResultsFromIndexingJobs(indexMap);

            this.indexMap = indexMap;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HollowPrimaryKeyIndex getPrimaryKeyIndex(IndexSpec spec) {
        return (HollowPrimaryKeyIndex) indexMap.get(spec);
    }

    public HollowHashIndex getHashIndex(IndexSpec spec) {
        return (HollowHashIndex) indexMap.get(spec);
    }

    private List<IndexSpec> retrieveDefinedIndexSpecsViaReflection() throws IllegalAccessException {
        List<IndexSpec> definedIndexSpecs = new ArrayList<IndexSpec>();
        Field[] declaredFields = this.getClass().getDeclaredFields();

        for(Field field : declaredFields) {
            if(field.getType() == IndexSpec.class && Modifier.isStatic(field.getModifiers())) {
                definedIndexSpecs.add((IndexSpec)field.get(null));
            }
        }
        return definedIndexSpecs;
    }

    private void submitIndexingJobs(HollowReadStateEngine stateEngine, ExecutorService executor, List<IndexSpec> definedIndexSpecs, Map<IndexSpec, Object> indexMap) {
        for(IndexSpec spec : definedIndexSpecs) {
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
                return new HollowPrimaryKeyIndex(stateEngine,
                        spec.getParameters()[0],
                        Arrays.copyOfRange(spec.getParameters(), 1, spec.getParameters().length));
            }
        });
    }

}
