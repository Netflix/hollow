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
package com.netflix.hollow.tools.history;

import static com.netflix.hollow.core.read.filter.TypeFilter.newTypeFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Regression test for the NPE observed when the Hollow History Explorer builds a historical state for a
 * namespace whose set type declares a hash key rooted at an element type that has no data.
 *
 * <p>A zero-record nested type is omitted from the blob header by RawHollow, so the consumer ends up with
 * the hash-keyed {@code SetOfElement} type but no {@code Element} type state. Building the historical state
 * runs {@link HollowHistoricalSetDataAccess#buildKeyMatcher()}, which resolves the hash key's field path
 * against the (absent) element type. Before the fix this NPE'd inside
 * {@code HollowHistoricalStateDataAccess.getTypeDataAccess} because the next-state chain is not yet linked
 * during construction and the type is absent from every historical map.
 */
public class HollowHistoryMissingHashKeyTypeTest {

    /**
     * Reproduces the reported stack: a set with a hash key whose element type is absent from the read state.
     * {@code createBasedOnNewDelta} constructs the historical state (and therefore runs buildKeyMatcher) in
     * the same way as both {@code HollowHistory.deltaOccurred} and {@code reverseDeltaOccurred}.
     */
    @Test
    public void buildsHistoricalStateWhenSetHashKeyElementTypeIsAbsent() throws IOException {
        HollowReadStateEngine readEngine = readWithElementTypeAbsent();

        // Precondition: the hash-keyed set type is present, but its element type is not.
        assertThat(readEngine.getSchema("SetOfElement")).isNotNull();
        assertThat(readEngine.getSchema("Element")).isNull();

        assertThatCode(() -> new HollowHistoricalStateCreator().createBasedOnNewDelta(0, readEngine))
                .doesNotThrowAnyException();
    }

    /**
     * The historical state, once built, is usable: getSchema returns null for the absent type rather than
     * throwing (matching the {@link com.netflix.hollow.core.HollowDataset#getSchema} contract that
     * {@code getNonNullSchema} already relies on), while present types resolve normally.
     */
    @Test
    public void historicalStateSchemaLookupToleratesAbsentType() throws IOException {
        HollowReadStateEngine readEngine = readWithElementTypeAbsent();

        HollowHistoricalStateDataAccess history =
                new HollowHistoricalStateCreator().createBasedOnNewDelta(0, readEngine);

        assertThat(history.getSchema("Element")).isNull();
        assertThat(history.getSchema("SetOfElement")).isNotNull();
        assertThat(history.getSchema("TopLevel")).isNotNull();
    }

    /** Produces a read state that has SetOfElement (with its hash key) but no Element type state. */
    private static HollowReadStateEngine readWithElementTypeAbsent() throws IOException {
        // Producer: TopLevel -> Set<Element> keyed on Element.id, with Element records present.
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.add(new TopLevel(1, new Element(11), new Element(22)));

        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(snapshot);

        // Drop the Element type from the header (the shape RawHollow emits for a zero-record nested type),
        // keeping SetOfElement, whose schema still carries the hash key rooted at the now-absent Element.
        HollowFilterConfig excludeElement = new HollowFilterConfig(true); // exclude filter
        excludeElement.addType("Element");
        ByteArrayOutputStream filtered = new ByteArrayOutputStream();
        new FilteredHollowBlobWriter(excludeElement)
                .filterSnapshot(new ByteArrayInputStream(snapshot.toByteArray()), filtered);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        new HollowBlobReader(readEngine).readSnapshot(
                HollowBlobInput.serial(filtered.toByteArray()),
                newTypeFilter().excludeAll().includeRecursive("TopLevel").build());
        return readEngine;
    }

    @SuppressWarnings("unused")
    private static class TopLevel {
        int id;

        @HollowTypeName(name = "SetOfElement")
        @HollowHashKey(fields = "id")
        Set<Element> elements;

        TopLevel(int id, Element... elements) {
            this.id = id;
            this.elements = new HashSet<Element>();
            for (Element e : elements) {
                this.elements.add(e);
            }
        }
    }

    @SuppressWarnings("unused")
    private static class Element {
        int id;

        Element(int id) {
            this.id = id;
        }
    }
}
