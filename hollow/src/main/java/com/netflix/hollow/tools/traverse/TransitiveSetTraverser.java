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
package com.netflix.hollow.tools.traverse;

import static com.netflix.hollow.tools.traverse.TransitiveSetTraverser.TransitiveSetTraverserAction.ADD_REFERENCING_OUTSIDE_CLOSURE;
import static com.netflix.hollow.tools.traverse.TransitiveSetTraverser.TransitiveSetTraverserAction.REMOVE_REFERENCED_OUTSIDE_CLOSURE;

import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The TransitiveSetTraverser can be used to find children and parent references for a selected set of records.  
 * A selection is represented with a Map&lt;String, BitSet&gt;, where each key in the map represents a type, and the corresponding BitSet
 * represents the ordinals of the selected records.  
 * Entries in this Map will indicate a type, plus the ordinals of the selected records:
 * <pre>
 * {@code
 * Map&lt;String, BitSet&gt; selection = new HashMap&lt;String, BitSet&gt;();
 *  
 * /// select the movies with ordinals 1 and 4.
 * BitSet selectedMovies = new BitSet();
 * selectedMovies.set(1);
 * selectedMovies.set(4);
 *  
 * selection.put("Movie", movies);
 * }
 * </pre>
 * <p>
 * We can add the references, and the <i>transitive</i> references, of our selection.  
 * After the following call returns, our selection will be augmented with these matches:
 * <pre>
 * {@code TransitiveSetTraverser.addTransitiveMatches(readEngine, selection);}
 * </pre>
 * <p>
 * Given a selection, we can also add any records which reference anything in the selection.  
 * This is essentially the opposite operation as above; it can be said that addTransitiveMatches 
 * traverses down, while addReferencingOutsideClosure traverses up.  After the following call returns, 
 * our selection will be augmented with this selection:
 *
 */
public class TransitiveSetTraverser {
    private static final Logger log = Logger.getLogger(TransitiveSetTraverser.class.getName());

    /**
     * Augment the given selection by adding the references, and the <i>transitive</i> references, of our selection.
     * @param stateEngine the state engine
     * @param matches the map to which matches are placed
     */
    public static void addTransitiveMatches(HollowReadStateEngine stateEngine, Map<String, BitSet> matches) {
        List<HollowSchema> schemaList = HollowSchemaSorter.dependencyOrderedSchemaList(stateEngine);
        Collections.reverse(schemaList);

        for(HollowSchema schema : schemaList) {
            BitSet currentMatches = matches.get(schema.getName());
            if(currentMatches != null) {
                addTransitiveMatches(stateEngine, schema.getName(), matches);
            }
        }
    }
    
    /**
     * Remove any records from the given selection which are referenced by other records not in the selection.
     * @param stateEngine the state engine
     * @param matches the matches
     */
    public static void removeReferencedOutsideClosure(HollowReadStateEngine stateEngine, Map<String, BitSet> matches) {
        List<HollowSchema> orderedSchemas = HollowSchemaSorter.dependencyOrderedSchemaList(stateEngine);
        Collections.reverse(orderedSchemas);


        for(HollowSchema referencedSchema : orderedSchemas) {
            if(matches.containsKey(referencedSchema.getName())) {
                for(HollowSchema referencerSchema : orderedSchemas) {
                    if(referencerSchema == referencedSchema)
                        break;

                    if(matches.containsKey(referencedSchema.getName()) && matches.get(referencedSchema.getName()).cardinality() > 0)
                        traverseReferencesOutsideClosure(stateEngine, referencerSchema.getName(), referencedSchema.getName(), matches, REMOVE_REFERENCED_OUTSIDE_CLOSURE);
                }
            }
        }
    }
    
