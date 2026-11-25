package com.netflix.hollow.core.util;

import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;

import java.util.Comparator;
import java.util.List;

public final class StateEngineUtil {

    /**
     * Sort Primary Key based on the schemas of specified HollowStateEngine
     */
    public static List<PrimaryKey> sortPrimaryKeys(List<PrimaryKey> primaryKeys, HollowStateEngine stateEngine) {
        if (primaryKeys.isEmpty()) return primaryKeys;

        final List<HollowSchema> dependencyOrderedSchemas = HollowSchemaSorter.dependencyOrderedSchemaList(stateEngine.getSchemas());
        primaryKeys.sort(new Comparator<PrimaryKey>() {
            public int compare(PrimaryKey o1, PrimaryKey o2) {
                return schemaDependencyIdx(o1) - schemaDependencyIdx(o2);
            }

            private int schemaDependencyIdx(PrimaryKey key) {
                for (int i = 0; i < dependencyOrderedSchemas.size(); i++) {
                    if (dependencyOrderedSchemas.get(i).getName().equals(key.getType()))
                        return i;
                }
                throw new IllegalArgumentException("Primary key defined for non-existent type: " + key.getType());
            }
        });

        return primaryKeys;
    }
}