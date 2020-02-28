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
package com.netflix.hollow.core.read.filter;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A HollowFilterConfig specifies a subset of the available fields in a data model.  It can be specified 
 * as either an exclude filter, or an include filter. 
 * <p>
 * An exclude filter specifies fields which are excluded from the subset.  With an exclude filter,
 * all fields are included by default and only the identified field are excluded.
 * <p>
 * An include filter (the default) specifies fields which are included in the subset.  With an include filter,
 * all fields are excluded by default and only the identified fields are included.
 * <p>
 * A HollowFilterConfig can be used to reduce the heap footprint on consumers, either with 
 * {@link HollowConsumer.Builder#withFilterConfig(HollowFilterConfig)} if using a {@link HollowConsumer} or with
 * {@link HollowBlobReader#readSnapshot(java.io.InputStream, HollowFilterConfig)} if using a {@link HollowReadStateEngine}
 * directly.
 * <p>
 * Note that when using this to configure a filter for a consumer, only the snapshot must be filtered.  Subsequent
 * deltas will automatically use the same filter.
 *
 * <p>{@link HollowFilterConfig} is deprecated in favor of {@link TypeFilter}.</p>
 *
 * <p>{@code HollowFilterConfig} has these limitations:</p>
 *
 * <ul>
 *     <li>cannot mix inclusions and exclusions in a single filter and cannot compose filters</li>
 *     <li>recursive actions requires that callers already have the dataset's schema, leading to
 *     a chicken-and-egg situation</li>
 * </ul>
 *
 * @deprecated use {@link TypeFilter}
 */
@Deprecated
public class HollowFilterConfig implements TypeFilter {

    private final ObjectFilterConfig INCLUDE_ALL = new ObjectFilterConfig(Boolean.TRUE);
    private final ObjectFilterConfig INCLUDE_NONE = new ObjectFilterConfig(Boolean.FALSE);

    private final boolean isExcludeFilter;
    private final Set<String> specifiedTypes;
    private final Map<String, ObjectFilterConfig> specifiedFieldConfigs;

    /**
     * Create a new <i>include</i> filter.
     */
    public HollowFilterConfig() {
        this(false);
    }

    /**
     * Create a new filter
     * @param isExcludeFilter true for an <i>exclude</i> filter, false for an <i>include</i> filter.
     */
    public HollowFilterConfig(boolean isExcludeFilter) {
        this.isExcludeFilter = isExcludeFilter;
        this.specifiedTypes = new HashSet<String>();
        this.specifiedFieldConfigs = new HashMap<String, ObjectFilterConfig>();
    }

    /**
     * Add a type.  All fields in the type will be either excluded or included, depending on
     * whether this is an exclude or include filter, respectively.
     *
     * @param type the type name
     */
    public void addType(String type) {
        specifiedTypes.add(type);
    }

    /**
     * Add a type, plus recursively add any directly or transitively referenced types.
     * 
     * All fields in these types will be either excluded or included, depending on whether
     * this is an exclude or include filter, respectively.
     * 
     * @param type A type from the data model.
     * @param schemas All schemas from the data model.
     */
    public void addTypeRecursive(String type, Collection<HollowSchema> schemas) {
        addTypeRecursive(type, mapSchemas(schemas));
    }

    /**
     * Add a type, plus recursively add any directly or transitively referenced types.
     * 
     * All fields in these types will be either excluded or included, depending on whether
     * this is an exclude or include filter, respectively.
     * 
     * 
     * @param type A type from the data model.
     * @param schemas A map of typeName to schema including all schemas for this data model.
     */
    public void addTypeRecursive(String type, Map<String, HollowSchema> schemas) {
        addType(type);
        HollowSchema schema = schemas.get(type);
        switch(schema.getSchemaType()) {
        case OBJECT:
            HollowObjectSchema objSchema = (HollowObjectSchema)schema;
            for(int i=0;i<objSchema.numFields();i++) {
                if(objSchema.getFieldType(i) == FieldType.REFERENCE)
                    addTypeRecursive(objSchema.getReferencedType(i), schemas);
            }
            break;
        case MAP:
            addTypeRecursive(((HollowMapSchema)schema).getKeyType(), schemas);
            addTypeRecursive(((HollowMapSchema)schema).getValueType(), schemas);
            break;
        case LIST:
        case SET:
            addTypeRecursive(((HollowCollectionSchema)schema).getElementType(), schemas);
            break;
        }
    }

    /**
     * Add an individual field from an OBJECT schema.  This field will be either 
     * excluded or included, depending on whether this is an exclude or include filter, respectively.
     * 
     * @param type The OBJECT type from the data model
     * @param objectField The field in the specified type to either include or exclude.
     */
    public void addField(String type, String objectField) {
        ObjectFilterConfig typeConfig = specifiedFieldConfigs.get(type);
        if(typeConfig == null) {
            typeConfig = new ObjectFilterConfig();
            specifiedFieldConfigs.put(type, typeConfig);
        }
        typeConfig.addField(objectField);
    }

    /**
     * Add an individual field from an OBJECT schema, plus recursively add any directly or transitively referenced types.
     * This field will be either excluded or included, depending on whether this is an exclude or include filter, respectively.
     * 
     * @param type The OBJECT type from the data model
     * @param objectField The field in the specified type to either include or exclude.
     * @param schemas All schemas from the data model.
     */
    public void addFieldRecursive(String type, String objectField, Collection<HollowSchema> schemas) {
        addFieldRecursive(type, objectField, mapSchemas(schemas));
    }

    /**
     * Add an individual field from an OBJECT schema, plus recursively add any directly or transitively referenced types.
     * This field will be either excluded or included, depending on whether this is an exclude or include filter, respectively.
     * 
     * @param type The OBJECT type from the data model
     * @param objectField The field in the specified type to either include or exclude.
     * @param schemas A map of typeName to schema including all schemas for this data model.
     */
    public void addFieldRecursive(String type, String objectField, Map<String, HollowSchema> schemas) {
        addField(type, objectField);
        HollowObjectSchema schema = (HollowObjectSchema)schemas.get(type);
        if(schema.getFieldType(objectField) == FieldType.REFERENCE) {
            addTypeRecursive(schema.getReferencedType(objectField), schemas);
        }
    }

    /**
     * @param type A type from the data model
     * @return whether or not this filter includes the specified type.
     */
    public boolean doesIncludeType(String type) {
        if(isExcludeFilter)
            return !specifiedTypes.contains(type);

        return specifiedTypes.contains(type) || specifiedFieldConfigs.containsKey(type);
    }

    /**
     * @return true if this is an <i>exclude</i> filter.  False otherwise.
     */
    public boolean isExcludeFilter() {
        return isExcludeFilter;
    }

    public int numSpecifiedTypes() {
        return specifiedTypes.size();
    }
    
    public Set<String> getSpecifiedTypes() {
        return specifiedTypes;
    }

    public ObjectFilterConfig getObjectTypeConfig(String type) {
        ObjectFilterConfig typeConfig = specifiedFieldConfigs.get(type);
        if(typeConfig != null)
            return typeConfig;

        if(isExcludeFilter) {
            if(specifiedTypes.contains(type))
                return INCLUDE_NONE;
            return INCLUDE_ALL;
        } else {
            if(specifiedTypes.contains(type))
                return INCLUDE_ALL;
            return INCLUDE_NONE;
        }
    }

    @Override
    public boolean includes(String type) {
        return doesIncludeType(type);
    }

    @Override
    public boolean includes(String type, String field) {
        return getObjectTypeConfig(type).includesField(field);
    }

    public class ObjectFilterConfig {
        private final Boolean alwaysAnswer;
        private final Set<String> specifiedFields;

        public ObjectFilterConfig() {
            this(null);
        }

        public ObjectFilterConfig(Boolean alwaysAnswer) {
            this.specifiedFields = new HashSet<String>();
            this.alwaysAnswer = alwaysAnswer;
        }

        private void addField(String fieldName) {
            specifiedFields.add(fieldName);
        }

        public boolean includesField(String field) {
            if(alwaysAnswer != null)
                return alwaysAnswer.booleanValue();

            if(isExcludeFilter)
                return !specifiedFields.contains(field);
            return specifiedFields.contains(field);
        }

        public int numIncludedFields() {
            return specifiedFields.size();
        }
    }

    /**
     * Write this HollowFilterConfig to a human-readable, and parseable String.
     * 
     * This can be used to serialize a configuration.  The returned String can be used to
     * recreate the {@link HollowFilterConfig} using {@link HollowFilterConfig#fromString(String)}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(isExcludeFilter ? "EXCLUDE" : "INCLUDE");

        for(String type : specifiedTypes) {
            builder.append('\n').append(type);
        }

        for(Map.Entry<String, ObjectFilterConfig> entry : specifiedFieldConfigs.entrySet()) {
            String typeName = entry.getKey();
            ObjectFilterConfig typeConfig = entry.getValue();
            if(typeConfig.specifiedFields.isEmpty()) {
                builder.append('\n').append(typeName);
            } else {
                for(String field : typeConfig.specifiedFields) {
                    builder.append('\n').append(typeName).append('.').append(field);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Parse a HollowFilterConfig from the specified String.  The String should contain multiple lines.
     * The first line should be either "EXCLUDE" or "INCLUDE".  Subsequent lines should be one of the following:
     * <ul>
     * <li>&lt;typeName&gt;</li>
     * <li>&lt;typeName&gt;.&lt;fieldName&gt;</li>
     * </ul>
     *
     * @param conf the configuration as a string
     * @return the filter configuration
     */
    public static HollowFilterConfig fromString(String conf) {
        String lines[] = conf.split("\n");

        HollowFilterConfig config = new HollowFilterConfig("EXCLUDE".equals(lines[0]));

        for(int i=1;i<lines.length;i++) {
            int delimiterIdx = lines[i].indexOf('.');
            if(delimiterIdx == -1) {
                config.addType(lines[i]);
            } else {
                String type = lines[i].substring(0, delimiterIdx);
                String field = lines[i].substring(delimiterIdx+1);
                config.addField(type, field);
            }
        }

        return config;
    }

    private Map<String, HollowSchema> mapSchemas(Collection<HollowSchema> schemas) {
        Map<String, HollowSchema> schemaMap = new HashMap<String, HollowSchema>();
        for(HollowSchema schema : schemas) {
            schemaMap.put(schema.getName(), schema);
        }
        return schemaMap;
    }

}
