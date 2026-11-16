/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.consumer.longevity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowCodeGenerationCompileUtil;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Indexes and generated {@code getAll…} built over a hold see the held version: a hold's types answer their populated
 * ordinals and max ordinal for it, and never expose the state engine's type states.
 */
public class HeldIndexesTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();
    private final List<Long> versions = fixture.cycles(5);
    private final HollowConsumer consumer = fixture.heldConsumer();
    private final List<HollowVersionHold> holds = holdBehindAndAtTheEngine();

    @Test
    public void aHoldsTypesAnswerForItsVersion() {
        assertTypesAnswerForTheirVersion(); // one hold at the engine's version
        consumer.triggerRefreshTo(versions.get(4));
        assertTypesAnswerForTheirVersion();
    }

    private void assertTypesAnswerForTheirVersion() {
        for (HollowVersionHold hold : holds) {
            for (HollowTypeReadState expected : fixture.reference(hold.getVersion()).getTypeStates()) {
                String type = expected.getSchema().getName();
                HollowTypeDataAccess held = hold.getDataAccess().getTypeDataAccess(type);
                assertEquals(hold.getVersion() + " " + type, expected.getPopulatedOrdinals(), held.getPopulatedOrdinals());
                assertEquals(expected.getPopulatedOrdinals().length() - 1, held.getMaxOrdinal());
                try {
                    held.getTypeState();
                    fail(hold.getVersion() + " " + type);
                } catch (UnsupportedOperationException expectedFailure) {
                }
            }
        }
    }

    @Test
    public void aClosedHoldsTypesHaveNoPopulatedOrdinals() {
        HollowVersionHold hold = holds.get(0);
        hold.close();
        try {
            hold.getDataAccess().getTypeDataAccess("Movie").getPopulatedOrdinals();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void aUniqueKeyIndexOnAHoldFindsItsVersionsRecords() {
        HollowUniqueKeyIndex[] indexes = new HollowUniqueKeyIndex[holds.size()];
        for (int i = 0; i < holds.size(); i++)
            indexes[i] = new HollowUniqueKeyIndex(holds.get(i).getDataAccess(), "Movie", "id");
        consumer.triggerRefreshTo(versions.get(4)); // indexes over a hold never follow the engine

        Set<Long> ids = ids();
        for (int i = 0; i < holds.size(); i++) {
            long version = holds.get(i).getVersion();
            HollowUniqueKeyIndex expected = new HollowUniqueKeyIndex(fixture.reference(version), "Movie", "id");
            for (long id : ids)
                assertEquals(version + " " + id, expected.getMatchingOrdinal(id), indexes[i].getMatchingOrdinal(id));
        }
    }

    @Test
    public void aHashIndexOnAHoldFindsItsVersionsRecords() {
        HollowHashIndex[] indexes = new HollowHashIndex[holds.size()];
        for (int i = 0; i < holds.size(); i++)
            indexes[i] = new HollowHashIndex(holds.get(i).getDataAccess(), "Movie", "", "title.value");
        consumer.triggerRefreshTo(versions.get(4));

        Set<String> titles = titles();
        for (int i = 0; i < holds.size(); i++) {
            long version = holds.get(i).getVersion();
            HollowHashIndex expected = new HollowHashIndex(fixture.reference(version), "Movie", "", "title.value");
            for (String title : titles)
                assertEquals(version + " " + title, ordinals(expected.findMatches(title)),
                        ordinals(indexes[i].findMatches(title)));
        }
    }

    @Test
    public void aRegeneratedApisGetAllOnAHoldReturnsItsVersionsRecords() throws Exception {
        List<Class<? extends HollowAPI>> apiClasses = generateApis();
        consumer.triggerRefreshTo(versions.get(4));

        for (Class<? extends HollowAPI> apiClass : apiClasses) {
            for (HollowVersionHold hold : holds) {
                Collection<?> movies = (Collection<?>) apiClass.getMethod("getAllMovie").invoke(hold.getAPI(apiClass));
                BitSet ordinals = new BitSet();
                for (Object movie : movies)
                    ordinals.set(((HollowRecord) movie).getOrdinal());
                assertEquals(apiClass.getName() + " " + hold.getVersion(),
                        fixture.reference(hold.getVersion()).getTypeState("Movie").getPopulatedOrdinals(), ordinals);
            }
        }
    }

    @Test
    public void anApiGeneratedBeforeFailsOnAHoldRatherThanReadTheEngine() {
        for (HollowVersionHold hold : holds) {
            HollowTypeDataAccess movies = hold.getDataAccess().getTypeDataAccess("Movie");
            try {
                new AllHollowRecordCollection<Integer>(movies.getTypeState()) { // what its getAll… does
                    @Override
                    protected Integer getForOrdinal(int ordinal) {
                        return ordinal;
                    }
                };
                fail(String.valueOf(hold.getVersion()));
            } catch (UnsupportedOperationException expected) {
            }
        }
    }

    /** The served hold, a hold behind the engine from copies, and a hold at the engine's version. */
    private List<HollowVersionHold> holdBehindAndAtTheEngine() {
        consumer.triggerRefreshTo(versions.get(0));
        HollowVersionHold served = consumer.holdVersion(versions.get(0));
        consumer.triggerRefreshTo(versions.get(3)); // runs ahead of the served hold
        return Arrays.asList(served, consumer.holdVersion(versions.get(1)), consumer.holdVersion(versions.get(3)));
    }

    /** An API generated for the fixture's data model, and one with parameterized class names. */
    private List<Class<? extends HollowAPI>> generateApis() throws Exception {
        File source = folder.newFolder("src");
        File classes = folder.newFolder("classes");
        for (boolean parameterized : new boolean[] {false, true}) {
            new HollowAPIGenerator.Builder()
                    .withDataModel(fixture.reference(versions.get(0)))
                    .withAPIClassname("HeldAPI")
                    .withPackageName(parameterized ? "held.parameterized" : "held.generated")
                    .withParameterizeAllClassNames(parameterized)
                    .withDestination(source.getPath())
                    .build()
                    .generateSourceFiles();
        }
        HollowCodeGenerationCompileUtil.compileSrcFiles(source.getPath(), classes.getPath());
        ClassLoader loader = new URLClassLoader(new URL[] {classes.toURI().toURL()}, getClass().getClassLoader());
        return Arrays.asList(loader.loadClass("held.generated.HeldAPI").asSubclass(HollowAPI.class),
                loader.loadClass("held.parameterized.HeldAPI").asSubclass(HollowAPI.class));
    }

    /** Every movie id in any version. */
    private Set<Long> ids() {
        Set<Long> ids = new TreeSet<>();
        for (long version : versions) {
            HollowObjectTypeReadState movies = movies(version);
            int id = movies.getSchema().getPosition("id");
            BitSet populated = movies.getPopulatedOrdinals();
            for (int ordinal = populated.nextSetBit(0); ordinal >= 0; ordinal = populated.nextSetBit(ordinal + 1))
                ids.add(movies.readLong(ordinal, id));
        }
        return ids;
    }

    /** Every movie title in any version. */
    private Set<String> titles() {
        Set<String> titles = new TreeSet<>();
        for (long version : versions) {
            HollowReadStateEngine engine = fixture.reference(version);
            HollowObjectTypeReadState movies = movies(version);
            HollowObjectTypeReadState strings = (HollowObjectTypeReadState) engine.getTypeState("String");
            int title = movies.getSchema().getPosition("title");
            int value = strings.getSchema().getPosition("value");
            BitSet populated = movies.getPopulatedOrdinals();
            for (int ordinal = populated.nextSetBit(0); ordinal >= 0; ordinal = populated.nextSetBit(ordinal + 1))
                titles.add(strings.readString(movies.readOrdinal(ordinal, title), value));
        }
        return titles;
    }

    private HollowObjectTypeReadState movies(long version) {
        return (HollowObjectTypeReadState) fixture.reference(version).getTypeState("Movie");
    }

    private static Set<Integer> ordinals(HollowHashIndexResult result) {
        Set<Integer> ordinals = new TreeSet<>();
        if (result != null) {
            HollowOrdinalIterator it = result.iterator();
            for (int ordinal = it.next(); ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS; ordinal = it.next())
                ordinals.add(ordinal);
        }
        return ordinals;
    }
}
