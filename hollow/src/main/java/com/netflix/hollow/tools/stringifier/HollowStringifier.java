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
package com.netflix.hollow.tools.stringifier;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.io.IOException;
import java.io.Writer;

@SuppressWarnings("rawtypes")
public interface HollowStringifier<T extends HollowStringifier> {
    String NEWLINE = "\n";
    String INDENT = "  ";

    /**
     * Exclude specified object types (replace output with null).
     *
     * @param types the object type names
     * @return this stringifier
     */
    T addExcludeObjectTypes(String... types);

    /**
     * Create a String representation of the specified {@link HollowRecord}.
     *
     * @param record the record
     * @return the string representation
     */
    String stringify(HollowRecord record);

    /**
     * Writes a String representation of the specified {@link HollowRecord} to the provided Writer.
     *
     * @param writer the writer
     * @param record the record
     * @throws IOException thrown if there is an error writing to the Writer
     */
    void stringify(Writer writer, HollowRecord record) throws IOException;

    /**
     * Writes a String representation of the specified collection of {@link HollowRecord} to the provided Writer.
     *
     * @param writer the writer
     * @param records the records
     * @throws IOException thrown if there is an error writing to the Writer
     */
    default void stringify(Writer writer, Iterable<HollowRecord> records) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Create a String representation of the record in the provided dataset, of the given type, with the specified ordinal.
     *
     * @param dataAccess the data access
     * @param type the type name
     * @param ordinal the oridinal
     * @return the string representation
     */
    String stringify(HollowDataAccess dataAccess, String type, int ordinal);

    /**
     * Writes a String representation of the record in the provided dataset, of the given type, with the specified ordinal.
     *
     * @param writer the writer
     * @param dataAccess the data access
     * @param type the type name
     * @param ordinal the oridinal
     * @throws IOException thrown if there is an error writing to the Writer
     */
    void stringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal)
            throws IOException;
}
