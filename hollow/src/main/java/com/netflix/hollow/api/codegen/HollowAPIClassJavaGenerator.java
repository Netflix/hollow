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

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowFactoryClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowObjectProviderName;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.lowercase;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.typeAPIClassname;

import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.api.objects.provider.HollowObjectCacheProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectFactoryProvider;
import com.netflix.hollow.api.objects.provider.HollowObjectProvider;
import com.netflix.hollow.api.sampling.HollowObjectCreationSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import com.netflix.hollow.core.util.AllHollowRecordCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 *
 * @see HollowAPIGenerator
 */
public class HollowAPIClassJavaGenerator extends HollowConsumerJavaFileGenerator {
    public static final String SUB_PACKAGE_NAME = "";

    private final boolean parameterizeClassNames;

    public HollowAPIClassJavaGenerator(String packageName, String apiClassname, HollowDataset dataset, boolean parameterizeClassNames, CodeGeneratorConfig config) {
        super(packageName, SUB_PACKAGE_NAME, dataset, config);
        this.className = apiClassname;
        this.parameterizeClassNames = parameterizeClassNames;
    }

    @Override
    public String generate() {
        List<HollowSchema> schemaList = HollowSchemaSorter.dependencyOrderedSchemaList(dataset);

        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);

        builder.append("import ").append(Collection.class.getName()).append(";\n");
        builder.append("import ").append(Collections.class.getName()).append(";\n");
        builder.append("import ").append(Set.class.getName()).append(";\n");
        builder.append("import ").append(Map.class.getName()).append(";\n");
        builder.append("import ").append(HollowConsumerAPI.class.getName()).append(";\n");
        builder.append("import ").append(HollowAPI.class.getName()).append(";\n");
        builder.append("import ").append(HollowDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowListTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowSetTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowMapTypeDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectMissingDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowListMissingDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowSetMissingDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowMapMissingDataAccess.class.getName()).append(";\n");
        builder.append("import ").append(HollowFactory.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectProvider.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectCacheProvider.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectFactoryProvider.class.getName()).append(";\n");
        builder.append("import ").append(HollowObjectCreationSampler.class.getName()).append(";\n");
        builder.append("import ").append(HollowSamplingDirector.class.getName()).append(";\n");
        builder.append("import ").append(SampleResult.class.getName()).append(";\n");
        builder.append("import ").append(AllHollowRecordCollection.class.getName()).append(";\n");

        builder.append("\n@SuppressWarnings(\"all\")\n");
        builder.append("public class ").append(className).append(" extends HollowAPI ");
        Set<String> primitiveTypes = HollowCodeGenerationUtils.getPrimitiveTypes(schemaList); // Implement Primitive Type Retriever(s)
        if (config.isUseHollowPrimitiveTypes() && !primitiveTypes.isEmpty()) {
            builder.append("implements ");
            int itemCount = 0;
            for(String pType : primitiveTypes) {
                if (itemCount++ > 0) builder.append(",");

                builder.append(" HollowConsumerAPI.").append(HollowCodeGenerationUtils.upperFirstChar(pType)).append("Retriever");
            }
        }
        builder.append(" {\n\n");

        builder.append("    private final HollowObjectCreationSampler objectCreationSampler;\n\n");

        for (HollowSchema schema : schemaList) {
            builder.append("    private final " + typeAPIClassname(schema.getName())).append(" ").append(lowercase(typeAPIClassname(schema.getName()))).append(";\n");
        }

        builder.append("\n");

        for(HollowSchema schema : schemaList) {
            builder.append("    private final HollowObjectProvider ").append(hollowObjectProviderName(schema.getName())).append(";\n");
        }

        builder.append("\n");

