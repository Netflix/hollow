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
package com.netflix.hollow.explorer.ui.model;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowSchema;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TypeOverview {
    private static final String[] HEAP_SIZE_UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };

    private final String typeName;
    private final int numRecords;
    private final int numHoles;
    private final long approxHoleFootprint;
    private final long approxHeapFootprint;
    private final PrimaryKey primaryKey;
    private final HollowSchema schema;
    
    public TypeOverview(String typeName, int numRecords, int numHoles, long approxHoleFootprint,  long approxHeapFootprint, PrimaryKey primaryKey, HollowSchema schema) {
        this.typeName = typeName;
        this.numRecords = numRecords;
        this.numHoles = numHoles;
        this.approxHoleFootprint = approxHoleFootprint;
        this.approxHeapFootprint = approxHeapFootprint;
        this.primaryKey = primaryKey;
        this.schema = schema;
    }

    public String getTypeName() {
        return typeName;
    }
    
    public int getNumRecordsInt() {
        return numRecords;
    }

    public String getNumRecords() {
        return NumberFormat.getIntegerInstance().format(numRecords);
    }

    public int getNumHolesInt() { return numHoles; }
    public String getNumHoles() { return NumberFormat.getIntegerInstance().format(numHoles); }

    public long getApproxHoleFootprintLong() { return approxHoleFootprint; }
    public String getApproxHoleFootprint() {  return heapFootprintDisplayString(approxHoleFootprint); }

    public long getApproxHeapFootprintLong() {
        return approxHeapFootprint;
    }

    public String getApproxHeapFootprint() {
        return heapFootprintDisplayString(approxHeapFootprint);
    }

    public String getPrimaryKey() {
        return primaryKey == null ? "" : primaryKey.toString();
    }

    public String getSchema() {
        return schema.toString();
    }

    public static String heapFootprintDisplayString(long approxHeapFootprint) {
        if(approxHeapFootprint <= 0) return "0";
        int digitGroups = (int) (Math.log10(approxHeapFootprint)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(approxHeapFootprint/Math.pow(1024, digitGroups)) + " " + HEAP_SIZE_UNITS[digitGroups];
    }
}
