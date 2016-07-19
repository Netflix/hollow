package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;

import java.util.ArrayList;
import java.util.List;

public class IndexDuplicateChecker {

    private final HollowReadStateEngine stateEngine;
    private final List<String> dupKeyInTypeList;

    public IndexDuplicateChecker(HollowReadStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.dupKeyInTypeList = new ArrayList<>();
    }

    public void checkIndex(String type, String... keyFieldPaths) {
        if (new HollowPrimaryKeyIndex(stateEngine, type, keyFieldPaths).containsDuplicates())
            dupKeyInTypeList.add(type);
    }

    public void checkCoreTypeDuplicates() {
        for (OutputTypeConfig type : OutputTypeConfig.CORE_TYPES) {
            checkIndex(type.getType(), type.getKeyFieldPaths());
        }
    }

    public void checkAllDuplicates() {
        for (OutputTypeConfig type : OutputTypeConfig.values()) {
            checkIndex(type.getType(), type.getKeyFieldPaths());
        }
    }

    public boolean wasDupKeysDetected() {
        return !dupKeyInTypeList.isEmpty();
    }

    /**
     * Return Empty List if there are no duplicate keys detected; otherwise, the list with the type
     */
    public List<String> getResults() {
        return dupKeyInTypeList;
    }
}