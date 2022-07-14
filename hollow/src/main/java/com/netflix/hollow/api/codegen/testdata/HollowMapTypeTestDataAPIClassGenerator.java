/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.codegen.testdata;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;

class HollowMapTypeTestDataAPIClassGenerator {

    private final HollowDataset dataset;
    private final HollowMapSchema schema;
    private final String packageName;
    private final String className;
    private final String keyClassName;
    private final String valueClassName;

    public HollowMapTypeTestDataAPIClassGenerator(HollowDataset dataset, HollowMapSchema schema, String packageName) {
        this.dataset = dataset;
        this.schema = schema;
        this.packageName = packageName;
        this.className = schema.getName() + "TestData";
        this.keyClassName = schema.getKeyType() + "TestData";
        this.valueClassName = schema.getValueType() + "TestData";
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();

        builder.append("package " + packageName + ";\n\n");

        builder.append("import com.netflix.hollow.api.testdata.HollowTestMapRecord;\n" +
                "import com.netflix.hollow.core.schema.HollowMapSchema;\n\n");

        builder.append("public class " + className + "<T> extends HollowTestMapRecord<T> {\n\n");

        builder.append("    " + className + "(T parent) {\n");
        builder.append("        super(parent);\n");
        builder.append("    }\n\n");

        builder.append("    public Entry entry() {\n");
        builder.append("        Entry e = new Entry();\n");
        builder.append("        addEntry(e);\n");
        builder.append("        return e;\n");
        builder.append("    }\n\n");

        if(canErgonomicShortcut(schema.getKeyType()) && canErgonomicShortcut(schema.getValueType())) {
            String keyType = getErgonomicShortcutType(schema.getKeyType());
            String valueType = getErgonomicShortcutType(schema.getValueType());

            builder.append("    public " + className + "<T> entry(" + keyType + " key, " + valueType + " value) {\n");
            builder.append("        entry().key(key).value(value);\n");
            builder.append("        return this;\n");
            builder.append("    }\n\n");

        } else if(canErgonomicShortcut(schema.getKeyType())) {
            // TODO
        }

        builder.append("    private static final HollowMapSchema SCHEMA = new HollowMapSchema(\"" + schema.getName() + "\", \"" + schema.getKeyType() + "\", \"" + schema.getValueType() + "\"");
        if(schema.getHashKey() != null) {
            for(String fieldPath : schema.getHashKey().getFieldPaths()) {
                builder.append(", \"" + fieldPath + "\"");
            }
        }
        builder.append(");\n\n");

        builder.append("    @Override public HollowMapSchema getSchema() { return SCHEMA; }\n\n");


        builder.append("    public class Entry extends HollowTestMapRecord.Entry<" + className + "<T>> {\n\n");

        builder.append("        public Entry() {\n");
        builder.append("            super(" + className + ".this);\n");
        builder.append("        }\n\n");

        builder.append("        public " + keyClassName + "<Entry> key() {\n");
        builder.append("            " + keyClassName + "<Entry> __k = new " + keyClassName + "<>(this);\n");
        builder.append("            setKey(__k);\n");
        builder.append("            return __k;\n");
        builder.append("        }\n\n");

        builder.append("        public " + valueClassName + "<Entry> value() {\n");
        builder.append("            " + valueClassName + "<Entry> __v = new " + valueClassName + "<>(this);\n");
        builder.append("            setValue(__v);\n");
        builder.append("            return __v;\n");
        builder.append("        }\n\n");

        if(canErgonomicShortcut(schema.getKeyType())) {
            String keyType = getErgonomicShortcutType(schema.getKeyType());
            String keyFieldName = getErgonomicFieldName(schema.getKeyType());

            builder.append("        public Entry key(" + keyType + " key) {\n");
            builder.append("            key()." + keyFieldName + "(key);\n");
            builder.append("            return this;\n");
            builder.append("        }\n\n");
        }

        if(canErgonomicShortcut(schema.getValueType())) {
            String valueType = getErgonomicShortcutType(schema.getValueType());
            String valueFieldName = getErgonomicFieldName(schema.getValueType());

            builder.append("        public Entry value(" + valueType + " value) {\n");
            builder.append("            value()." + valueFieldName + "(value);\n");
            builder.append("            return this;\n");
            builder.append("        }\n\n");
        }


        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }

    public String className(String type) {
        return type + "TestData";
    }

    private boolean canErgonomicShortcut(String schemaName) {
        return canErgonomicShortcut(dataset.getSchema(schemaName));
    }

    private boolean canErgonomicShortcut(HollowSchema schema) {
        if(schema.getSchemaType() != SchemaType.OBJECT)
            return false;

        HollowObjectSchema objSchema = (HollowObjectSchema) schema;

        if(objSchema.numFields() != 1)
            return false;

        return objSchema.getFieldType(0) != FieldType.REFERENCE;
    }

    private String getErgonomicShortcutType(String schemaName) {
        HollowObjectSchema schema = (HollowObjectSchema) dataset.getSchema(schemaName);

        switch(schema.getFieldType(0)) {
            case INT:
                return "Integer";
            case LONG:
                return "Long";
            case FLOAT:
                return "Float";
            case DOUBLE:
                return "Double";
            case BOOLEAN:
                return "Boolean";
            case BYTES:
                return "byte[]";
            case STRING:
                return "String";
            default:
                throw new IllegalArgumentException();
        }
    }

    private String getErgonomicFieldName(String schemaName) {
        HollowObjectSchema schema = (HollowObjectSchema) dataset.getSchema(schemaName);
        return schema.getFieldName(0);
    }

}
