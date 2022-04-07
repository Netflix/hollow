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
package com.netflix.hollow.diffview.effigy;

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import java.util.ArrayList;
import java.util.List;

/**
 * The HollowEffigy is an Object-based representation of a Hollow record,
 * it is used in creation of the diff HTML.
 */
public class HollowEffigy {

    private final HollowEffigyFactory factory;
    private final String objectType;

    final HollowTypeDataAccess dataAccess;
    final int ordinal;
    
    private List<Field> fields;

    public HollowEffigy(String objectType) {
        this.factory = null;
        this.objectType = objectType;
        this.dataAccess = null;
        this.ordinal = -1;
        this.fields = new ArrayList<Field>();
    }
    
    public HollowEffigy(HollowEffigyFactory factory, HollowTypeDataAccess dataAccess, int ordinal) {
        this.factory = factory;
        this.objectType = null;
        this.dataAccess = dataAccess;
        this.ordinal = ordinal;
    }

    public void add(HollowEffigy.Field field) {
        fields.add(field);
    }
    
    public String getObjectType() {
        if(objectType != null)
            return objectType;
        return dataAccess.getSchema().getName();
    }
    
    public HollowTypeDataAccess getDataAccess() {
        return dataAccess;
    }
    
    public int getOrdinal() {
        return ordinal;
    }

    public List<Field> getFields() {
        if(fields == null)
            fields = factory.createFields(this);
        return fields;
    }

    public static class Field {
        private final String fieldName;
        private final String typeName;
        private final Object value;
        private final int hashCode;

        public Field(String fieldName, HollowEffigy value) {
            this(fieldName, value.getObjectType(), value);
        }
        
        public Field(String fieldName, String typeName, Object value) {
            this.fieldName = fieldName;
            this.typeName = typeName;
            this.value = value;
            this.hashCode = 31 * (31 * (fieldName == null ? 0 : fieldName.hashCode()) + typeName.hashCode()) + (value == null ? 0 : value.hashCode());
        }

        public String getTypeName() {
            return typeName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getValue() {
            return value;
        }

        public boolean isLeafNode() {
            return !(value instanceof HollowEffigy);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object other) {
            if(this == other)
                return true;

            if(other instanceof Field) {
                if(this.fieldName.equals(((Field) other).fieldName) && this.typeName.equals(((Field) other).typeName)) {
                    if(this.value == null)
                        return ((Field) other).value == null;
                    return this.value.equals(((Field) other).value);
                }
            }

            return false;
        }
    }

    public static enum CollectionType {
        NONE,
        MAP,
        COLLECTION
    }

    @Override
    public int hashCode() {
        int hashcode = 31 + (fields == null ? 0 : fields.hashCode());
        return hashcode;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other)
            return true;

        if(other instanceof HollowEffigy) {
            HollowEffigy otherEffigy = (HollowEffigy) other;
            if (this.fields == null && otherEffigy.fields == null) {
                return true;
            }
            return this.fields.equals(otherEffigy.fields);
        }

        return false;
    }
}