    /**
     * Augment the given selection with any records outside the selection which reference 
     * (or transitively reference) any records in the selection. 
     * @param stateEngine the state engine
     * @param matches the matches
     */
    public static void addReferencingOutsideClosure(HollowReadStateEngine stateEngine, Map<String, BitSet> matches) {
        List<HollowSchema> orderedSchemas = HollowSchemaSorter.dependencyOrderedSchemaList(stateEngine);

        for(HollowSchema referencerSchema : orderedSchemas) {
            for(HollowSchema referencedSchema : orderedSchemas) {
                if(referencedSchema == referencerSchema)
                    break;

                if(matches.containsKey(referencedSchema.getName()) && matches.get(referencedSchema.getName()).cardinality() > 0)
                    traverseReferencesOutsideClosure(stateEngine, referencerSchema.getName(), referencedSchema.getName(), matches, ADD_REFERENCING_OUTSIDE_CLOSURE);
            }
        }
    }
    
    private static void addTransitiveMatches(HollowReadStateEngine stateEngine, String type, Map<String, BitSet> matches) {
        HollowTypeReadState typeState = stateEngine.getTypeState(type);

        switch(typeState.getSchema().getSchemaType()) {
        case OBJECT:
            addTransitiveMatches(stateEngine, (HollowObjectTypeReadState)typeState, matches);
            break;
        case LIST:
        case SET:
            addTransitiveMatches(stateEngine, (HollowCollectionTypeReadState)typeState, matches);
            break;
        case MAP:
            addTransitiveMatches(stateEngine, (HollowMapTypeReadState)typeState, matches);
            break;
        }
    }

