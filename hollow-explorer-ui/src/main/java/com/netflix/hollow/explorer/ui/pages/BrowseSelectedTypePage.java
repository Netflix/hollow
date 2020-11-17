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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.tools.util.SearchUtils.MULTI_FIELD_KEY_DELIMITER;
import static com.netflix.hollow.tools.util.SearchUtils.getFieldPathIndexes;
import static com.netflix.hollow.tools.util.SearchUtils.getOrdinalToDisplay;
import static com.netflix.hollow.tools.util.SearchUtils.getPrimaryKey;
import static com.netflix.hollow.tools.util.SearchUtils.parseKey;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.explorer.ui.HollowExplorerUI;
import com.netflix.hollow.explorer.ui.model.QueryResult;
import com.netflix.hollow.explorer.ui.model.TypeKey;
import com.netflix.hollow.tools.stringifier.HollowRecordJsonStringifier;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import com.netflix.hollow.tools.stringifier.HollowStringifier;
import com.netflix.hollow.ui.HollowUISession;
import com.netflix.hollow.ui.HtmlEscapingWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.VelocityContext;

public class BrowseSelectedTypePage extends HollowExplorerPage {
    private static final Logger LOG = Logger.getLogger(BrowseSelectedTypePage.class.getName());
    private static final String SESSION_ATTR_QUERY_RESULT = "query-result";

    public BrowseSelectedTypePage(HollowExplorerUI ui) {
        super(ui);
    }

    @Override
    protected void setUpContext(HttpServletRequest req, HollowUISession session, VelocityContext ctx) {
        HollowTypeReadState typeState = getTypeState(req);
        
        int page = req.getParameter("page") == null ? 0 : Integer.parseInt(req.getParameter("page"));
        int pageSize = req.getParameter("pageSize") == null ? 20 : Integer.parseInt(req.getParameter("pageSize"));
        int startRec = page * pageSize;
        
        String displayFormat = "json".equals(req.getParameter("display")) ? "json" : "text";
        
        BitSet selectedOrdinals = typeState.getPopulatedOrdinals();

        if("true".equals(req.getParameter("clearQuery")))
            session.clearAttribute(SESSION_ATTR_QUERY_RESULT);

        if(session.getAttribute(SESSION_ATTR_QUERY_RESULT) != null) {
            QueryResult queryResult =
                (QueryResult) session.getAttribute(SESSION_ATTR_QUERY_RESULT);
            queryResult.recalculateIfNotCurrent(ui.getStateEngine());
            
            selectedOrdinals = queryResult.getQueryMatches().get(typeState.getSchema().getName());
            if(selectedOrdinals == null)
                selectedOrdinals = new BitSet();
            
            ctx.put("filteredByQuery", queryResult.getQueryDisplayString());
        }
        
        int currentOrdinal = selectedOrdinals.nextSetBit(0);
        for(int i=0;i<startRec;i++) {
            currentOrdinal = selectedOrdinals.nextSetBit(currentOrdinal+1);
        }
        
        PrimaryKey primaryKey = getPrimaryKey(typeState.getSchema());
        int fieldPathIndexes[][] = getFieldPathIndexes(ui.getStateEngine(), primaryKey);
        
        List<TypeKey> keys = new ArrayList<>(pageSize);
        
        for(int i = 0; i < pageSize && currentOrdinal != ORDINAL_NONE; i ++) {
            keys.add(getKey(startRec + i, typeState, currentOrdinal, fieldPathIndexes));
            currentOrdinal = selectedOrdinals.nextSetBit(currentOrdinal + 1);
        }

        
        String key = req.getParameter("key") == null ? "" : req.getParameter("key");
        Object parsedKey[] = null;
        try {
            parsedKey = parseKey(ui.getStateEngine(), primaryKey, key);
        } catch(Exception e) {
            LOG.log(Level.WARNING, String.format("Failed to parse query=%s into %s", key, primaryKey.toString()), e);
            key = "";
        }

        HollowTypeReadState readTypeState = getTypeState(req);
        int ordinal = req.getParameter("ordinal") == null ? ORDINAL_NONE : Integer.parseInt(req.getParameter("ordinal"));
        ordinal = getOrdinalToDisplay(ui.getStateEngine(), key, parsedKey, ordinal, selectedOrdinals, fieldPathIndexes, readTypeState);
        if (ordinal != ORDINAL_NONE && "".equals(key)
                && fieldPathIndexes != null) {
            // set key for the case where it was unset previously
            key = getKey(ORDINAL_NONE, typeState, ordinal, fieldPathIndexes).getKey();
        }

        int numRecords = selectedOrdinals.cardinality();

        ctx.put("keys", keys);
        ctx.put("page", page);
        ctx.put("pageSize", pageSize);
        ctx.put("numPages", ((numRecords - 1) / pageSize) + 1);
        ctx.put("numRecords", numRecords);
        ctx.put("type", typeState.getSchema().getName());
        ctx.put("key", key);
        ctx.put("ordinal", ordinal);
        ctx.put("display", displayFormat);
    }

    @Override
    protected void renderPage(HttpServletRequest req, VelocityContext ctx, Writer writer) {
        String key = (String) ctx.get("key");
        Integer ordinal = (Integer) ctx.get("ordinal");
        ui.getVelocityEngine().getTemplate("browse-selected-type-top.vm").merge(ctx, writer);
        try {
            Writer htmlEscapingWriter = new HtmlEscapingWriter(writer);
            if (!"".equals(key) && ordinal != null && ordinal.equals(ORDINAL_NONE)) {
                htmlEscapingWriter.append("ERROR: Key " + key + " was not found!");
            } else if (ordinal != null && !ordinal.equals(ORDINAL_NONE)) {
                HollowStringifier stringifier = "json".equals(req.getParameter("display"))
                    ? new HollowRecordJsonStringifier() : new HollowRecordStringifier();
                stringifier.stringify(htmlEscapingWriter, ui.getStateEngine(),
                    getTypeState(req).getSchema().getName(), ordinal);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error streaming response", e);
        }
        ui.getVelocityEngine().getTemplate("browse-selected-type-bottom.vm").merge(ctx, writer);
    }

    private TypeKey getKey(int recordIdx, HollowTypeReadState typeState, int ordinal, int[][] fieldPathIndexes) {
        if(fieldPathIndexes != null) {
            StringBuilder keyBuilder = new StringBuilder();

            HollowObjectTypeReadState objState = (HollowObjectTypeReadState)typeState;

            for(int i=0;i<fieldPathIndexes.length;i++) {
                int curOrdinal = ordinal;
                HollowObjectTypeReadState curState = objState;

                for(int j=0;j<fieldPathIndexes[i].length - 1;j++) {
                    curOrdinal = curState.readOrdinal(curOrdinal, fieldPathIndexes[i][j]);
                    curState = (HollowObjectTypeReadState) curState.getSchema().getReferencedTypeState(fieldPathIndexes[i][j]);
                }

                if(i > 0)
                    keyBuilder.append(MULTI_FIELD_KEY_DELIMITER);

                keyBuilder.append(HollowReadFieldUtils.fieldValueObject(curState, curOrdinal, fieldPathIndexes[i][fieldPathIndexes[i].length - 1]));
            }

            return new TypeKey(recordIdx, ordinal, keyBuilder.toString());
        }

        return new TypeKey(recordIdx, ordinal, "", "ORDINAL:" + ordinal);
    }

    private HollowTypeReadState getTypeState(HttpServletRequest req) {
        return ui.getStateEngine().getTypeState(req.getParameter("type"));
    }
}
