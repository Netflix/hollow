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
package com.netflix.hollow.tools.filter;

import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import java.io.DataOutputStream;
import java.io.OutputStream;


class FilteredHollowBlobWriterStreamAndFilter {
    private final DataOutputStream dos;
    private final HollowFilterConfig config;

    FilteredHollowBlobWriterStreamAndFilter(DataOutputStream dos, HollowFilterConfig config) {
        this.dos = dos;
        this.config = config;
    }

    public DataOutputStream getStream() {
        return dos;
    }

    public HollowFilterConfig getConfig() {
        return config;
    }

    public static DataOutputStream[] streamsOnly(FilteredHollowBlobWriterStreamAndFilter[] streamAndFilters) {
        DataOutputStream streams[] = new DataOutputStream[streamAndFilters.length];

        for(int i = 0; i < streams.length; i++)
            streams[i] = streamAndFilters[i].getStream();

        return streams;
    }

    public static FilteredHollowBlobWriterStreamAndFilter[] combine(OutputStream streams[], HollowFilterConfig configs[]) {
        if(streams.length != configs.length)
            throw new IllegalArgumentException("Must provide exactly the same number of streams as configs");

        FilteredHollowBlobWriterStreamAndFilter streamAndFilters[] = new FilteredHollowBlobWriterStreamAndFilter[streams.length];

        for(int i = 0; i < streams.length; i++)
            streamAndFilters[i] = new FilteredHollowBlobWriterStreamAndFilter(new DataOutputStream(streams[i]), configs[i]);

        return streamAndFilters;
    }

    public static FilteredHollowBlobWriterStreamAndFilter[] withType(String typeName, FilteredHollowBlobWriterStreamAndFilter[] allStreamAndFilters) {
        int countConfigsWithType = 0;

        for(int i = 0; i < allStreamAndFilters.length; i++) {
            if(allStreamAndFilters[i].getConfig().doesIncludeType(typeName))
                countConfigsWithType++;
        }

        FilteredHollowBlobWriterStreamAndFilter[] streamAndFiltersWithType = new FilteredHollowBlobWriterStreamAndFilter[countConfigsWithType];
        int withTypeCounter = 0;

        for(int i = 0; i < allStreamAndFilters.length; i++) {
            if(allStreamAndFilters[i].getConfig().doesIncludeType(typeName))
                streamAndFiltersWithType[withTypeCounter++] = allStreamAndFilters[i];
        }

        return streamAndFiltersWithType;
    }

}
