/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.schema;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HollowSchemaSorter {

    /**
     * Dependency types come before dependent types
     *  
     * @param dataset the data set
     * @return the dependent schema
     */
    public static List<HollowSchema> dependencyOrderedSchemaList(HollowDataset dataset) {
        return dependencyOrderedSchemaList(dataset.getSchemas());
    }

    /**
     * Dependency types come before dependent types
     *
     * @param schemas the schema
     * @return the dependent schema
     */
    public static List<HollowSchema> dependencyOrderedSchemaList(Collection<HollowSchema> schemas) {
        DependencyIndex idx = new DependencyIndex();
        Map<String, HollowSchema> schemaMap = new HashMap<String, HollowSchema>();
        for(HollowSchema schema : schemas) {
            schemaMap.put(schema.getName(), schema);
            idx.indexSchema(schema, schemas);
        }

        List<HollowSchema> orderedSchemas = new ArrayList<HollowSchema>();

        while(idx.hasMoreTypes())
            orderedSchemas.add(schemaMap.get(idx.getNextType()));

        return orderedSchemas;
    }


    private static class DependencyIndex {
        private final Map<String, Set<String>> dependencyIndex;
        private final Map<String, Set<String>> reverseDependencyIndex;

        public DependencyIndex() {
            this.dependencyIndex = new HashMap<String, Set<String>>();
            this.reverseDependencyIndex = new HashMap<String, Set<String>>();
        }

        public boolean hasMoreTypes() {
            for(Map.Entry<String, Set<String>> entry : dependencyIndex.entrySet()) {
                if(entry.getValue().isEmpty()) {
                    return true;
                }
            }

            return false;
        }

        public String getNextType() {
            List<String> availableTypes = new ArrayList<String>();
            for(Map.Entry<String, Set<String>> entry : dependencyIndex.entrySet()) {
                if(entry.getValue().isEmpty()) {
                    availableTypes.add(entry.getKey());
                }
            }

            String firstAvailableType = availableTypes.get(0);

            for(int i = 1; i < availableTypes.size(); i++) {
                if(availableTypes.get(i).compareTo(firstAvailableType) < 0)
                    firstAvailableType = availableTypes.get(i);
            }

            removeType(firstAvailableType);
            return firstAvailableType;
        }

        private void indexSchema(HollowSchema schema, Collection<HollowSchema> allSchemas) {
            if(schema instanceof HollowCollectionSchema) {
                String elementType = ((HollowCollectionSchema) schema).getElementType();
                addDependency(schema.getName(), elementType, allSchemas);
            } else if(schema instanceof HollowMapSchema) {
                String keyType = ((HollowMapSchema) schema).getKeyType();
                String valueType = ((HollowMapSchema) schema).getValueType();

                addDependency(schema.getName(), keyType, allSchemas);
                addDependency(schema.getName(), valueType, allSchemas);
            } else if(schema instanceof HollowObjectSchema) {
                HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
                for(int i = 0; i < objectSchema.numFields(); i++) {
                    if(objectSchema.getFieldType(i) == FieldType.REFERENCE) {
                        String refType = objectSchema.getReferencedType(i);
                        addDependency(schema.getName(), refType, allSchemas);
                    }
                }
            }

            getList(schema.getName(), dependencyIndex);
            getList(schema.getName(), reverseDependencyIndex);
        }

        private void removeType(String type) {
            Set<String> dependents = reverseDependencyIndex.remove(type);

            for(String dependent : dependents)
                dependencyIndex.get(dependent).remove(type);

            dependencyIndex.remove(type);
        }

        private void addDependency(String dependent, String dependency, Collection<HollowSchema> allSchemas) {
            if(schemaExists(dependency, allSchemas)) {
                getList(dependent, dependencyIndex).add(dependency);
                getList(dependency, reverseDependencyIndex).add(dependent);
            }
        }

        private boolean schemaExists(String schemaName, Collection<HollowSchema> allSchemas) {
            for(HollowSchema schema : allSchemas) {
                if(schema.getName().equals(schemaName))
                    return true;
            }
            return false;
        }

        private Set<String> getList(String key, Map<String, Set<String>> dependencyIndex2) {
            Set<String> list = dependencyIndex2.get(key);
            if(list == null) {
                list = new HashSet<String>();
                dependencyIndex2.put(key, list);
            }
            return list;
        }
    }

    /**
     * @param stateEngine the state engine
     * @param dependentType the dependent type name
     * @param dependencyType the dependency type name
     * @return Whether or not the dependencyType is equal to, referenced by, or transitively referenced by the dependentType. 
     */
    public static boolean typeIsTransitivelyDependent(HollowStateEngine stateEngine, String dependentType, String dependencyType) {
        if(dependentType.equals(dependencyType))
            return true;

        HollowSchema dependentTypeSchema = stateEngine.getSchema(dependentType);

        if(dependentTypeSchema == null)
            return false;

        switch(dependentTypeSchema.getSchemaType()) {
            case OBJECT:
                HollowObjectSchema objectSchema = (HollowObjectSchema) dependentTypeSchema;

                for(int i = 0; i < objectSchema.numFields(); i++) {
                    if(objectSchema.getFieldType(i) == FieldType.REFERENCE) {
                        if(typeIsTransitivelyDependent(stateEngine, objectSchema.getReferencedType(i), dependencyType))
                            return true;
                    }
                }

                break;
            case LIST:
            case SET:
                return typeIsTransitivelyDependent(stateEngine, ((HollowCollectionSchema) dependentTypeSchema).getElementType(), dependencyType);
            case MAP:
                return typeIsTransitivelyDependent(stateEngine, ((HollowMapSchema) dependentTypeSchema).getKeyType(), dependencyType)
                        || typeIsTransitivelyDependent(stateEngine, ((HollowMapSchema) dependentTypeSchema).getValueType(), dependencyType);
        }

        return false;
    }

}
