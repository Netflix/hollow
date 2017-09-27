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
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 * 
 * @see HollowAPIGenerator
 * 
 */
public class HollowHashIndexGenerator extends HollowIndexGenerator {

    private final HollowDataset dataset;
    
    public HollowHashIndexGenerator(String packageName, String apiClassname, String classPostfix, boolean useAggressiveSubstitutions, HollowDataset dataset, boolean usePackageGrouping) {
        super(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, usePackageGrouping);
        this.className = apiClassname + "HashIndex";
        this.dataset = dataset;
    }

    @Override
    public String generate() {
        List<HollowSchema> schemaList = HollowSchemaSorter.dependencyOrderedSchemaList(dataset);
        
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);
        
        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + HollowAPI.class.getName() + ";\n");
        builder.append("import " + HollowHashIndex.class.getName() + ";\n");
        builder.append("import " + HollowHashIndexResult.class.getName() + ";\n");
        builder.append("import " + HollowReadStateEngine.class.getName() + ";\n");
        builder.append("import " + HollowOrdinalIterator.class.getName() + ";\n");
        builder.append("import " + Collections.class.getName() + ";\n");
        builder.append("import " + Iterable.class.getName() + ";\n");
        builder.append("import " + Iterator.class.getName() + ";\n\n");

        builder.append("public class " + className + " implements HollowConsumer.RefreshListener {\n\n");

        builder.append("    private HollowHashIndex idx;\n");
        builder.append("    private " + apiClassname + " api;\n");
        builder.append("    private final String queryType;");
        builder.append("    private final String selectFieldPath;\n");
        builder.append("    private final String matchFieldPaths[];\n\n");
        
        builder.append("    public " + className + "(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {\n");
        builder.append("        this.queryType = queryType;");
        builder.append("        this.selectFieldPath = selectFieldPath;\n");
        builder.append("        this.matchFieldPaths = matchFieldPaths;\n");
        builder.append("        consumer.getRefreshLock().lock();\n");
        builder.append("        try {\n");
        builder.append("            this.api = (" + apiClassname + ")consumer.getAPI();\n");
        builder.append("            this.idx = new HollowHashIndex(consumer.getStateEngine(), queryType, selectFieldPath, matchFieldPaths);\n");
        builder.append("            consumer.addRefreshListener(this);\n");
        builder.append("        } catch(ClassCastException cce) {\n");
        builder.append("            throw new ClassCastException(\"The HollowConsumer provided was not created with the " + apiClassname + " generated API class.\");\n");
        builder.append("        } finally {\n");
        builder.append("            consumer.getRefreshLock().unlock();\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        
        for(HollowSchema schema : schemaList) {
            builder.append("    public Iterable<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "> find" + substituteInvalidChars(schema.getName()) + "Matches(Object... keys) {\n");
            builder.append("        HollowHashIndexResult matches = idx.findMatches(keys);\n");
            builder.append("        if(matches == null)\n");
            builder.append("            return Collections.emptySet();\n\n");
            builder.append("        final HollowOrdinalIterator iter = matches.iterator();\n\n");
            builder.append("        return new Iterable<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + ">() {\n");
            builder.append("            public Iterator<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "> iterator() {\n");
            builder.append("                return new Iterator<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + ">() {\n\n");
            builder.append("                    private int next = iter.next();\n\n");
            builder.append("                    public boolean hasNext() {\n");
            builder.append("                        return next != HollowOrdinalIterator.NO_MORE_ORDINALS;\n");
            builder.append("                    }\n\n");
            builder.append("                    public " + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + " next() {\n");
            builder.append("                        " + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + " obj = api.get" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "(next);\n");
            builder.append("                        next = iter.next();\n");
            builder.append("                        return obj;\n");
            builder.append("                    }\n\n");
            builder.append("                    public void remove() {\n");
            builder.append("                        throw new UnsupportedOperationException();\n");
            builder.append("                    }\n");
            builder.append("                };\n");
            builder.append("            }\n");
            builder.append("        };\n");
            builder.append("    }\n\n");
        }
        
        builder.append("    @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {\n"); 
        builder.append("        reindex(stateEngine, api);\n");
        builder.append("    }\n\n");
        
        builder.append("    @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {\n");
        builder.append("        reindex(stateEngine, api);\n");
        builder.append("    }\n\n");

        builder.append("    private void reindex(HollowReadStateEngine stateEngine, HollowAPI api) {\n");
        builder.append("        this.idx = new HollowHashIndex(stateEngine, queryType, selectFieldPath, matchFieldPaths);\n");
        builder.append("        this.api = (" + apiClassname + ") api;\n");
        builder.append("    }\n\n");

        builder.append("    @Override public void refreshStarted(long currentVersion, long requestedVersion) { }\n");
        builder.append("    @Override public void blobLoaded(HollowConsumer.Blob transition) { }\n");
        builder.append("    @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) { }\n");
        builder.append("    @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) { }\n\n");

        builder.append("}");
        
        return builder.toString();
    }

}
