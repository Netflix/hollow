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
package com.netflix.hollow.core.index;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
public class HollowPrimaryKeyIndexLongevityTest {

    @Test
    public void testIndexSurvives3Updates() throws IOException {
        TestHollowConsumer longConsumer = createHollowConsumer(true);
        TestHollowConsumer shortConsumer = createHollowConsumer(false);
        HollowWriteStateEngine snapshotEngine = createSnapshot(0,5, "snapshot");
        longConsumer.applySnapshot(0, snapshotEngine);
        shortConsumer.applySnapshot(0, snapshotEngine);

        //If we were using listeners, we would have access to the ReadStateEngine + API. So we'll just use API to
        //simulate that.

        //Auto discover the keys
        HollowPrimaryKeyIndex longSnapshot0 = new HollowPrimaryKeyIndex(longConsumer.getAPI().getDataAccess(), "TypeA");
        HollowPrimaryKeyIndex shortSnapshot0 = new HollowPrimaryKeyIndex(shortConsumer.getAPI().getDataAccess(), "TypeA");
        int longOrd0 = longSnapshot0.getMatchingOrdinal(0);
        int longOrd1 = longSnapshot0.getMatchingOrdinal(1);
        int longOrd2 = longSnapshot0.getMatchingOrdinal(2);
        HollowAPI longSnapshotApi = longConsumer.getAPI();
        //All of these return non-null results. That verifies the index worked as of this snapshot.
        assertThat(longSnapshot0.getMatchingOrdinal(0)).isEqualTo(longOrd0).isNotEqualTo(-1);
        assertThat(longSnapshot0.getMatchingOrdinal(1)).isEqualTo(longOrd1).isNotEqualTo(-1);
        assertThat(longSnapshot0.getMatchingOrdinal(2)).isEqualTo(longOrd2).isNotEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(0)).isNotEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(1)).isNotEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(2)).isNotEqualTo(-1);



        //Now we do a delta. Both indexes should work
        HollowWriteStateEngine delta1Engine = createSnapshot(0,5, "delta1");
        longConsumer.applyDelta(1, delta1Engine);
        shortConsumer.applyDelta(1, delta1Engine);
        HollowPrimaryKeyIndex longDelta1 = new HollowPrimaryKeyIndex(longConsumer.getAPI().getDataAccess(), "TypeA");
        HollowPrimaryKeyIndex shortDelta1 = new HollowPrimaryKeyIndex(shortConsumer.getAPI().getDataAccess(), "TypeA");
        assertThat(longConsumer.getAPI()).isNotSameAs(longSnapshotApi);
        //The ordinals should all change because every record was updated.
        assertThat(longDelta1.getMatchingOrdinal(0)).isNotEqualTo(longOrd0).isNotEqualTo(-1);
        assertThat(longDelta1.getMatchingOrdinal(1)).isNotEqualTo(longOrd1).isNotEqualTo(-1);
        assertThat(longDelta1.getMatchingOrdinal(2)).isNotEqualTo(longOrd2).isNotEqualTo(-1);
        assertThat(shortDelta1.getMatchingOrdinal(0)).isNotEqualTo(shortSnapshot0.getMatchingOrdinal(0)).isNotEqualTo(-1);
        assertThat(shortDelta1.getMatchingOrdinal(1)).isNotEqualTo(shortSnapshot0.getMatchingOrdinal(1)).isNotEqualTo(-1);
        assertThat(shortDelta1.getMatchingOrdinal(2)).isNotEqualTo(shortSnapshot0.getMatchingOrdinal(2)).isNotEqualTo(-1);
        //All of these should continue to work.
        assertThat(longSnapshot0.getMatchingOrdinal(0)).isEqualTo(longOrd0);
        assertThat(longSnapshot0.getMatchingOrdinal(1)).isEqualTo(longOrd1);
        assertThat(longSnapshot0.getMatchingOrdinal(2)).isEqualTo(longOrd2);
        assertThat(shortSnapshot0.getMatchingOrdinal(0)).isNotEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(1)).isNotEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(2)).isNotEqualTo(-1);


        //Do another delta. The long index should work. The short index should not.
        HollowWriteStateEngine delta2Engine = createSnapshot(4,10, "delta1");
        longConsumer.applyDelta(2, delta2Engine);
        shortConsumer.applyDelta(2, delta2Engine);
        HollowPrimaryKeyIndex longDelta2 = new HollowPrimaryKeyIndex(longConsumer.getAPI().getDataAccess(), "TypeA");
        HollowPrimaryKeyIndex shortDelta2 = new HollowPrimaryKeyIndex(shortConsumer.getAPI().getDataAccess(), "TypeA");
        assertThat(longConsumer.getAPI()).isNotSameAs(longSnapshotApi);
        assertThat(longDelta2.getMatchingOrdinal(0)).isEqualTo(-1);
        assertThat(longDelta2.getMatchingOrdinal(1)).isEqualTo(-1);
        assertThat(longDelta2.getMatchingOrdinal(2)).isEqualTo(-1);
        assertThat(longDelta2.getMatchingOrdinal(5)).isNotEqualTo(-1);
        assertThat(shortDelta2.getMatchingOrdinal(0)).isEqualTo(-1);
        assertThat(shortDelta2.getMatchingOrdinal(1)).isEqualTo(-1);
        assertThat(shortDelta2.getMatchingOrdinal(2)).isEqualTo(-1);
        assertThat(shortDelta2.getMatchingOrdinal(5)).isNotEqualTo(-1);
        //Long should keep working, short should not.
        assertThat(longSnapshot0.getMatchingOrdinal(0)).isEqualTo(longOrd0).isNotEqualTo(-1);
        assertThat(longSnapshot0.getMatchingOrdinal(1)).isEqualTo(longOrd1).isNotEqualTo(-1);
        assertThat(longSnapshot0.getMatchingOrdinal(2)).isEqualTo(longOrd2).isNotEqualTo(-1);
        //We changed the id range to exclude 0-2 to ensure we don't end up with a "new" object squatting on an old ordinal
        //and the index accidentally matching.
        assertThat(shortSnapshot0.getMatchingOrdinal(0)).isEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(1)).isEqualTo(-1);
        assertThat(shortSnapshot0.getMatchingOrdinal(2)).isEqualTo(-1);
    }


    @SuppressWarnings("FieldCanBeLocal")
    @HollowPrimaryKey(fields = {"key"})
    private static class TypeA {
        private final int key;
        private final String value;

        public TypeA(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private static HollowWriteStateEngine createSnapshot(int start, int end, String value) {
        Object[] objects = IntStream.range(start, end)
                .mapToObj(id -> new TypeA(id, value))
                .toArray();
        return new HollowWriteStateEngineBuilder(Collections.singleton(TypeA.class)).add(objects).build();
    }

    private static TestHollowConsumer createHollowConsumer(boolean longevity) {
        return new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withObjectLongevityConfig(
                        new HollowConsumer.ObjectLongevityConfig() {
                            public long usageDetectionPeriodMillis() {
                                return 1_000L;
                            }

                            public long gracePeriodMillis() {
                                return HOURS.toMillis(2);
                            }

                            public boolean forceDropData() {
                                return true;
                            }

                            public boolean enableLongLivedObjectSupport() {
                                return longevity;
                            }

                            public boolean enableExpiredUsageStackTraces() {
                                return false;
                            }

                            public boolean dropDataAutomatically() {
                                return true;
                            }
                        })
                .build();

    }
}
