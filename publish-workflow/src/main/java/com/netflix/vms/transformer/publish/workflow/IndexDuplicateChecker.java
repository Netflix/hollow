package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IndexDuplicateChecker {

    private final HollowReadStateEngine stateEngine;
    private final Map<String, Collection<Object[]>> dupKeyInTypeMap;

    public IndexDuplicateChecker(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.dupKeyInTypeMap = new HashMap<>();
    }

    public void checkDuplicates(OutputTypeConfig... types) {
        for (OutputTypeConfig type : types) {
            checkIndex(type.getType(), type.getKeyFieldPaths());
        }
    }
    
    public void checkIndex(String type, String... keyFieldPaths) {
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(stateEngine, type, keyFieldPaths);
        Collection<Object[]> duplicateKeys = idx.getDuplicateKeys();
        if(!duplicateKeys.isEmpty())
            dupKeyInTypeMap.put(type, duplicateKeys);
    }

    public void checkDuplicates() {
        for (OutputTypeConfig type : OutputTypeConfig.values()) {
            checkIndex(type.getType(), type.getKeyFieldPaths());
        }
    }

    public boolean wasDupKeysDetected() {
        return !dupKeyInTypeMap.isEmpty();
    }

    /**
     * Return Empty List if there are no duplicate keys detected; otherwise, the list with the type
     */
    public Map<String, Collection<Object[]>> getResults() {
        return dupKeyInTypeMap;
    }
}