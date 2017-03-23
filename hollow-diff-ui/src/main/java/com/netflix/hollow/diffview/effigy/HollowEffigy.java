/*
 *
 *  Copyright 2016 Netflix, Inc.
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
import com.netflix.hollow.tools.diff.HollowDiffNodeIdentifier;
import java.util.ArrayList;
import java.util.List;

/**
 * The HollowEffigy is an Object-based representation of a Hollow record,
 * it is used in creation of the diff HTML, and for holding representations
 * of expired hollow records in a state chain in the diff history.
 */
public class HollowEffigy {

    private final String objectType;
    private final HollowTypeDataAccess dataAccess;
    private final int ordinal;
    private final List<Field> fields;

    public HollowEffigy(String objectType) {
        this.objectType = objectType;
        this.dataAccess = null;
        this.ordinal = -1;
        this.fields = new ArrayList<Field>();
    }
    
    public HollowEffigy(HollowTypeDataAccess dataAccess, int ordinal) {
        this.objectType = null;
        this.dataAccess = dataAccess;
        this.ordinal = ordinal;
        this.fields = new ArrayList<Field>();
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
        return fields;
    }

    public static class Field {
        private final HollowDiffNodeIdentifier fieldIdentifier;
        private final Object value;
        private final int hashCode;

        public Field(HollowDiffNodeIdentifier fieldIdentifier, Object value) {
            this.fieldIdentifier = fieldIdentifier;
            this.value = value;
            this.hashCode = 31 * fieldIdentifier.hashCode() + (value == null ? 0 : value.hashCode());
        }

        public HollowDiffNodeIdentifier getFieldNodeIndex() {
            return fieldIdentifier;
        }

        public String getFieldName() {
            return fieldIdentifier.getViaFieldName();
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
                Field otherField = (Field)other;
                if(this.fieldIdentifier.equals(otherField.fieldIdentifier)) {
                    if(this.value == null)
                        return otherField.value == null;
                    return this.value.equals(otherField.value);
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

}
