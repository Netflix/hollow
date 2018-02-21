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
package com.netflix.hollow.explorer.ui.pages;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.TypeOverview;
import com.netflix.hollow.ui.HollowUISession;
import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            int numRecords = typeState.getPopulatedOrdinals() == null ? Integer.MIN_VALUE : typeState.getPopulatedOrdinals().cardinality();
            PrimaryKey primaryKey = typeState.getSchema().getSchemaType() == SchemaType.OBJECT ? ((HollowObjectSchema)typeState.getSchema()).getPrimaryKey() : null;
            long approxHeapFootprint = typeState.getApproximateHeapFootprintInBytes();
            HollowSchema schema = typeState.getSchema();
            
            typeOverviews.add(new TypeOverview(typeName, numRecords, approxHeapFootprint, primaryKey, schema));
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
        case "heapSize":
            Collections.sort(typeOverviews, new Comparator<TypeOverview>() {
                public int compare(TypeOverview o1, TypeOverview o2) {
                    return Long.compare(o2.getApproxHeapFootprintLong(), o1.getApproxHeapFootprintLong());
                }
            });
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
        
        ctx.put("totalHeapFootprint", totalApproximateHeapFootprint(typeOverviews));
        ctx.put("typeOverviews", typeOverviews);
    }

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        ui.getVelocityEngine().getTemplate("show-all-types.vm").merge(ctx, writer);
    }
    
    private String totalApproximateHeapFootprint(List<TypeOverview> allTypes) {
        long totalHeapFootprint = 0;
        for(TypeOverview type : allTypes)
            totalHeapFootprint += type.getApproxHeapFootprintLong();
        return TypeOverview.heapFootprintDisplayString(totalHeapFootprint);
    }
}
