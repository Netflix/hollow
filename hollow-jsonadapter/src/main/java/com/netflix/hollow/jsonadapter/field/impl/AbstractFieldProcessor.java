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
package com.netflix.hollow.jsonadapter.field.impl;

import com.netflix.hollow.jsonadapter.field.FieldProcessor;

public abstract class AbstractFieldProcessor implements FieldProcessor {

    protected final String entityName;
    protected final String fieldName;

    public AbstractFieldProcessor(String entityName, String fieldName) {
        this.entityName = entityName;
        this.fieldName = fieldName;
    }

    @Override
    public String getEntityName() {
        return this.entityName;
    }


    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        AbstractFieldProcessor other = (AbstractFieldProcessor) obj;
        if(entityName == null) {
            if(other.entityName != null) return false;
        } else if(!entityName.equals(other.entityName)) return false;
        if(fieldName == null) {
            if(other.fieldName != null) return false;
        } else if(!fieldName.equals(other.fieldName)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractFieldProcessor [entityName=").append(entityName).append(", fieldName=").append(fieldName).append("]");
        return builder.toString();
    }
}
