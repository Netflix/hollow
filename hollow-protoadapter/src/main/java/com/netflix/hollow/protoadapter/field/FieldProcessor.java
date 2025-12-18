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
package com.netflix.hollow.protoadapter.field;

import com.google.protobuf.Message;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.io.IOException;

/**
 * Interface for custom field processors that can handle specific Protocol Buffer message fields.
 */
public interface FieldProcessor {
    
    /**
     * Get the entity name this processor is associated with.
     * 
     * @return The entity name
     */
    String getEntityName();
    
    /**
     * Get the field name this processor is associated with.
     * 
     * @return The field name
     */
    String getFieldName();
    
    /**
     * Process a field from a Protocol Buffer message.
     * 
     * @param message The Protocol Buffer message
     * @param stateEngine The Hollow write state engine
     * @param writeRecord The Hollow object write record
     * @throws IOException If an error occurs during processing
     */
    void processField(Message message, HollowWriteStateEngine stateEngine, HollowObjectWriteRecord writeRecord) throws IOException;
}
