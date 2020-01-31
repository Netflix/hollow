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

import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A schema for a List record type.
 * 
 * @see HollowSchema
 * 
 * @author dkoszewnik
 *
 */
public class HollowListSchema extends HollowCollectionSchema {

    private final String elementType;

    private HollowTypeReadState elementTypeState;

    public HollowListSchema(String schemaName, String elementType) {
        super(schemaName);
        this.elementType = elementType;
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    public void setElementTypeState(HollowTypeReadState typeState) {
        this.elementTypeState = typeState;
    }

    @Override
    public HollowTypeReadState getElementTypeState() {
        return elementTypeState;
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.LIST;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if(!(other instanceof HollowListSchema))
            return false;
        HollowListSchema otherSchema = (HollowListSchema)other;
        if(!getName().equals(otherSchema.getName()))
            return false;
        
        return getElementType().equals(otherSchema.getElementType());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSchemaType().hashCode();
        result = 31 * result + elementType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getName() + " List<" + getElementType() + ">;";
    }
    
    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        dos.write(SchemaType.LIST.getTypeId());
        dos.writeUTF(getName());
        dos.writeUTF(getElementType());
    }

}
