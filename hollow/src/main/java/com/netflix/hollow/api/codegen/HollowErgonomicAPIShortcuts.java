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
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HollowErgonomicAPIShortcuts {

    public static final HollowErgonomicAPIShortcuts NO_SHORTCUTS = new HollowErgonomicAPIShortcuts();

    private final Map<String, Shortcut> shortcutFieldPaths;

    private HollowErgonomicAPIShortcuts() {
        this.shortcutFieldPaths = Collections.emptyMap();
    }

    HollowErgonomicAPIShortcuts(HollowDataset dataset) {
        this.shortcutFieldPaths = new HashMap<String, Shortcut>();
        populatePaths(dataset);
    }

    public Shortcut getShortcut(String typeField) {
        return shortcutFieldPaths.get(typeField);
    }

    int numShortcuts() {
        return shortcutFieldPaths.size();
    }

    private void populatePaths(HollowDataset dataset) {
        for(HollowSchema schema : dataset.getSchemas()) {
            if(schema.getSchemaType() == SchemaType.OBJECT) {
                HollowObjectSchema objSchema = (HollowObjectSchema) schema;

                for(int i = 0; i < objSchema.numFields(); i++) {
                    if(objSchema.getFieldType(i) == FieldType.REFERENCE) {
                        HollowSchema refSchema = dataset.getSchema(objSchema.getReferencedType(i));
                        if(refSchema != null) {
                            Shortcut shortcut = getShortcutFieldPath(dataset, refSchema);
                            if(shortcut != null) {
                                String key = objSchema.getName() + "." + objSchema.getFieldName(i);
                                shortcutFieldPaths.put(key, shortcut);
                            }
                        }
                    }
                }
            }
        }
    }

    private Shortcut getShortcutFieldPath(HollowDataset dataset, HollowSchema schema) {
        if(schema.getSchemaType() == SchemaType.OBJECT) {
            HollowObjectSchema objSchema = (HollowObjectSchema) schema;
            if(objSchema.numFields() == 1) {
                if(objSchema.getFieldType(0) == FieldType.REFERENCE) {
                    HollowSchema refSchema = dataset.getSchema(objSchema.getReferencedType(0));
                    if(refSchema != null) {
                        Shortcut childShortcut = getShortcutFieldPath(dataset, refSchema);
                        if(childShortcut != null) {
                            String[] shortcutPathTypes = new String[childShortcut.getPathTypes().length + 1];
                            String[] shortcutPath = new String[childShortcut.getPath().length + 1];
                            shortcutPathTypes[0] = objSchema.getName();
                            shortcutPath[0] = objSchema.getFieldName(0);
                            System.arraycopy(childShortcut.getPath(), 0, shortcutPath, 1, childShortcut.getPath().length);
                            System.arraycopy(childShortcut.getPathTypes(), 0, shortcutPathTypes, 1, childShortcut.getPathTypes().length);
                            return new Shortcut(shortcutPathTypes, shortcutPath, childShortcut.getType());
                        }
                    }
                } else {
                    return new Shortcut(new String[]{objSchema.getName()}, new String[]{objSchema.getFieldName(0)}, objSchema.getFieldType(0));
                }
            }
        }

        return null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, Shortcut> entry : shortcutFieldPaths.entrySet()) {
            builder.append(entry.getKey() + ": " + entry.getValue()).append("\n");
        }
        return builder.toString();
    }

    public static class Shortcut {
        public final String[] pathTypes;
        public final String[] path;
        public final FieldType type;

        public Shortcut(String[] pathTypes, String[] path, FieldType type) {
            this.pathTypes = pathTypes;
            this.path = path;
            this.type = type;
        }

        public String[] getPath() {
            return path;
        }

        public String[] getPathTypes() {
            return pathTypes;
        }

        public FieldType getType() {
            return type;
        }

        public String toString() {
            return Arrays.toString(path) + " (" + type.toString() + ")";
        }
    }
}
