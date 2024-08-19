package com.netflix.hollow.core.write.objectmapper.flatrecords.traversal;

import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordReader;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FlatRecordTraversalMapNode extends AbstractMap<FlatRecordTraversalNode, FlatRecordTraversalNode> implements FlatRecordTraversalNode {
    private FlatRecordReader reader;
    private IntList ordinalPositions;
    private HollowMapSchema schema;
    private int[] keyOrdinals;
    private int[] valueOrdinals;
    private Map<String, HollowObjectSchema> commonSchemaMap;

    @Override
    public void reposition(FlatRecordReader reader, IntList ordinalPositions, int ordinal) {
        this.reader = reader;
        this.ordinalPositions = ordinalPositions;

        reader.resetTo(ordinalPositions.get(ordinal));
        schema = (HollowMapSchema) reader.readSchema();

        int size = reader.readCollectionSize();
        keyOrdinals = new int[size];
        valueOrdinals = new int[size];
        int keyOrdinal = 0;
        for (int i = 0; i < size; i++) {
            keyOrdinal += reader.readOrdinal();
            keyOrdinals[i] = keyOrdinal;
            valueOrdinals[i] = reader.readOrdinal();
        }
    }

    @Override
    public void setCommonSchema(Map<String, HollowObjectSchema> commonSchema) {
        this.commonSchemaMap = commonSchema;
    }

    @Override
    public int hashCode() {
        int h = 0;
        Iterator<Entry<FlatRecordTraversalNode,FlatRecordTraversalNode>> i = entrySet().iterator();
        while (i.hasNext()) {
            Entry<FlatRecordTraversalNode, FlatRecordTraversalNode> e = i.next();
            FlatRecordTraversalNode key = e.getKey();
            FlatRecordTraversalNode value = e.getValue();
            if(commonSchemaMap.containsKey(key.getSchema().getName())) {
                key.setCommonSchema(commonSchemaMap);
                h += (key == null ? 0 : key.hashCode());
            }
            if(commonSchemaMap.containsKey(value.getSchema().getName())) {
                value.setCommonSchema(commonSchemaMap);
                h += (value == null ? 0 : value.hashCode());
            }
        }
        return h;
    }

    @Override
    public HollowMapSchema getSchema() {
        return schema;
    }

    @Override
    public Set<Entry<FlatRecordTraversalNode, FlatRecordTraversalNode>> entrySet() {
        return new AbstractSet<Entry<FlatRecordTraversalNode, FlatRecordTraversalNode>>() {
            @Override
            public Iterator<Entry<FlatRecordTraversalNode, FlatRecordTraversalNode>> iterator() {
                return new EntrySetIteratorImpl<>();
            }

            @Override
            public int size() {
                return keyOrdinals.length;
            }
        };
    }

    public <K extends FlatRecordTraversalNode, V extends FlatRecordTraversalNode> Iterator<Entry<K, V>> entrySetIterator() {
        return new EntrySetIteratorImpl<>();
    }

    private class EntrySetIteratorImpl<K extends FlatRecordTraversalNode, V extends FlatRecordTraversalNode> implements Iterator<Entry<K, V>> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < keyOrdinals.length;
        }

        @Override
        public Entry<K, V> next() {
            if (index >= keyOrdinals.length) {
                throw new IllegalStateException("No more elements");
            }

            int keyOrdinal = keyOrdinals[index];
            int valueOrdinal = valueOrdinals[index];
            index++;

            return new Entry<K, V>() {
                @Override
                public K getKey() {
                    if (keyOrdinal == -1) {
                        return null;
                    }
                    return (K) createAndRepositionNode(reader, ordinalPositions, keyOrdinal);
                }

                @Override
                public V getValue() {
                    if (valueOrdinal == -1) {
                        return null;
                    }
                    return (V) createAndRepositionNode(reader, ordinalPositions, valueOrdinal);
                }

                @Override
                public V setValue(V value) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    private static class MapEntry {
        private final int key;
        private final int value;

        public MapEntry(int key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof MapEntry)) return false;
            MapEntry other = (MapEntry) o;
            return key == other.key && value == other.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "MapEntry(" + key + ", " + value + ")";
        }
    }
}
