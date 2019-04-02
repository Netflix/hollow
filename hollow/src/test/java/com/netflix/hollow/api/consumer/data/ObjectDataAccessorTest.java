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
 */
package com.netflix.hollow.api.consumer.data;

import static com.netflix.hollow.api.consumer.data.HollowObjectAssertions.assertList;
import static com.netflix.hollow.api.consumer.data.HollowObjectAssertions.assertUpdatedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.type.accessor.ObjectDataAccessor;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class ObjectDataAccessorTest {

    @Test
    public void test() throws IOException {
        HollowWriteStateEngine writeState;
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withGeneratedAPIClass(api.LettersAPI.class)
                .build();

        writeState = new HollowWriteStateEngineBuilder()
                .add(new model.Alpha(1, "one.a"))
                .add(new model.Alpha(2, "two.a"))
                .add(new model.Alpha(3, "three.a"))
                .build();
        consumer.addSnapshot(1L, writeState);
        consumer.triggerRefreshTo(1L);


        {
            ObjectDataAccessor.Builder<api.Alpha> builder = ObjectDataAccessor.from(consumer, api.Alpha.class);
            ObjectDataAccessor<api.Alpha> accessor = builder.bindToPrimaryKey();

            assertFalse(accessor.isDataChangeComputed());
            assertEquals(3, accessor.getAddedRecords().size());
            assertList(accessor.getAddedRecords(), Arrays.asList(1, 2, 3));
            assertTrue(accessor.getRemovedRecords().isEmpty());
            assertTrue(accessor.getUpdatedRecords().isEmpty());
            assertTrue(accessor.isDataChangeComputed());
        }

        writeState = new HollowWriteStateEngineBuilder(model.Alpha.class)
                .restoreFrom(consumer.getStateEngine())
                .add(new model.Alpha(1, "one.a"))             // unchanged
                // .add(new model.Alpha(2, "two.a"))          // removed
                .add(new model.Alpha(3, "three.b"))           // modified
                .add(new model.Alpha(1000, "one-thousand.a")) // added
                .add(new model.Alpha(0, "zero.a"))            // added
                .build();
        consumer.addDelta(1L, 2L, writeState);
        consumer.triggerRefreshTo(2L);

        if (!this.getClass().getSimpleName().equals("")) return;
        {
            ObjectDataAccessor.Builder<api.Alpha> builder = ObjectDataAccessor.from(consumer, api.Alpha.class);
            ObjectDataAccessor<api.Alpha> accessor = builder.bindToPrimaryKey();

            assertFalse("changes aren't precomputed", accessor.isDataChangeComputed());
            assertEquals(2, accessor.getAddedRecords().size());
            assertList(accessor.getAddedRecords(), Arrays.asList(1000, 0));
            assertEquals(1, accessor.getRemovedRecords().size());
            assertList(accessor.getRemovedRecords(), Arrays.asList(2));
            assertEquals(1, accessor.getUpdatedRecords().size());
            assertUpdatedList(accessor.getUpdatedRecords(), Arrays.asList("three"), Arrays.asList("three.b"));
            assertTrue("changes were computed on demand", accessor.isDataChangeComputed());
        }


        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) consumer.getStateEngine().getTypeState(model.Alpha.class.getSimpleName());
        assertEquals(5, typeState.maxOrdinal());

        assertObject(typeState, 0, 1, "one");
        assertObject(typeState, 1, 2, "two"); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 2, 3, "three"); /// this was "removed", but the data hangs around as a "ghost" until the following cycle.
        assertObject(typeState, 3, 3, "three_updated");
        assertObject(typeState, 4, 1000, "one thousand");
        assertObject(typeState, 5, 0, "zero");

