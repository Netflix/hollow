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
package com.netflix.hollow.explorer.ui.pages;

import static com.netflix.hollow.ui.HollowDiffUtil.formatBytes;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaUtil;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.TypeOverview;
import com.netflix.hollow.ui.HollowUISession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class ShowAllTypesPage extends HollowExplorerPage {

    public ShowAllTypesPage(HollowExplorerUI ui) {
        super(ui);
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        
        String sort = req.getParameter("sort") == null ? "primaryKey" : req.getParameter("sort");
        
        List<TypeOverview> typeOverviews = new ArrayList<TypeOverview>();

        for(HollowTypeReadState typeState : ui.getStateEngine().getTypeStates()) {
            String typeName = typeState.getSchema().getName();
            BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
            int numRecords = populatedOrdinals == null ? Integer.MIN_VALUE : populatedOrdinals.cardinality();
            int numHoles = populatedOrdinals == null ? Integer.MIN_VALUE : populatedOrdinals.length()-populatedOrdinals.cardinality();
            long approxHoleFootprint = typeState.getApproximateHoleCostInBytes();
            PrimaryKey primaryKey = typeState.getSchema().getSchemaType() == SchemaType.OBJECT ? ((HollowObjectSchema)typeState.getSchema()).getPrimaryKey() : null;
            long approxHeapFootprint = typeState.getApproximateHeapFootprintInBytes();
            HollowSchema schema = typeState.getSchema();
            int numShards = typeState.numShards();

            // Get number of partitions for object types
            int numPartitions = 1;
            if (typeState instanceof HollowObjectTypeReadState) {
                numPartitions = ((HollowObjectTypeReadState) typeState).getNumPartitions();
            }

            typeOverviews.add(new TypeOverview(typeName, numRecords, numHoles, approxHoleFootprint, approxHeapFootprint, primaryKey, schema, numShards, numPartitions));
        }

        switch(sort) {
        case "typeName":
            Collections.sort(typeOverviews, new Comparator<TypeOverview>() {
                public int compare(TypeOverview o1, TypeOverview o2) {
                    return o1.getTypeName().compareTo(o2.getTypeName());
                }
            });
            break;
        case "numRecords":
            Collections.sort(typeOverviews, new Comparator<TypeOverview>() {
                public int compare(TypeOverview o1, TypeOverview o2) {
                    return Integer.compare(o2.getNumRecordsInt(), o1.getNumRecordsInt());
                }
            });
            break;
        case "numHoles":
                typeOverviews.sort((o1, o2) -> Integer.compare(o2.getNumHolesInt(), o1.getNumHolesInt()));
                break;
        case "holeSize":
                typeOverviews.sort((o1, o2) -> Long.compare(o2.getApproxHoleFootprintLong(), o1.getApproxHoleFootprintLong()));
                break;
        case "heapSize":
            Collections.sort(typeOverviews, new Comparator<TypeOverview>() {
                public int compare(TypeOverview o1, TypeOverview o2) {
                    return Long.compare(o2.getApproxHeapFootprintLong(), o1.getApproxHeapFootprintLong());
                }
            });
            break;
        case "numShards":
            typeOverviews.sort((o1, o2) -> Integer.compare(o2.getNumShardsInt(), o1.getNumShardsInt()));
            break;
        default:
            Collections.sort(typeOverviews, new Comparator<TypeOverview>() {
                public int compare(TypeOverview o1, TypeOverview o2) {
                    if(!"".equals(o1.getPrimaryKey()) && "".equals(o2.getPrimaryKey()))
                        return -1;
                    if("".equals(o1.getPrimaryKey()) && !"".equals(o2.getPrimaryKey()))
                        return 1;
                    
                    return o1.getTypeName().compareTo(o2.getTypeName()); 
                }
            });
        }

        ctx.put("totalHoleFootprint", totalApproximateHoleFootprint(typeOverviews));
        ctx.put("totalHeapFootprint", totalApproximateHeapFootprint(typeOverviews));
        ctx.put("typeOverviews", typeOverviews);
        ctx.put("topLevelTypes", HollowSchemaUtil.getTopLevelTypes(ui.getStateEngine()));
    }

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        ui.getVelocityEngine().getTemplate("show-all-types.vm").merge(ctx, writer);
    }
    
    private String totalApproximateHeapFootprint(List<TypeOverview> allTypes) {
        long totalHeapFootprint = 0;
        for(TypeOverview type : allTypes)
            totalHeapFootprint += type.getApproxHeapFootprintLong();
        return formatBytes(totalHeapFootprint);
    }

    private String totalApproximateHoleFootprint(List<TypeOverview> allTypes) {
        long totalFootprint = 0;
        for(TypeOverview type : allTypes)
            totalFootprint += type.getApproxHoleFootprintLong();
        return formatBytes(totalFootprint);
    }
}
