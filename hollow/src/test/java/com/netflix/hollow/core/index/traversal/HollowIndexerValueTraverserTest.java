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
package com.netflix.hollow.core.index.traversal;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowIndexerValueTraverserTest extends AbstractStateEngineTest {

    private HollowObjectSchema aSchema;
    private HollowListSchema bSchema;
    private HollowMapSchema cSchema;
    private HollowObjectSchema dSchema;
    private HollowObjectSchema eSchema;
    private HollowSetSchema fSchema;
    private HollowObjectSchema gSchema;

    @Before
    public void setUp() {
        aSchema = new HollowObjectSchema("A", 3);
        aSchema.addField("a", FieldType.INT);
        aSchema.addField("b", FieldType.REFERENCE, "B");
        aSchema.addField("c", FieldType.REFERENCE, "C");

        bSchema = new HollowListSchema("B", "D");

        cSchema = new HollowMapSchema("C", "E", "F");

        dSchema = new HollowObjectSchema("D", 2);
        dSchema.addField("d1", FieldType.STRING);
        dSchema.addField("d2", FieldType.BOOLEAN);

        eSchema = new HollowObjectSchema("E", 1);
        eSchema.addField("e", FieldType.STRING);

        fSchema = new HollowSetSchema("F", "G");

        gSchema = new HollowObjectSchema("G", 1);
        gSchema.addField("g", FieldType.FLOAT);

        super.setUp();
    }

    @Test
    public void iteratesValues() throws IOException {
        a(1,
                b(
                        d("1", true),
                        d("2", false),
                        d("3", true)
                 ),
                c(
                        e("one"), f(g(1.1f), g(1.2f), g(1.3f)),
                        e("two"), f(g(2.1f)),
                        e("three"), f(g(3.1f))
                 )
          );

        roundTripSnapshot();

        HollowIndexerValueTraverser traverser = new HollowIndexerValueTraverser(readStateEngine, "A", "a", "b.element.d1", "b.element.d2", "c.key.e", "c.value.element.g");

        traverser.traverse(0);

        for(int i=0;i<traverser.getNumMatches();i++) {
            for(int j=0;j<traverser.getNumFieldPaths();j++) {
                System.out.print(traverser.getMatchedValue(i, j) + ", ");
            }
            System.out.println();
        }


        Assert.assertEquals(15, traverser.getNumMatches());
        assertValueTraverserContainsEntry(traverser, 1, "1", true, "three", 3.1f);
        assertValueTraverserContainsEntry(traverser, 1, "2", false, "two", 2.1f);
        assertValueTraverserContainsEntry(traverser, 1, "3", true, "two", 2.1f);
        assertValueTraverserContainsEntry(traverser, 1, "3", true, "one", 1.1f);
        assertValueTraverserContainsEntry(traverser, 1, "3", true, "one", 1.2f);
        assertValueTraverserContainsEntry(traverser, 1, "1", true, "one", 1.3f);
    }

    private void assertValueTraverserContainsEntry(HollowIndexerValueTraverser traverser, Object... values) {
        for(int i=0;i<traverser.getNumMatches();i++) {
            boolean allMatched = true;
            for(int j=0;j<traverser.getNumFieldPaths();j++) {
                if(!values[j].equals(traverser.getMatchedValue(i, j)))
                    allMatched = false;
            }
            if(allMatched) return;
        }
        Assert.fail("entry not found");
    }

    private int a(int a, int b, int c) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(aSchema);
        rec.setInt("a", a);
        rec.setReference("b", b);
        rec.setReference("c", c);
        return writeStateEngine.add("A", rec);
    }

    private int b(int... values) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(int i=0;i<values.length;i++) {
            rec.addElement(values[i]);
        }
        return writeStateEngine.add("B", rec);
    }

    private int c(int... keyValuePairs) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(int i=0;i<keyValuePairs.length;i+=2) {
            rec.addEntry(keyValuePairs[i], keyValuePairs[i+1]);
        }
        return writeStateEngine.add("C", rec);
    }

    private int d(String d1, boolean d2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(dSchema);
        rec.setString("d1", d1);
        rec.setBoolean("d2", d2);
        return writeStateEngine.add("D", rec);
    }

    private int e(String value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(eSchema);
        rec.setString("e", value);
        return writeStateEngine.add("E", rec);
    }

    private int f(int... values) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(int i=0;i<values.length;i++) {
            rec.addElement(values[i]);
        }
        return writeStateEngine.add("F", rec);
    }

    private int g(float value) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(gSchema);
        rec.setFloat("g", value);
        return writeStateEngine.add("G", rec);
    }


    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(aSchema));
        writeStateEngine.addTypeState(new HollowListTypeWriteState(bSchema));
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(cSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(dSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(eSchema));
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(fSchema));
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(gSchema));
    }

}
