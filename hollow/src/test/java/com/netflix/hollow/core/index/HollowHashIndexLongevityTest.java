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

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.IntStream;
import org.assertj.core.api.Condition;
import org.junit.Test;

@SuppressWarnings("unused")
public class HollowHashIndexLongevityTest {

    @Test
    public void testIndexSurvives3Updates() throws IOException {
        TestHollowConsumer longConsumer = createHollowConsumer(true);
        TestHollowConsumer shortConsumer = createHollowConsumer(false);
        HollowWriteStateEngine snapshotEngine = createSnapshot(0, 5, "snapshot");
        longConsumer.applySnapshot(0, snapshotEngine);
        shortConsumer.applySnapshot(0, snapshotEngine);

        //If we were using listeners, we would have access to the ReadStateEngine + API. So we'll just use API to
        //simulate that.

        //Auto discover the keys
        HollowHashIndex longSnapshot0 = new HollowHashIndex(longConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        HollowHashIndex shortSnapshot0 = new HollowHashIndex(shortConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        int longOrd0 = getOnlyOrdinal(longSnapshot0.findMatches(0));
        int longOrd1 = getOnlyOrdinal(longSnapshot0.findMatches(1));
        int longOrd2 = getOnlyOrdinal(longSnapshot0.findMatches(2));
        int shortOrd0 = getOnlyOrdinal(longSnapshot0.findMatches(0));
        int shortOrd1 = getOnlyOrdinal(longSnapshot0.findMatches(1));
        int shortOrd2 = getOnlyOrdinal(longSnapshot0.findMatches(2));
        HollowAPI longSnapshotApi = longConsumer.getAPI();
        //All of these return non-null results. That verifies the index worked as of this snapshot.
        assertThat(longSnapshot0.findMatches(0)).is(exactlyOrdinal(longOrd0));
        assertThat(longSnapshot0.findMatches(1)).is(exactlyOrdinal(longOrd1));
        assertThat(longSnapshot0.findMatches(2)).is(exactlyOrdinal(longOrd2));
        assertThat(shortSnapshot0.findMatches(0)).is(exactlyOrdinal(shortOrd0));
        assertThat(shortSnapshot0.findMatches(1)).is(exactlyOrdinal(shortOrd1));
        assertThat(shortSnapshot0.findMatches(2)).is(exactlyOrdinal(shortOrd2));


        //Now we do a delta. Both indexes should work
        HollowWriteStateEngine delta1Engine = createSnapshot(0, 5, "delta1");
        longConsumer.applyDelta(1, delta1Engine);
        shortConsumer.applyDelta(1, delta1Engine);
        HollowHashIndex longDelta1 = new HollowHashIndex(longConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        HollowHashIndex shortDelta1 = new HollowHashIndex(shortConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        assertThat(longConsumer.getAPI()).isNotSameAs(longSnapshotApi);
        //The ordinals should all change because every record was updated.
        assertThat(longDelta1.findMatches(0)).is(notOrdinal(longOrd0));
        assertThat(longDelta1.findMatches(1)).is(notOrdinal(longOrd1));
        assertThat(longDelta1.findMatches(2)).is(notOrdinal(longOrd2));
        assertThatOrdinalsAreNotEqual(shortDelta1.findMatches(0), shortSnapshot0.findMatches(0));
        assertThatOrdinalsAreNotEqual(shortDelta1.findMatches(1), shortSnapshot0.findMatches(1));
        assertThatOrdinalsAreNotEqual(shortDelta1.findMatches(2), shortSnapshot0.findMatches(2));
        //All of these should continue to work.
        assertThat(longSnapshot0.findMatches(0)).is(exactlyOrdinal(longOrd0));
        assertThat(longSnapshot0.findMatches(1)).is(exactlyOrdinal(longOrd1));
        assertThat(longSnapshot0.findMatches(2)).is(exactlyOrdinal(longOrd2));
        assertThat(shortSnapshot0.findMatches(0)).is(exactlyOrdinal(shortOrd0));
        assertThat(shortSnapshot0.findMatches(1)).is(exactlyOrdinal(shortOrd1));
        assertThat(shortSnapshot0.findMatches(2)).is(exactlyOrdinal(shortOrd2));


        //Do another delta. The long index should work. The short index should not.
        HollowWriteStateEngine delta2Engine = createSnapshot(4, 10, "delta1");
        longConsumer.applyDelta(2, delta2Engine);
        shortConsumer.applyDelta(2, delta2Engine);
        HollowHashIndex longDelta2 = new HollowHashIndex(longConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        HollowHashIndex shortDelta2 = new HollowHashIndex(shortConsumer.getAPI().getDataAccess(), "TypeA", "", "key");
        assertThat(longConsumer.getAPI()).isNotSameAs(longSnapshotApi);
        assertThat(longDelta2.findMatches(0)).isNull();
        assertThat(longDelta2.findMatches(1)).isNull();
        assertThat(longDelta2.findMatches(2)).isNull();
        assertThat(longDelta2.findMatches(5)).is(exactlyOneOrdinal());
        assertThat(shortDelta2.findMatches(0)).isNull();
        assertThat(shortDelta2.findMatches(1)).isNull();
        assertThat(shortDelta2.findMatches(2)).isNull();
        assertThat(shortDelta2.findMatches(5)).is(exactlyOneOrdinal());
        //Long should keep working, short should not.
        assertThat(longSnapshot0.findMatches(0)).is(exactlyOrdinal(longOrd0)).is(exactlyOneOrdinal());
        assertThat(longSnapshot0.findMatches(1)).is(exactlyOrdinal(longOrd1)).is(exactlyOneOrdinal());
        assertThat(longSnapshot0.findMatches(2)).is(exactlyOrdinal(longOrd2)).is(exactlyOneOrdinal());
        //We changed the id range to exclude 0-2 to ensure we don't end up with a "new" object squatting on an old ordinal
        //and the index accidentally matching.
        assertThat(shortSnapshot0.findMatches(0)).isNull();
        assertThat(shortSnapshot0.findMatches(1)).isNull();
        assertThat(shortSnapshot0.findMatches(2)).isNull();
    }

    private static void assertThatOrdinalsAreNotEqual(HollowHashIndexResult actual, HollowHashIndexResult expected) {
        Set<Integer> expectedOrdinals = toOrdinals(expected);
        assertThat(expectedOrdinals).isNotEmpty();

        assertThat(toOrdinals(actual))
                .isNotEmpty()
                .isNotEqualTo(expectedOrdinals);
    }

    private static Set<Integer> toOrdinals(HollowHashIndexResult result) {
        if(result != null) {
            return result.stream().boxed().collect(toSet());
        } else {
            return Collections.emptySet();
        }
    }


    private static Condition<HollowHashIndexResult> notOrdinal(int expectedOrdinal) {
        return new Condition<HollowHashIndexResult>("HollowHashIndexResult must have 1 result and it must NOT be " + expectedOrdinal) {
            @Override
            public boolean matches(HollowHashIndexResult matches) {
                if(matches.numResults()!=1) {
                    describedAs("HollowHashIndexResult has " + matches.numResults() + " results. Expected only 1.");
                    return false;
                }
                int actualOrdinal = matches.iterator().next();
                if(actualOrdinal == expectedOrdinal) {
                    describedAs("HollowHashIndexResult returned ordinal " + actualOrdinal + ". Returned ordinal must be any other value.");
                    return false;
                }
                return true;
            }
        };
    }

    private static Condition<HollowHashIndexResult> exactlyOrdinal(int expectedOrdinal) {
        return new Condition<HollowHashIndexResult>("HollowHashIndexResult must have 1 result and it must be " + expectedOrdinal) {
            @Override
            public boolean matches(HollowHashIndexResult matches) {
                if(matches.numResults()!=1) {
                    describedAs("HollowHashIndexResult has " + matches.numResults() + " results. Expected only 1.");
                    return false;
                }
                int actualOrdinal = matches.iterator().next();
                if(actualOrdinal != expectedOrdinal) {
                    describedAs("HollowHashIndexResult returned ordinal " + actualOrdinal + ". Expected ordinal " + expectedOrdinal + ".");
                    return false;
                }
                return true;
            }
        };
    }

    private static Condition<HollowHashIndexResult> exactlyOneOrdinal() {
        return new Condition<HollowHashIndexResult>("HollowHashIndexResult must have at least one result.") {
            @Override
            public boolean matches(HollowHashIndexResult matches) {
                if(matches.numResults()==0) {
                    describedAs("HollowHashIndexResult has " + matches.numResults() + " results. Expected at least one.");
                    return false;
                }
                return true;
            }
        };
    }

    private static int getOnlyOrdinal(HollowHashIndexResult matches) {
        assertThat(matches.numResults()).isEqualTo(1);
        return matches.iterator().next();
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
