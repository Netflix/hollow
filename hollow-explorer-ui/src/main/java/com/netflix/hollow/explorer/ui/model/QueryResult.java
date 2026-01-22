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
package com.netflix.hollow.explorer.ui.model;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.query.HollowFieldMatchQuery;
import com.netflix.hollow.tools.traverse.TransitiveSetTraverser;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueryResult {
    
    private final List<QueryClause> queryClauses;
    private final Map<String, BitSet> queryMatches;
    private long randomizedStateTag;
    
    public QueryResult(long randomizedStateTag) {
        this.queryClauses = new ArrayList<QueryClause>();
        this.queryMatches = new HashMap<String, BitSet>();
        this.randomizedStateTag = randomizedStateTag;
    }
    
    public List<QueryClause> getQueryClauses() {
        return queryClauses;
    }
    
    public String getQueryDisplayString() {
        StringBuilder builder = new StringBuilder();
        
        for(int i=0;i<queryClauses.size();i++) {
            if(i > 0)
                builder.append(" AND ");
            builder.append(queryClauses.get(i));
        }
        
        return builder.toString();
    }

    public Map<String, BitSet> getQueryMatches() {
        return queryMatches;
    }
    
    public void recalculateIfNotCurrent(HollowReadStateEngine stateEngine) {
        if(stateEngine.getCurrentRandomizedTag() != randomizedStateTag) {        
            queryMatches.clear();
            List<QueryClause> requeryClauses = new ArrayList<QueryClause>(this.queryClauses);
            this.queryClauses.clear();
    
            for(QueryClause clause : requeryClauses)
                augmentQuery(clause, stateEngine);
        
            randomizedStateTag = stateEngine.getCurrentRandomizedTag();
        }
    }
    
    public void augmentQuery(QueryClause clause, HollowReadStateEngine stateEngine) {
        HollowFieldMatchQuery query = new HollowFieldMatchQuery(stateEngine);
        Map<String, BitSet> clauseMatches = clause.getType() != null ? query.findMatchingRecords(clause.getType(), clause.getField(), clause.getValue()) : query.findMatchingRecords(clause.getField(), clause.getValue());
        TransitiveSetTraverser.addReferencingOutsideClosure(stateEngine, clauseMatches);
                
        if(queryClauses.isEmpty())
            queryMatches.putAll(clauseMatches);
        else
            booleanAndQueryMatches(clauseMatches);
        
        queryClauses.add(clause);
    }
    
    public void booleanAndQueryMatches(Map<String, BitSet> newQueryMatches) {
        Iterator<Map.Entry<String, BitSet>> iter = queryMatches.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, BitSet> existingEntry = iter.next();
            BitSet newTypeMatches = newQueryMatches.get(existingEntry.getKey());
            if(newTypeMatches != null) {
                existingEntry.getValue().and(newTypeMatches);
            } else {
                iter.remove();
            }
        }
    }
    
    public List<QueryTypeMatches> getTypeMatches() {
        List<QueryTypeMatches> list = new ArrayList<QueryTypeMatches>();
        
        for(Map.Entry<String, BitSet> entry : queryMatches.entrySet()) {
            int numTypeMatches = entry.getValue().cardinality();
            if(numTypeMatches > 0)
                list.add(new QueryTypeMatches(entry.getKey(), numTypeMatches));
        }
        
        Collections.sort(list, new Comparator<QueryTypeMatches>() {
            public int compare(QueryTypeMatches o1, QueryTypeMatches o2) {
                return Integer.compare(o2.getNumMatches(), o1.getNumMatches());
            }
        });
        
        return list;
    }

    public void removeQueryClause(String fieldName) {
        queryClauses.removeIf(clause -> clause.getField().equals(fieldName));
    }
    
    public static class QueryClause {
        private final String type;
        private final String field;
        private final String value;
        
        public QueryClause(String type, String field, String value) {
            this.type = type;
            this.field = field;
            this.value = value;
        }
        
        public String getType() {
            return type;
        }
        public String getField() {
            return field;
        }
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            
            if(type != null)
                builder.append(type).append(".");
            
            builder.append(field).append("=\"").append(value).append("\"");
            
            return builder.toString();
        }
    }

    public static class QueryTypeMatches {
        private final String typeName;
        private final int numMatches;
        
        public QueryTypeMatches(String typeName, int numMatches) {
            this.typeName = typeName;
            this.numMatches = numMatches;
        }

        public String getTypeName() {
            return typeName;
        }
        public int getNumMatches() {
            return numMatches;
        }
    }

}
