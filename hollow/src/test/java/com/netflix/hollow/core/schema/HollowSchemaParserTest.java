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
package com.netflix.hollow.core.schema;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class HollowSchemaParserTest {
    @Test
    public void parsesObjectSchema() throws IOException {
        String objectSchema =
                "/* This is a comment\n" +
                        "   consisting of multiple lines */\n" +
                        " TypeA {\n" +
                        "    int a1;\n" +
                        "    \tstring a2; //This is a comment\n" +
                        "    String a3;\n" +
                        "}\n";

        HollowObjectSchema schema = (HollowObjectSchema) HollowSchemaParser.parseSchema(objectSchema);

        Assert.assertEquals("TypeA", schema.getName());
        Assert.assertEquals(3, schema.numFields());
        Assert.assertEquals(FieldType.INT, schema.getFieldType(0));
        Assert.assertEquals("a1", schema.getFieldName(0));
        Assert.assertEquals(FieldType.STRING, schema.getFieldType(1));
        Assert.assertEquals("a2", schema.getFieldName(1));
        Assert.assertEquals(FieldType.REFERENCE, schema.getFieldType(2));
        Assert.assertEquals("String", schema.getReferencedType(2));
        Assert.assertEquals("a3", schema.getFieldName(2));

        // HollowObjectSchame.toString is parsed properly
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesObjectSchemaWithKey() throws IOException {
        String objectSchema = " TypeA @PrimaryKey(a1) {\n" +
                "    int a1;\n" +
                "    string a2;\n" +
                "    String a3;\n" +
                "}\n";

        HollowObjectSchema schema = (HollowObjectSchema) HollowSchemaParser.parseSchema(objectSchema);

        Assert.assertEquals("TypeA", schema.getName());
        Assert.assertEquals(3, schema.numFields());
        Assert.assertEquals(FieldType.INT, schema.getFieldType(0));
        Assert.assertEquals("a1", schema.getFieldName(0));
        Assert.assertEquals(FieldType.STRING, schema.getFieldType(1));
        Assert.assertEquals("a2", schema.getFieldName(1));
        Assert.assertEquals(FieldType.REFERENCE, schema.getFieldType(2));
        Assert.assertEquals("String", schema.getReferencedType(2));
        Assert.assertEquals("a3", schema.getFieldName(2));

        // Make sure primary key and HollowObjectSchame.toString is parsed properly
        Assert.assertEquals(new PrimaryKey("TypeA", "a1"), schema.getPrimaryKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesObjectSchemaMultipleWithKey() throws IOException {
        String objectSchema = " TypeA @PrimaryKey(a1, a3.value) {\n" +
                "    int a1;\n" +
                "    string a2;\n" +
                "    String a3;\n" +
                "}\n";

        HollowObjectSchema schema = (HollowObjectSchema) HollowSchemaParser.parseSchema(objectSchema);

        Assert.assertEquals("TypeA", schema.getName());
        Assert.assertEquals(3, schema.numFields());
        Assert.assertEquals(FieldType.INT, schema.getFieldType(0));
        Assert.assertEquals("a1", schema.getFieldName(0));
        Assert.assertEquals(FieldType.STRING, schema.getFieldType(1));
        Assert.assertEquals("a2", schema.getFieldName(1));
        Assert.assertEquals(FieldType.REFERENCE, schema.getFieldType(2));
        Assert.assertEquals("String", schema.getReferencedType(2));
        Assert.assertEquals("a3", schema.getFieldName(2));

        // Make sure primary key and HollowObjectSchame.toString is parsed properly
        Assert.assertEquals(new PrimaryKey("TypeA", "a1", "a3.value"), schema.getPrimaryKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesListSchema() throws IOException {
        String listSchema = "ListOfTypeA List<TypeA>;\n";

        HollowListSchema schema = (HollowListSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("ListOfTypeA", schema.getName());
        Assert.assertEquals("TypeA", schema.getElementType());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }


    @Test
    public void parsesSetSchema() throws IOException {
        String listSchema = "SetOfTypeA Set<TypeA>;\n";

        HollowSetSchema schema = (HollowSetSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("SetOfTypeA", schema.getName());
        Assert.assertEquals("TypeA", schema.getElementType());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesSetSchemaWithKey() throws IOException {
        String listSchema = "SetOfTypeA Set<TypeA> @HashKey(id.value);\n";

        HollowSetSchema schema = (HollowSetSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("SetOfTypeA", schema.getName());
        Assert.assertEquals("TypeA", schema.getElementType());
        Assert.assertEquals(new PrimaryKey("TypeA", "id.value"), schema.getHashKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesSetSchemaWithMultiFieldKey() throws IOException {
        String listSchema = "SetOfTypeA Set<TypeA> @HashKey(id.value, region.country.id, key);\n";

        HollowSetSchema schema = (HollowSetSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("SetOfTypeA", schema.getName());
        Assert.assertEquals("TypeA", schema.getElementType());
        Assert.assertEquals(new PrimaryKey("TypeA", "id.value", "region.country.id", "key"), schema.getHashKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }


    @Test
    public void parsesMapSchema() throws IOException {
        String listSchema = "MapOfStringToTypeA Map<String, TypeA>;\n";

        HollowMapSchema schema = (HollowMapSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("MapOfStringToTypeA", schema.getName());
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("TypeA", schema.getValueType());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }


    @Test
    public void parsesMapSchemaWithPrimaryKey() throws IOException {
        String listSchema = "MapOfStringToTypeA Map<String, TypeA> @HashKey(value);\n";

        HollowMapSchema schema = (HollowMapSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("MapOfStringToTypeA", schema.getName());
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("TypeA", schema.getValueType());
        Assert.assertEquals(new PrimaryKey("String", "value"), schema.getHashKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }


    @Test
    public void parsesMapSchemaWithMultiFieldPrimaryKey() throws IOException {
        String listSchema = "MapOfStringToTypeA Map<String, TypeA> @HashKey(id.value, region.country.id, key);\n";

        HollowMapSchema schema = (HollowMapSchema) HollowSchemaParser.parseSchema(listSchema);

        Assert.assertEquals("MapOfStringToTypeA", schema.getName());
        Assert.assertEquals("String", schema.getKeyType());
        Assert.assertEquals("TypeA", schema.getValueType());
        Assert.assertEquals(new PrimaryKey("String", "id.value", "region.country.id", "key"), schema.getHashKey());
        Assert.assertEquals(schema, HollowSchemaParser.parseSchema(schema.toString()));
    }

    @Test
    public void parsesManySchemas() throws IOException {
        String manySchemas =
                "/* This is a comment\n" +
                        "   consisting of multiple lines */\n" +
                        " TypeA {\n" +
                        "    int a1;\n" +
                        "    \tstring a2; //This is a comment\n" +
                        "    String a3;\n" +
                        "}\n\n"+
                        "MapOfStringToTypeA Map<String, TypeA>;\n"+
                        "ListOfTypeA List<TypeA>;\n"+
                        "TypeB { float b1; double b2; boolean b3; }";


        List<HollowSchema> schemas = HollowSchemaParser.parseCollectionOfSchemas(manySchemas);

        Assert.assertEquals(4, schemas.size());
    }

    @Test
    public void testParseCollectionOfSchemas_reader() throws Exception {
        InputStream input = null;
        try {
            input = getClass().getResourceAsStream("/schema1.txt");
            List<HollowSchema> schemas =
                    HollowSchemaParser.parseCollectionOfSchemas(new BufferedReader(new InputStreamReader(input)));
            Assert.assertEquals("Should have two schemas", 2, schemas.size());
            Assert.assertEquals("Should have Minion schema", "Minion", schemas.get(0).getName());
            Assert.assertEquals("Should have String schema", "String", schemas.get(1).getName());
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}
