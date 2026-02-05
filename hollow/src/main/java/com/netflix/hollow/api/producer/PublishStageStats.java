/*
 *  Copyright 2016-2026 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMapStats;

import java.util.Map;

public class PublishStageStats implements StageStats {
    private final Map<String, ByteArrayOrdinalMapStats> getOrdinalMapStats;
    private PublishStageStats(Map<String, ByteArrayOrdinalMapStats> getOrdinalMapStats) {
        this.getOrdinalMapStats = getOrdinalMapStats;
    }

    public Map<String, ByteArrayOrdinalMapStats> getOrdinalMapStats() {
        return this.getOrdinalMapStats;
    }

    public static class Builder {
        private Map<String, ByteArrayOrdinalMapStats> byteArrayOrdinalMapStatsByType;
        public Builder withStats(Map<String, ByteArrayOrdinalMapStats> byteArrayOrdinalMapStatsByType) {
            this.byteArrayOrdinalMapStatsByType = byteArrayOrdinalMapStatsByType;
            return this;
        }

        public PublishStageStats build() {
            return new PublishStageStats(byteArrayOrdinalMapStatsByType);
        }
    }
}
