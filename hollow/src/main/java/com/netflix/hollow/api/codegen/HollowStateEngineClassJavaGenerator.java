/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateInterfaceName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateLookupClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.delegateLookupImplName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowFactoryClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowImplClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.lowercase;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;

import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Not intended for external consumption.
 * 
 * @see HollowAPIGenerator
 * 
 */
public class HollowStateEngineClassJavaGenerator implements HollowJavaFileGenerator {

    private final String packageName;
    private final String className;
    private final HollowStateEngine stateEngine;
    private final boolean parameterizeClassNames;
    private final String classPostfix;
    private final boolean useAggressiveSubstitutions;

    public HollowStateEngineClassJavaGenerator(String packageName, String className, HollowStateEngine stateEngine, boolean parameterizeClassNames, String classPostfix, boolean useAggressiveSubstitutions) {
        this.packageName = packageName;
        this.className = className;
        this.stateEngine = stateEngine;
        this.parameterizeClassNames = parameterizeClassNames;
        this.classPostfix = classPostfix;
        this.useAggressiveSubstitutions = useAggressiveSubstitutions;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String generate() {
        List<HollowSchema> schemaList = schemaList();

        StringBuilder builder = new StringBuilder();

        if(!"".equals(packageName)) {
            builder.append("package ").append(packageName).append(";\n\n");
        }

        builder.append("import " + HollowFactory.class.getName() + ";\n");
        builder.append("import " + HollowReadStateEngine.class.getName() + ";\n");
        builder.append("import " + HollowTypeReadState.class.getName() + ";\n");
        builder.append("import " + HollowObjectTypeReadState.class.getName() + ";\n");
        builder.append("import " + HollowListTypeReadState.class.getName() + ";\n");
        builder.append("import " + HollowSetTypeReadState.class.getName() + ";\n");
        builder.append("import " + HollowMapTypeReadState.class.getName() + ";\n\n");

        builder.append("import " + Collections.class.getName() + ";\n");
        builder.append("import " + Collection.class.getName() + ";\n");
        builder.append("import " + Set.class.getName() + ";\n");
        builder.append("import " + List.class.getName() + ";\n");
        builder.append("import " + ArrayList.class.getName() + ";\n");
        builder.append("import " + SampleResult.class.getName() + ";\n");
        builder.append("import " + HollowSamplingDirector.class.getName() + ";\n");
        builder.append("import " + HollowObjectCreationSampler.class.getName() + ";\n\n");

        builder.append("import " + HollowListDelegate.class.getName() + ";\n");
        builder.append("import " + HollowListLookupDelegate.class.getName() + ";\n");
        builder.append("import " + HollowSetDelegate.class.getName() + ";\n");
        builder.append("import " + HollowSetLookupDelegate.class.getName() + ";\n");
        builder.append("import " + HollowMapDelegate.class.getName() + ";\n");
        builder.append("import " + HollowMapLookupDelegate.class.getName() + ";\n");

        builder.append("import " + ArraySegmentRecycler.class.getName() + ";\n");
        builder.append("import " + RecyclingRecycler.class.getName() + ";\n\n");


        builder.append("import " + HollowObjectHashCodeFinder.class.getName() + ";\n");
        builder.append("import " + DefaultHashCodeFinder.class.getName() + ";\n\n");

        builder.append("@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" extends HollowAPI {\n\n");

        builder.append("    private final HollowObjectCreationSampler objectCreationSampler;\n\n");

        builder.append("    private Set<String> configCachedTypesSet;\n\n");

        for (HollowSchema schema : schemaList) {
            builder.append("    private " + typeAPIClassname(schema.getName())).append(" ").append(lowercase(typeAPIClassname(schema.getName()))).append(" = new ").append(typeAPIClassname(schema.getName())).append("(this, null);\n");
        }

        builder.append("\n\n");

        for(HollowSchema schema : schemaList) {
            if(schema instanceof HollowObjectSchema) {
                builder.append("    private " + delegateLookupImplName(schema.getName())).append(" ").append(lowercase(delegateLookupImplName(schema.getName())))
                    .append(" = new ").append(delegateLookupImplName(schema.getName())).append("(").append(lowercase(typeAPIClassname(schema.getName()))).append(");\n");

            } else if(schema instanceof HollowListSchema) {
                builder.append("    private HollowListDelegate ").append(lowercase(delegateLookupImplName(schema.getName())))
                    .append(" = new HollowListMissingDelegate(this, \"").append(schema.getName()).append("\");\n");
            } else if(schema instanceof HollowSetSchema) {
                builder.append("    private HollowSetDelegate ").append(lowercase(delegateLookupImplName(schema.getName())))
                    .append(" = new HollowSetMissingDelegate(this, \"").append(schema.getName()).append("\");\n");
            } else if(schema instanceof HollowMapSchema) {
                builder.append("    private HollowMapDelegate ").append(lowercase(delegateLookupImplName(schema.getName())))
                    .append(" = new HollowMapMissingDelegate(this, \"").append(schema.getName()).append("\");\n");
            }
        }

        builder.append("\n\n");

        for (HollowSchema schema : schemaList) {
            String factoryClassname = hollowFactoryClassname(schema.getName());
            builder.append("    private HollowFactory<?> ").append(lowercase(factoryClassname)).append(" = new ").append(factoryClassname).append("();\n");
        }

        builder.append("\n\n");

        appendConstructors(schemaList, builder);

        builder.append("    protected void setConfiguredCacheTypes(Set<String> configCachedTypesSet) {\n");
        builder.append("        this.configCachedTypesSet = configCachedTypesSet;\n");
        for(HollowSchema schema : schemaList) {
            String name = schema.getName();

            builder.append("        if(configCachedTypesSet.contains(\"").append(schema.getName()).append("\"))\n");
            builder.append("            ").append(lowercase(hollowFactoryClassname(name))).append(" = ").append("new DelegatingCacheHollowFactory(").append(lowercase(hollowFactoryClassname(name))).append(");\n");
        }
        builder.append("    }\n\n");

        builder.append("    @Override\n");
        builder.append("    protected void addTypeState(HollowTypeReadState typeState) {\n");
        builder.append("        super.addTypeState(typeState);\n");

        for (HollowSchema schema : schemaList) {
            String name = schema.getName();

            String typeStateCast = getTypeStateCast(schema);

            builder.append("        if(typeState.getSchema().getName().equals(\"").append(name).append("\")) {\n");
            builder.append("            ").append(lowercase(typeAPIClassname(name))).append(" = ").append("new ").append(typeAPIClassname(name)).append("(this, (").append(typeStateCast).append(")typeState);\n");
            if(schema instanceof HollowObjectSchema)
                builder.append("            ").append(lowercase(delegateLookupImplName(name))).append(" = new ").append(delegateLookupClassname(schema)).append("(").append(lowercase(typeAPIClassname(name))).append(");\n");
            else
                builder.append("            ").append(lowercase(delegateLookupImplName(name))).append(" = new ").append(delegateLookupClassname(schema)).append("((").append(typeStateCast).append(")typeState);\n");
            builder.append("            ").append(lowercase(hollowFactoryClassname(name))).append(".setTypeState(typeState);\n");
            builder.append("        }\n");
        }


        builder.append("    }\n\n");


        builder.append("\n\n");

        for (HollowSchema schema : schemaList) {
            builder.append("    public " + typeAPIClassname(schema.getName())).append(" get").append(typeAPIClassname(schema.getName())).append("() {\n");
            builder.append("        return " + lowercase(typeAPIClassname(schema.getName()))).append(";\n");
            builder.append("    };\n\n");
        }

        for (HollowSchema schema : schemaList) {
            builder.append("    public " + delegateInterfaceName(schema)).append(" get").append(delegateLookupImplName(schema.getName())).append("() {\n");
            builder.append("        return " + lowercase(delegateLookupImplName(schema.getName()))).append(";\n");
            builder.append("    };\n\n");
        }


        for (int i=0;i<schemaList.size();i++) {
            HollowSchema schema = schemaList.get(i);
            String hollowImplClassname = hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions);
            if(parameterizeClassNames)
                builder.append("    public <T> T get").append(hollowImplClassname).append("(int ordinal){\n");
            else
                builder.append("    public ").append(hollowImplClassname).append(" get").append(hollowImplClassname).append("(int ordinal){\n");
            builder.append("        objectCreationSampler.recordCreation(").append(i).append(");\n");
            builder.append("        return ").append(parameterizeClassNames ? "(T)" : "").append(" " + lowercase(hollowFactoryClassname(schema.getName())) + ".getHollowObject(ordinal);\n");
            builder.append("    }\n\n");
        }

        for (HollowSchema schema : schemaList) {
            builder.append("    public <T> void set" + hollowFactoryClassname(schema.getName()) + "(HollowFactory<T> factory) {\n");
            builder.append("        if(configCachedTypesSet.contains(\"").append(schema.getName()).append("\"))\n");
            builder.append("            factory = new DelegatingCacheHollowFactory<T>(factory);\n");
            builder.append("        HollowTypeReadState typeState = getTypeState(\"").append(schema.getName()).append("\");\n");
            builder.append("        if(typeState != null)\n");
            builder.append("            factory.setTypeState(getTypeState(\"").append(schema.getName()).append("\"));\n");
            builder.append("        addFactory(\"").append(schema.getName()).append("\", factory);\n");
            builder.append("        this.").append(lowercase(hollowFactoryClassname(schema.getName()))).append(" = factory;\n");
            builder.append("    }\n\n");
        }

        builder.append("    public void setSamplingDirector(HollowSamplingDirector samplingDirector) {\n");
        builder.append("        super.setSamplingDirector(samplingDirector);\n");
        builder.append("        objectCreationSampler.setSamplingDirector(samplingDirector);\n");
        for(HollowSchema schema : schemaList) {
            if(schema instanceof HollowObjectSchema) {
                builder.append("        if(").append(lowercase(typeAPIClassname(schema.getName()))).append(" != null)\n");
                builder.append("            ").append(lowercase(typeAPIClassname(schema.getName())))
                    .append(".getBoxedFieldAccessSampler().setSamplingDirector(samplingDirector);\n");
            }
        }
        builder.append("    }\n\n");

        builder.append("    public Collection<SampleResult> getBoxedObjectCreationSampleResults() {\n");
        builder.append("        List<SampleResult> aggregateList = new ArrayList<SampleResult>();\n");
        for(HollowSchema schema : schemaList) {
            if(schema instanceof HollowObjectSchema) {
                builder.append("        if(").append(lowercase(typeAPIClassname(schema.getName()))).append(" != null)\n");
                builder.append("            aggregateList.addAll(").append(lowercase(typeAPIClassname(schema.getName())))
                    .append(".getBoxedFieldAccessSampler().getSampleResults());\n");
            }
        }
        builder.append("        Collections.sort(aggregateList);\n");
        builder.append("        return aggregateList;\n");
        builder.append("    }\n\n");

        builder.append("    public Collection<SampleResult> getHollowObjectCreationSampleResults() {\n");
        builder.append("        return objectCreationSampler.getSampleResults();\n");
        builder.append("    }\n\n");

        builder.append("}");

        return builder.toString();
    }

    private void appendConstructors(List<HollowSchema> schemaList, StringBuilder builder) {
        builder.append("    public ").append(className).append("() {\n");
        builder.append("        this(new DefaultHashCodeFinder());\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(className).append("(HollowObjectHashCodeFinder hasher) {\n");
        builder.append("        this(hasher, new RecyclingRecycler(11, 8));\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(className).append("(HollowObjectHashCodeFinder hasher, ArraySegmentRecycler memoryRecycler) {\n");
        builder.append("        super(hasher, false, memoryRecycler);\n");
        builder.append("        this.objectCreationSampler = new HollowObjectCreationSampler(new String[] {\n");
        for(int i=0;i<schemaList.size();i++) {
            builder.append("                \"").append(schemaList.get(i).getName()).append("\"");
            if(i < schemaList.size() - 1)
                builder.append(",\n");
            else
                builder.append("\n");

        }
        builder.append("            }, samplingDirector);\n");
        builder.append("        this.configCachedTypesSet = Collections.emptySet();\n");
        builder.append("    }\n\n");
    }

    private List<HollowSchema> schemaList() {
        // Sort Schema to have consistent ordering when generating VMSHollowStateEngine
        List<HollowSchema> schemaList = new ArrayList<HollowSchema>(stateEngine.getSchemas());
        Collections.sort(schemaList, new Comparator<HollowSchema>() {
            @Override
            public int compare(HollowSchema o1, HollowSchema o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return schemaList;
    }

    private String getTypeStateCast(HollowSchema schema) {
        if(schema instanceof HollowObjectSchema)
            return HollowObjectTypeReadState.class.getSimpleName();
        if(schema instanceof HollowListSchema)
            return HollowListTypeReadState.class.getSimpleName();
        if(schema instanceof HollowSetSchema)
            return HollowSetTypeReadState.class.getSimpleName();
        if(schema instanceof HollowMapSchema)
            return HollowMapTypeReadState.class.getSimpleName();
        throw new UnsupportedOperationException("What kind of schema is a " + schema.getClass().getSimpleName() + "?");
    }

}
