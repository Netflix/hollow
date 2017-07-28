/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.codegen.indexes;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowImplClassname;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowJavaFileGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 * 
 * @see HollowAPIGenerator
 * 
 */
public class HollowPrimaryKeyIndexGenerator implements HollowJavaFileGenerator {

    private final String packageName;
    private final String classname;
    private final String apiClassname;
    private final String classPostfix;
    private final boolean useAggressiveSubstitutions;
    private final HollowObjectSchema schema;
    
    public HollowPrimaryKeyIndexGenerator(String packageName, String apiClassname, String classPostfix, boolean useAggressiveSubstitutions, HollowObjectSchema schema) {
        this.classname = schema.getName() + "PrimaryKeyIndex";
        this.apiClassname = apiClassname;
        this.packageName = packageName;
        this.classPostfix = classPostfix;
        this.useAggressiveSubstitutions = useAggressiveSubstitutions;
        this.schema = schema;
    }
    
    @Override
    public String getClassName() {
        return classname;
    }

    @Override
    public String generate() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("package " + packageName + ";\n\n");
        
        
        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + HollowAPI.class.getName() + ";\n");
        builder.append("import " + HollowObjectSchema.class.getName() + ";\n");
        builder.append("import " + HollowPrimaryKeyIndex.class.getName() + ";\n");
        builder.append("import " + HollowReadStateEngine.class.getName() + ";\n\n");
        
        builder.append("public class " + classname + " implements HollowConsumer.RefreshListener {\n\n");
        
        builder.append("    private HollowPrimaryKeyIndex idx;\n");
        builder.append("    private " + apiClassname + " api;\n\n");
        
        builder.append("    public " + classname + "(HollowConsumer consumer) {\n");
        builder.append("        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema(\"" + schema.getName() + "\")).getPrimaryKey().getFieldPaths());\n");
        builder.append("    }\n\n");
        
        builder.append("    public " + classname + "(HollowConsumer consumer, String... fieldPaths) {\n");
        builder.append("        consumer.getRefreshLock().lock();\n");
        builder.append("        try {\n");
        builder.append("            this.api = (" + apiClassname + ")consumer.getAPI();\n");
        builder.append("            this.idx = new HollowPrimaryKeyIndex(consumer.getStateEngine(), \"" + schema.getName() + "\", fieldPaths);\n");
        builder.append("            idx.listenForDeltaUpdates();\n");
        builder.append("            consumer.addRefreshListener(this);\n");
        builder.append("        } catch(ClassCastException cce) {\n");
        builder.append("            throw new ClassCastException(\"The HollowConsumer provided was not created with the " + apiClassname + " generated API class.\");\n");
        builder.append("        } finally {\n");
        builder.append("            consumer.getRefreshLock().unlock();\n");
        builder.append("        }\n");
        builder.append("    }\n\n");

        builder.append("    public " + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + " findMatch(Object... keys) {\n");
        builder.append("        int ordinal = idx.getMatchingOrdinal(keys);\n");
        builder.append("        if(ordinal == -1)\n");
        builder.append("            return null;\n");
        builder.append("        return api.get" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "(ordinal);\n");
        builder.append("    }\n\n");
        
        builder.append("    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {\n");
        builder.append("        idx.detachFromDeltaUpdates();\n");
        builder.append("        idx = new HollowPrimaryKeyIndex(stateEngine, idx.getPrimaryKey());\n");
        builder.append("        idx.listenForDeltaUpdates();\n");
        builder.append("        this.api = (" + apiClassname + ")api;\n");
        builder.append("    }\n\n");
        
        builder.append("    @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {\n");
        builder.append("        this.api = (" + apiClassname + ")api;\n");
        builder.append("    }\n\n");

        
        builder.append("    @Override public void refreshStarted(long currentVersion, long requestedVersion) { }\n");
        builder.append("    @Override public void blobLoaded(HollowConsumer.Blob transition) { }\n");
        builder.append("    @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) { }\n");
        builder.append("    @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) { }\n");

        builder.append("}");
        
        return builder.toString();
    }

    
}