        builder.append("    public ").append(className).append("(HollowDataAccess dataAccess) {\n");
        builder.append("        this(dataAccess, Collections.<String>emptySet());\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(className).append("(HollowDataAccess dataAccess, Set<String> cachedTypes) {\n");
        builder.append("        this(dataAccess, cachedTypes, Collections.<String, HollowFactory<?>>emptyMap());\n");
        builder.append("    }\n\n");


        builder.append("    public ").append(className).append("(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides) {\n");
        builder.append("        this(dataAccess, cachedTypes, factoryOverrides, null);\n");
        builder.append("    }\n\n");

        builder.append("    public ").append(className).append("(HollowDataAccess dataAccess, Set<String> cachedTypes, Map<String, HollowFactory<?>> factoryOverrides, ").append(className).append(" previousCycleAPI) {\n");
        builder.append("        super(dataAccess);\n");
        builder.append("        HollowTypeDataAccess typeDataAccess;\n");
        builder.append("        HollowFactory factory;\n\n");
        builder.append("        objectCreationSampler = new HollowObjectCreationSampler(");
        for(int i=0;i<schemaList.size();i++) {
            builder.append("\"").append(schemaList.get(i).getName()).append("\"");
            if(i < schemaList.size() - 1)
                builder.append(",");
        }
        builder.append(");\n\n");

        for (HollowSchema schema : schemaList) {
            builder.append("        typeDataAccess = dataAccess.getTypeDataAccess(\"").append(schema.getName()).append("\");\n");
            builder.append("        if(typeDataAccess != null) {\n");
            builder.append("            ").append(lowercase(typeAPIClassname(schema.getName()))).append(" = new ").append(typeAPIClassname(schema.getName())).append("(this, (Hollow").append(schemaType(schema)).append("TypeDataAccess)typeDataAccess);\n");
            builder.append("        } else {\n");
            builder.append("            ").append(lowercase(typeAPIClassname(schema.getName()))).append(" = new ").append(typeAPIClassname(schema.getName())).append("(this, new Hollow").append(schemaType(schema)).append("MissingDataAccess(dataAccess, \"").append(schema.getName()).append("\"));\n");
            builder.append("        }\n");
            builder.append("        addTypeAPI(").append(lowercase(typeAPIClassname(schema.getName()))).append(");\n");
            builder.append("        factory = factoryOverrides.get(\"").append(schema.getName()).append("\");\n");
            builder.append("        if(factory == null)\n");
            builder.append("            factory = new ").append(hollowFactoryClassname(schema.getName())).append("();\n");
            builder.append("        if(cachedTypes.contains(\"").append(schema.getName()).append("\")) {\n");
            builder.append("            HollowObjectCacheProvider previousCacheProvider = null;\n");
            builder.append("            if(previousCycleAPI != null && (previousCycleAPI.").append(hollowObjectProviderName(schema.getName())).append(" instanceof HollowObjectCacheProvider))\n");
            builder.append("                previousCacheProvider = (HollowObjectCacheProvider) previousCycleAPI.").append(hollowObjectProviderName(schema.getName())).append(";\n");
            builder.append("            ").append(hollowObjectProviderName(schema.getName())).append(" = new HollowObjectCacheProvider(typeDataAccess, ").append(lowercase(typeAPIClassname(schema.getName()))).append(", factory, previousCacheProvider);\n");
            builder.append("        } else {\n");
            builder.append("            ").append(hollowObjectProviderName(schema.getName())).append(" = new HollowObjectFactoryProvider(typeDataAccess, ").append(lowercase(typeAPIClassname(schema.getName()))).append(", factory);\n");
            builder.append("        }\n\n");
        }

        builder.append("    }\n\n");


        builder.append("    public void detachCaches() {\n");
        for(HollowSchema schema : schemaList) {
            builder.append("        if(").append(hollowObjectProviderName(schema.getName())).append(" instanceof HollowObjectCacheProvider)\n");
            builder.append("            ((HollowObjectCacheProvider)").append(hollowObjectProviderName(schema.getName())).append(").detach();\n");
        }
        builder.append("    }\n\n");


        for (HollowSchema schema : schemaList) {
            builder.append("    public ").append(typeAPIClassname(schema.getName())).append(" get" + typeAPIClassname(schema.getName())).append("() {\n");
            builder.append("        return ").append(lowercase(typeAPIClassname(schema.getName()))).append(";\n");
            builder.append("    }\n");
        }

        for(int i=0;i<schemaList.size();i++) {
            HollowSchema schema = schemaList.get(i);
            if(parameterizeClassNames) {
                builder.append("    public <T> Collection<T> getAll").append(hollowImplClassname(schema.getName())).append("() {\n");
                builder.append("        return new AllHollowRecordCollection<T>(getDataAccess().getTypeDataAccess(\"").append(schema.getName()).append("\").getTypeState()) {\n");
                builder.append("            protected T getForOrdinal(int ordinal) {\n");
                builder.append("                return get").append(hollowImplClassname(schema.getName())).append("(ordinal);\n");
                builder.append("            }\n");
                builder.append("        };\n");
                builder.append("    }\n");

                builder.append("    public <T> T get").append(hollowImplClassname(schema.getName())).append("(int ordinal) {\n");
                builder.append("        objectCreationSampler.recordCreation(").append(i).append(");\n");
                builder.append("        return (T) ").append(hollowObjectProviderName(schema.getName())).append(".getHollowObject(ordinal);\n");
                builder.append("    }\n");
            } else {
                String hollowImplClassname = hollowImplClassname(schema.getName());

                builder.append("    public Collection<"+hollowImplClassname+"> getAll").append(hollowImplClassname).append("() {\n");
                builder.append("        return new AllHollowRecordCollection<"+hollowImplClassname+">(getDataAccess().getTypeDataAccess(\"").append(schema.getName()).append("\").getTypeState()) {\n");
                builder.append("            protected "+hollowImplClassname+" getForOrdinal(int ordinal) {\n");
                builder.append("                return get").append(hollowImplClassname).append("(ordinal);\n");
                builder.append("            }\n");
                builder.append("        };\n");
                builder.append("    }\n");

                builder.append("    public ").append(hollowImplClassname).append(" get").append(hollowImplClassname).append("(int ordinal) {\n");
                builder.append("        objectCreationSampler.recordCreation(").append(i).append(");\n");
                builder.append("        return (").append(hollowImplClassname).append(")").append(hollowObjectProviderName(schema.getName())).append(".getHollowObject(ordinal);\n");
                builder.append("    }\n");
            }
        }

        builder.append("    public void setSamplingDirector(HollowSamplingDirector director) {\n");
        builder.append("        super.setSamplingDirector(director);\n");
        builder.append("        objectCreationSampler.setSamplingDirector(director);\n");
        builder.append("    }\n\n");

        builder.append("    public Collection<SampleResult> getObjectCreationSamplingResults() {\n");
        builder.append("        return objectCreationSampler.getSampleResults();\n");
        builder.append("    }\n\n");

        builder.append("}\n");

        return builder.toString();
    }

    private String schemaType(HollowSchema schema) {
        switch(schema.getSchemaType()) {
        case OBJECT:
            return "Object";
        case LIST:
            return "List";
        case SET:
            return "Set";
        case MAP:
            return "Map";
        default:
            throw new IllegalArgumentException();
        }
    }

}