//
//        roundTripDelta(); // remove everything
//        {
//            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
//            assertEquals(0, dAccessor.getAddedRecords().size());
//            assertEquals(4, dAccessor.getRemovedRecords().size());
//            assertList(dAccessor.getRemovedRecords(), Arrays.asList(1, 3, 1000, 0));
//            assertEquals(0, dAccessor.getUpdatedRecords().size());
//        }
//
//        assertObject(typeState, 0, 1, "one"); /// all records were "removed", but again hang around until the following cycle.
//        // assertObject(typeState, 1, 2, ""); /// this record should now be disappeared.
//        // assertObject(typeState, 2, 3, "three"); /// this record should now be disappeared.
//        assertObject(typeState, 3, 3, "three_updated"); /// "ghost"
//        assertObject(typeState, 4, 1000, "one thousand"); /// "ghost"
//        assertObject(typeState, 5, 0, "zero"); /// "ghost"
//
//        assertEquals(5, typeState.maxOrdinal());
//
//        addRecord(634, "six hundred thirty four");
//        addRecord(0, "zero");
//
//        roundTripDelta();
//        {
//            GenericHollowRecordDataAccessor dAccessor = new GenericHollowRecordDataAccessor(readStateEngine, TEST_TYPE);
//            assertEquals(2, dAccessor.getAddedRecords().size());
//            assertList(dAccessor.getAddedRecords(), Arrays.asList(634, 0));
//            assertEquals(0, dAccessor.getRemovedRecords().size());
//            assertEquals(0, dAccessor.getUpdatedRecords().size());
//        }
//
//        assertEquals(1, typeState.maxOrdinal());
//        assertObject(typeState, 0, 634, "six hundred thirty four"); /// now, since all records were removed, we can recycle the ordinal "0", even
//                                                                    /// though it was a "ghost" in the last cycle.
//        assertObject(typeState, 1, 0, "zero"); /// even though "zero" had an equivalent record in the previous cycle at ordinal "4", it is now
//                                               /// assigned to recycled ordinal "1".
    }

    private void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);

        Assert.assertEquals(intVal, obj.getInt("f1"));
        Assert.assertEquals(strVal, obj.getString("f2"));
    }

    static final class model {
        @HollowPrimaryKey(fields = "a1")
        private static final class Alpha {
            final int a1;
            @HollowInline final String a2;

            Alpha(int a1, String a2) {
                this.a1 = a1;
                this.a2 = a2;
            }
        }
    }

    public static final class api {
        public static class Alpha extends HollowObject {

            public Alpha(AlphaDelegate delegate, int ordinal) {
                super(delegate, ordinal);
            }

            public int getA1() {
                return delegate().getA1(ordinal);
            }

            String getA2() {
                return delegate().getA2(ordinal);
            }

            public boolean isA2Equal(String testValue) {
                return delegate().isA2Equal(ordinal, testValue);
            }

            public LettersAPI api() {
                return typeApi().getAPI();
            }

            public AlphaTypeAPI typeApi() {
                return delegate().getTypeAPI();
            }

            protected AlphaDelegate delegate() {
                return (AlphaDelegate)delegate;
            }

            @Override
            public String toString() {
                return new HollowRecordStringifier().stringify(this);
            }

        }

        public interface AlphaDelegate extends HollowObjectDelegate {
            int getA1(int ordinal);
            Integer getA1Boxed(int ordinal);
            String getA2(int ordinal);
            boolean isA2Equal(int ordinal, String testValue);
            AlphaTypeAPI getTypeAPI();
        }

        public static class AlphaDelegateLookupImpl extends HollowObjectAbstractDelegate implements AlphaDelegate {
            private final AlphaTypeAPI typeAPI;

            public AlphaDelegateLookupImpl(AlphaTypeAPI typeAPI) {
                this.typeAPI = typeAPI;
            }

            public int getA1(int ordinal) {
                return typeAPI.getA1(ordinal);
            }

            public Integer getA1Boxed(int ordinal) {
                return typeAPI.getA1Boxed(ordinal);
            }

            public String getA2(int ordinal) {
                return typeAPI.getA2(ordinal);
            }

            public boolean isA2Equal(int ordinal, String testValue) {
                return typeAPI.isA2Equal(ordinal, testValue);
            }

            public AlphaTypeAPI getTypeAPI() {
                return typeAPI;
            }

            @Override
            public HollowObjectSchema getSchema() {
                return typeAPI.getTypeDataAccess().getSchema();
            }

            @Override
            public HollowObjectTypeDataAccess getTypeDataAccess() {
                return typeAPI.getTypeDataAccess();
            }
        }

        public static class AlphaTypeAPI extends HollowObjectTypeAPI {
            private final AlphaDelegateLookupImpl delegateLookupImpl;

            public AlphaTypeAPI(LettersAPI api, HollowObjectTypeDataAccess typeDataAccess) {
                super(api, typeDataAccess, new String[] {
                        "a1",
                        "a2"
                });
                this.delegateLookupImpl = new AlphaDelegateLookupImpl(this);
            }

            public int getA1(int ordinal) {
                return fieldIndex[0] == -1 ? Integer.MIN_VALUE : getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
            }

            public Integer getA1Boxed(int ordinal) {
                if (fieldIndex[0] == -1)
                    return null;
                int i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
                return i == Integer.MIN_VALUE ? null : i;
            }

            public String getA2(int ordinal) {
                return fieldIndex[1] == -1 ? null : getTypeDataAccess().readString(ordinal, fieldIndex[1]);
            }

            public boolean isA2Equal(int ordinal, String testValue) {
                if(fieldIndex[1] == -1)
                    return testValue == null;
                return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[1], testValue);
            }

            public AlphaDelegateLookupImpl getDelegateLookupImpl() {
                return delegateLookupImpl;
            }

            @Override
            public LettersAPI getAPI() {
                return (LettersAPI) api;
            }

        }

        public static class LettersAPI extends HollowAPI  {
            private final AlphaTypeAPI alphaTypeAPI;

            private final HollowObjectProvider alphaProvider;

            public LettersAPI(HollowDataAccess dataAccess) {
                this(dataAccess, null);
            }

            public LettersAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
                this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());
            }

            public LettersAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {
                this(dataAccess, cachedTypes, factoryOverrides, null);
            }

            public LettersAPI(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, LettersAPI previousCycleAPI) {
                super(dataAccess);
                HollowTypeDataAccess typeDataAccess;
                HollowFactory factory;

                typeDataAccess = dataAccess.getTypeDataAccess("Alpha");
                if(typeDataAccess != null) {
                    alphaTypeAPI = new AlphaTypeAPI(this, (HollowObjectTypeDataAccess)typeDataAccess);
                } else {
                    alphaTypeAPI = new AlphaTypeAPI(this, new HollowObjectMissingDataAccess(dataAccess, "Alpha"));
                }
                addTypeAPI(alphaTypeAPI);
                factory = new AlphaHollowFactory();
                alphaProvider = new HollowObjectFactoryProvider(typeDataAccess, alphaTypeAPI, factory);
            }

            public AlphaTypeAPI getAlphaTypeAPI() {
                return alphaTypeAPI;
            }

            public Collection<Alpha> getAllAlpha() {
                return new AllHollowRecordCollection<Alpha>(getDataAccess().getTypeDataAccess("Alpha").getTypeState()) {
                    protected Alpha getForOrdinal(int ordinal) {
                        return getAlpha(ordinal);
                    }
                };
            }

            public Alpha getAlpha(int ordinal) {
                return (Alpha)alphaProvider.getHollowObject(ordinal);
            }
        }

        public static class AlphaHollowFactory<T extends Alpha> extends HollowFactory<T> {
            @Override
            public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
                return (T)new Alpha(((AlphaTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
            }
        }
    }
}