    private static void addTransitiveMatches(HollowReadStateEngine stateEngine, HollowObjectTypeReadState typeState, Map<String, BitSet> matches) {
        HollowObjectSchema schema = typeState.getSchema();
        BitSet matchingOrdinals = getOrCreateBitSet(matches, schema.getName(), typeState.maxOrdinal());

        BitSet childOrdinals[] = new BitSet[schema.numFields()];

        for(int i=0;i<schema.numFields();i++) {
            if(schema.getFieldType(i) == FieldType.REFERENCE) {
                HollowTypeReadState childTypeState = stateEngine.getTypeState(schema.getReferencedType(i));
                if(childTypeState != null && childTypeState.maxOrdinal() >= 0)
                    childOrdinals[i] = getOrCreateBitSet(matches, schema.getReferencedType(i), childTypeState.maxOrdinal());
            }
        }

        int ordinal = matchingOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            for(int i=0;i<childOrdinals.length;i++) {
                if(childOrdinals[i] != null) {
                    int childOrdinal = typeState.readOrdinal(ordinal, i);
                    if(childOrdinal != -1) {
                        childOrdinals[i].set(childOrdinal);
                    }
                }
            }
            ordinal = matchingOrdinals.nextSetBit(ordinal + 1);
        }
    }

    private static void addTransitiveMatches(HollowReadStateEngine stateEngine, HollowCollectionTypeReadState typeState, Map<String, BitSet> matches) {
        HollowCollectionSchema schema = typeState.getSchema();
        BitSet matchingOrdinals = getOrCreateBitSet(matches, schema.getName(), typeState.maxOrdinal());

        HollowTypeReadState childTypeState = stateEngine.getTypeState(schema.getElementType());
        if(childTypeState != null && childTypeState.maxOrdinal() >= 0) {
            BitSet childOrdinals = getOrCreateBitSet(matches, schema.getElementType(), childTypeState.maxOrdinal());
    
            int ordinal = matchingOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                try {
                    HollowOrdinalIterator iter = typeState.ordinalIterator(ordinal);
                    int elementOrdinal = iter.next();
                    while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                        childOrdinals.set(elementOrdinal);
                        elementOrdinal = iter.next();
                    }
                } catch(Exception e) {
                    log.log(Level.SEVERE, "Add transitive matches failed", e);
                }
    
                ordinal = matchingOrdinals.nextSetBit(ordinal + 1);
            }
    
            if(!childOrdinals.isEmpty()) {
                matches.put(schema.getElementType(), childOrdinals);
            }
        }
    }

    private static void addTransitiveMatches(HollowReadStateEngine stateEngine, HollowMapTypeReadState typeState, Map<String, BitSet> matches) {
        HollowMapSchema schema = typeState.getSchema();
        BitSet matchingOrdinals = getOrCreateBitSet(matches, schema.getName(), typeState.maxOrdinal());

        HollowTypeReadState keyTypeState = stateEngine.getTypeState(schema.getKeyType());
        HollowTypeReadState valueTypeState = stateEngine.getTypeState(schema.getValueType());

        BitSet keyOrdinals = keyTypeState == null || keyTypeState.maxOrdinal() < 0 ? null : getOrCreateBitSet(matches, schema.getKeyType(), keyTypeState.maxOrdinal());
        BitSet valueOrdinals = valueTypeState == null || valueTypeState.maxOrdinal() < 0 ? null : getOrCreateBitSet(matches, schema.getValueType(), valueTypeState.maxOrdinal());

        int ordinal = matchingOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(ordinal);
            while(iter.next()) {
                if(keyOrdinals != null)
                    keyOrdinals.set(iter.getKey());
                if(valueOrdinals != null)
                    valueOrdinals.set(iter.getValue());
            }

            ordinal = matchingOrdinals.nextSetBit(ordinal + 1);
        }
    }

    private static void traverseReferencesOutsideClosure(HollowReadStateEngine stateEngine, String referencerType, String referencedType, Map<String, BitSet> matches, TransitiveSetTraverserAction action) {
        HollowTypeReadState referencerTypeState = stateEngine.getTypeState(referencerType);

        switch(referencerTypeState.getSchema().getSchemaType()) {
        case OBJECT:
            traverseReferencesOutsideClosure(stateEngine, (HollowObjectTypeReadState)referencerTypeState, referencedType, matches, action);
            break;
        case LIST:
        case SET:
            traverseReferencesOutsideClosure(stateEngine, (HollowCollectionTypeReadState)referencerTypeState, referencedType, matches, action);
            break;
        case MAP:
            traverseReferencesOutsideClosure(stateEngine, (HollowMapTypeReadState)referencerTypeState, referencedType, matches, action);
            break;
        }
    }

    private static void traverseReferencesOutsideClosure(HollowReadStateEngine stateEngine, HollowObjectTypeReadState referencerTypeState, String referencedType, Map<String, BitSet> closureMatches, TransitiveSetTraverserAction action) {
        HollowObjectSchema schema = referencerTypeState.getSchema();
        BitSet referencedClosureMatches = getOrCreateBitSet(closureMatches, referencedType, stateEngine.getTypeState(referencedType).maxOrdinal());
        BitSet referencerClosureMatches = getOrCreateBitSet(closureMatches, schema.getName(), referencerTypeState.maxOrdinal());

        for(int i=0;i<schema.numFields();i++) {
            if(schema.getFieldType(i) == FieldType.REFERENCE && referencedType.equals(schema.getReferencedType(i))) {
                BitSet allReferencerOrdinals = getPopulatedOrdinals(referencerTypeState);

                int ordinal = allReferencerOrdinals.nextSetBit(0);
                while(ordinal != -1) {
                    if(!referencerClosureMatches.get(ordinal)) {
                        int refOrdinal = referencerTypeState.readOrdinal(ordinal, i);
                        if(refOrdinal != -1) {
                            if(referencedClosureMatches.get(refOrdinal)) {
                                action.foundReference(referencerClosureMatches, ordinal, referencedClosureMatches, refOrdinal);
                            }
                        }
                    }

                    ordinal = allReferencerOrdinals.nextSetBit(ordinal + 1);
                }
            }
        }
    }

    private static void traverseReferencesOutsideClosure(HollowReadStateEngine stateEngine, HollowCollectionTypeReadState referencerTypeState, String referencedType, Map<String, BitSet> closureMatches, TransitiveSetTraverserAction action) {
        HollowCollectionSchema schema = referencerTypeState.getSchema();

        if(!referencedType.equals(schema.getElementType()))
            return;

        BitSet referencedClosureMatches = getOrCreateBitSet(closureMatches, referencedType, stateEngine.getTypeState(referencedType).maxOrdinal());
        BitSet referencerClosureMatches = getOrCreateBitSet(closureMatches, schema.getName(), referencerTypeState.maxOrdinal());

        BitSet allReferencerOrdinals = getPopulatedOrdinals(referencerTypeState);

        int ordinal = allReferencerOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            if(!referencerClosureMatches.get(ordinal)) {
                HollowOrdinalIterator iter = referencerTypeState.ordinalIterator(ordinal);
                int refOrdinal = iter.next();
                while(refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    if(referencedClosureMatches.get(refOrdinal)) {
                        action.foundReference(referencerClosureMatches, ordinal, referencedClosureMatches, refOrdinal);
                    }
                    refOrdinal = iter.next();
                }
            }

            ordinal = allReferencerOrdinals.nextSetBit(ordinal + 1);
        }
    }

    private static void traverseReferencesOutsideClosure(HollowReadStateEngine stateEngine, HollowMapTypeReadState referencerTypeState, String referencedType, Map<String, BitSet> closureMatches, TransitiveSetTraverserAction action) {
        HollowMapSchema schema = referencerTypeState.getSchema();

        BitSet referencedClosureMatches = getOrCreateBitSet(closureMatches, referencedType, stateEngine.getTypeState(referencedType).maxOrdinal());
        BitSet referencerClosureMatches = getOrCreateBitSet(closureMatches, schema.getName(), referencerTypeState.maxOrdinal());

        BitSet allReferencerOrdinals = getPopulatedOrdinals(referencerTypeState);

        boolean keyTypeMatches = referencedType.equals(schema.getKeyType());
        boolean valueTypeMatches = referencedType.equals(schema.getValueType());

        if(keyTypeMatches || valueTypeMatches) {
            int ordinal = allReferencerOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                if(!referencerClosureMatches.get(ordinal)) {
                    HollowMapEntryOrdinalIterator iter = referencerTypeState.ordinalIterator(ordinal);
                    while(iter.next()) {
                        if(keyTypeMatches) {
                            int refOrdinal = iter.getKey();
                            if(referencedClosureMatches.get(refOrdinal)) {
                                action.foundReference(referencerClosureMatches, ordinal, referencedClosureMatches, refOrdinal);
                            }
                        }
                        if(valueTypeMatches) {
                            int refOrdinal = iter.getValue();
                            if(referencedClosureMatches.get(refOrdinal)) {
                                action.foundReference(referencerClosureMatches, ordinal, referencedClosureMatches, refOrdinal);
                            }
                        }
                    }
                }

                ordinal = allReferencerOrdinals.nextSetBit(ordinal + 1);
            }
        }
    }

    private static BitSet getPopulatedOrdinals(HollowTypeReadState typeState) {
        return typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

    private static BitSet getOrCreateBitSet(Map<String, BitSet> bitSets, String typeName, int numBitsRequired) {
        if(numBitsRequired < 0)
            numBitsRequired = 0;

        BitSet bs = bitSets.get(typeName);
        if(bs == null) {
            bs = new BitSet(numBitsRequired);
            bitSets.put(typeName, bs);
        }
        return bs;
    }
    
    public static interface TransitiveSetTraverserAction {
        
        public void foundReference(BitSet referencerClosureMatches, int referencerOrdinal, BitSet referencedClosureMatches, int referencedOrdinal);
        
        public static final TransitiveSetTraverserAction REMOVE_REFERENCED_OUTSIDE_CLOSURE = new TransitiveSetTraverserAction() {
            @Override
            public void foundReference(BitSet referencerClosureMatches, int referencerOrdinal, BitSet referencedClosureMatches, int referencedOrdinal) {
                referencedClosureMatches.clear(referencedOrdinal);
            }
        };
        
        public static final TransitiveSetTraverserAction ADD_REFERENCING_OUTSIDE_CLOSURE = new TransitiveSetTraverserAction() {
            @Override
            public void foundReference(BitSet referencerClosureMatches, int referencerOrdinal, BitSet referencedClosureMatches, int referencedOrdinal) {
                referencerClosureMatches.set(referencerOrdinal);
            }
        };
        
    }

}
