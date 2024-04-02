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
package com.netflix.hollow.core.index;

import static java.util.stream.Collectors.joining;

import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Functionality for processing field paths.
 */
public final class FieldPaths {

    private static final Logger LOG = Logger.getLogger(FieldPaths.class.getName());

    /**
     * Creates an object-based field path given a data set and the field path in symbolic form conforming to paths
     * associated with a primary key.
     *
     * @param dataset the data set
     * @param type the type name from which to bind the field path
     * @param path the symbolic field path
     * @return the field path
     * @throws IllegalArgumentException if the symbolic field path is ill-formed and cannot be bound
     */
    public static FieldPath<ObjectFieldSegment> createFieldPathForPrimaryKey(
            HollowDataset dataset, String type, String path) {
        boolean autoExpand = !path.endsWith("!");
        path = autoExpand ? path : path.substring(0, path.length() - 1);

        FieldPath<FieldSegment> fp = createFieldPath(dataset, type, path, autoExpand, false, false);

        // Erasure trick to avoid copying when it is known the list only contains
        // instances of ObjectFieldSegment
        assert fp.segments.stream().allMatch(o -> o instanceof ObjectFieldSegment);
        @SuppressWarnings( {"unchecked", "raw"})
        FieldPath<ObjectFieldSegment> result = (FieldPath<ObjectFieldSegment>) (FieldPath) fp;
        return result;
    }

    /**
     * Creates a field path given a data set and the field path in symbolic form conforming to paths
     * associated with a hash index.
     *
     * @param dataset the data set
     * @param type the type name from which to bind the field path
     * @param path the symbolic field path
     * @return the field path
     * @throws IllegalArgumentException if the symbolic field path is ill-formed and cannot be bound
     */
    public static FieldPath<FieldSegment> createFieldPathForHashIndex(HollowDataset dataset, String type, String path) {
        return createFieldPath(dataset, type, path, false, false, true);
    }

    /**
     * Creates a field path given a data set and the field path in symbolic form conforming to paths
     * associated with a prefix index.
     *
     * @param dataset the data set
     * @param type the type name from which to bind the field path
     * @param path the symbolic field path
     * @param autoExpand {@code true} if the field path should be expanded (if needed) to a full path referencing
     * an object field whose type is a non-reference type, otherwise the field path is not expanded and must be
     * a full path
     * @return the field path
     * @throws IllegalArgumentException if the symbolic field path is ill-formed and cannot be bound
     */
    public static FieldPath<FieldSegment> createFieldPathForPrefixIndex(
            HollowDataset dataset, String type, String path, boolean autoExpand) {
        // If autoExpand is false then requireFullPath must be true
        boolean requireFullPath = !autoExpand;
        return createFieldPath(dataset, type, path, autoExpand, requireFullPath, true);
    }

    /**
     * Creates a field path given a data set and the field path in symbolic form.
     *
     * @param dataset the data set
     * @param type the type name from which to bind the field path
     * @param path the symbolic field path
     * @param autoExpand {@code true} if the field path should be expanded (if needed) to a full path referencing
     * an object field whose type is a non-reference type, otherwise the field path is not expanded
     * @param requireFullPath {@code true} if a full path is required when {@code autoExpand} is {@code false}.
     * Ignored if {@code autoExpand} is {@code true}.
     * @param traverseSequences {@code true} if lists, sets and maps are traversed, otherwise an
     * {@code IllegalArgumentException} will be thrown
     * @return the field path
     * @throws IllegalArgumentException if the symbolic field path is ill-formed and cannot be bound
     */
    static FieldPath<FieldSegment> createFieldPath(
            HollowDataset dataset, String type, String path,
            boolean autoExpand, boolean requireFullPath, boolean traverseSequences) {
        Objects.requireNonNull(dataset);
        Objects.requireNonNull(type);
        Objects.requireNonNull(path);

        String[] segments = path.isEmpty() ? new String[0] : path.split("\\.");
        List<FieldSegment> fieldSegments = new ArrayList<>();

        String segmentType = type;
        for (int i = 0; i < segments.length; i++) {
            HollowSchema schema = dataset.getSchema(segmentType);
            // @@@ Can this only occur for anything other than the root `type`?
            if (schema == null) {
                LOG.log(Level.WARNING, FieldPathException.message(FieldPathException.ErrorKind.NOT_BINDABLE, dataset,
                        type, segments, fieldSegments, null, i));
                throw new FieldPathException(FieldPathException.ErrorKind.NOT_BINDABLE, dataset, type, segments, fieldSegments, null, i);
            }

            String segment = segments[i];
            HollowSchema.SchemaType schemaType = schema.getSchemaType();
            if (schemaType == HollowSchema.SchemaType.OBJECT) {
                HollowObjectSchema objectSchema = (HollowObjectSchema) schema;

                int index = objectSchema.getPosition(segment);
                if (index == -1) {
                    throw new FieldPathException(FieldPathException.ErrorKind.NOT_FOUND, dataset, type, segments,
                            fieldSegments, schema, i);
                }

                segmentType = objectSchema.getReferencedType(index);
                fieldSegments.add(new ObjectFieldSegment(objectSchema, segment, segmentType, index));
            } else if (traverseSequences && (schemaType == HollowSchema.SchemaType.SET
                    || schemaType == HollowSchema.SchemaType.LIST)) {
                HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;

                if (!segment.equals("element")) {
                    throw new FieldPathException(FieldPathException.ErrorKind.NOT_FOUND, dataset, type, segments,
                            fieldSegments, schema, i);
                }

                segmentType = collectionSchema.getElementType();
                fieldSegments.add(new FieldSegment(collectionSchema, segment, segmentType));
            } else if (traverseSequences && schemaType == HollowSchema.SchemaType.MAP) {
                HollowMapSchema mapSchema = (HollowMapSchema) schema;

                if (segment.equals("key")) {
                    segmentType = mapSchema.getKeyType();
                } else if (segment.equals("value")) {
                    segmentType = mapSchema.getValueType();
                } else {
                    throw new FieldPathException(FieldPathException.ErrorKind.NOT_FOUND, dataset, type, segments,
                            fieldSegments, schema, i);
                }

                fieldSegments.add(new FieldSegment(mapSchema, segment, segmentType));
            } else if (!traverseSequences) {
                throw new FieldPathException(FieldPathException.ErrorKind.NOT_TRAVERSABLE, dataset, type, segments,
                        fieldSegments, schema, i);
            }

            if (i < segments.length - 1 && segmentType == null) {
                throw new FieldPathException(FieldPathException.ErrorKind.NOT_TRAVERSABLE, dataset, type, segments,
                        fieldSegments, schema, i);
            }
        }

        if (autoExpand) {
            while (segmentType != null) {
                HollowSchema schema = dataset.getSchema(segmentType);
                if (schema == null) {
                    LOG.log(Level.WARNING, FieldPathException.message(FieldPathException.ErrorKind.NOT_BINDABLE, dataset,
                            type, segments, fieldSegments, null, i));
                    throw new FieldPathException(FieldPathException.ErrorKind.NOT_BINDABLE, dataset, type, segments,
                            fieldSegments, schema);
                }

                if (schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
                    HollowObjectSchema objectSchema = (HollowObjectSchema) schema;

                    if (objectSchema.numFields() == 1) {
                        segmentType = objectSchema.getReferencedType(0);

                        fieldSegments.add(
                                new ObjectFieldSegment(objectSchema, objectSchema.getFieldName(0), segmentType,
                                        0));
                    } else if (objectSchema.getPrimaryKey() != null && objectSchema.getPrimaryKey().numFields() == 1) {
                        PrimaryKey key = objectSchema.getPrimaryKey();

                        FieldPath<ObjectFieldSegment> expandedFieldSegments;
                        try {
                            expandedFieldSegments =
                                    createFieldPathForPrimaryKey(dataset, key.getType(), key.getFieldPaths()[0]);
                        } catch (FieldPathException cause) {
                            FieldPathException e = new FieldPathException(FieldPathException.ErrorKind.NOT_EXPANDABLE,
                                    dataset, type, segments,
                                    fieldSegments, objectSchema);
                            e.initCause(cause);
                            throw e;
                        }

                        fieldSegments.addAll(expandedFieldSegments.segments);
                        break;
                    } else {
                        throw new FieldPathException(FieldPathException.ErrorKind.NOT_EXPANDABLE, dataset, type,
                                segments,
                                fieldSegments, objectSchema);
                    }
                } else {
                    throw new FieldPathException(FieldPathException.ErrorKind.NOT_EXPANDABLE, dataset, type, segments,
                            fieldSegments, schema);
                }
            }
        } else if (requireFullPath && segmentType != null) {
            throw new FieldPathException(FieldPathException.ErrorKind.NOT_FULL, dataset, type, segments,
                    fieldSegments);
        }

        return new FieldPath<>(type, fieldSegments, !autoExpand);
    }

    /**
     * An exception contain structured information when a field path cannot be bound.
     */
    public static final class FieldPathException extends IllegalArgumentException {
        public enum ErrorKind {
            NOT_BINDABLE,
            NOT_FOUND,
            NOT_FULL,
            NOT_TRAVERSABLE,
            NOT_EXPANDABLE,
            ;
        }

        public final ErrorKind error;
        final String rootType;
        final String[] segments;

        // Prior paths
        final List<FieldSegment> fieldSegments;

        // Partial state
        final HollowSchema enclosingSchema;
        final int segmentIndex;

        FieldPathException(
                ErrorKind error, HollowDataset dataset, String rootType, String[] segments,
                List<FieldSegment> fieldSegments) {
            this(error, dataset, rootType, segments, fieldSegments, null, segments.length);
        }

        FieldPathException(
                ErrorKind error, HollowDataset dataset, String rootType, String[] segments,
                List<FieldSegment> fieldSegments, HollowSchema enclosingSchema) {
            this(error, dataset, rootType, segments, fieldSegments, enclosingSchema, segments.length);
        }

        FieldPathException(
                ErrorKind error, HollowDataset dataset, String rootType, String[] segments,
                List<FieldSegment> fieldSegments, HollowSchema enclosingSchema, int segmentIndex) {
            super(message(error, dataset, rootType, segments, fieldSegments, enclosingSchema, segmentIndex));

            this.error = error;
            this.rootType = rootType;
            this.segments = segments;
            this.fieldSegments = Collections.unmodifiableList(fieldSegments);
            this.enclosingSchema = enclosingSchema;
            this.segmentIndex = segmentIndex;
        }

        static String message(
                ErrorKind error, HollowDataset dataset, String rootType, String[] segments,
                List<FieldSegment> fieldSegments, HollowSchema enclosingSchema, int segmentIndex) {
            switch (error) {
                case NOT_BINDABLE:
                    return String.format("Field path \"%s\" cannot be bound to data set %s. " +
                                    "A schema of type named \"%s\" cannot be found for the last segment of the path prefix \"%s\".",
                            toPathString(segments), dataset,
                            getLastTypeName(rootType, fieldSegments), toPathString(segments, segmentIndex + 1));
                case NOT_FOUND:
                    return String.format("Field path \"%s\" not found in data set %s. " +
                                    "A schema of type named \"%s\" does not contain a field for the last segment of the path prefix \"%s\".",
                            toPathString(segments), dataset,
                            enclosingSchema.getName(), toPathString(segments, segmentIndex + 1));
                case NOT_TRAVERSABLE: {
                    if (enclosingSchema.getSchemaType() != HollowSchema.SchemaType.OBJECT) {
                        return String.format("Field path \"%s\" is not traversable in data set %s. " +
                                        "A non-object schema of type named \"%s\" and of schema type %s cannot be traversed for the last segment of the path prefix \"%s\".",
                                toPathString(segments), dataset,
                                enclosingSchema.getName(), enclosingSchema.getSchemaType(),
                                toPathString(segments, segmentIndex + 1));
                    } else {
                        return String.format("Field path \"%s\" is not traversable in data set %s. " +
                                        "An object schema of type named \"%s\" cannot be traversed for the last segment of the path prefix \"%s\". "
                                        +
                                        "The last segment of the path prefix refers to a value (non-reference) field.",
                                toPathString(segments), dataset,
                                enclosingSchema.getName(),
                                toPathString(segments, segmentIndex + 1));
                    }
                }
                case NOT_FULL:
                    return String.format("Field path \"%s\" is not a full path in data set %s. " +
                                    "The last segment of the path is not a value (non-reference) field and refers to a reference field whose schema is of type named \"%s\"",
                            toPathString(segments), dataset,
                            fieldSegments.get(fieldSegments.size() - 1).getTypeName());
                case NOT_EXPANDABLE: {
                    if (enclosingSchema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
                        HollowObjectSchema objectSchema = (HollowObjectSchema) enclosingSchema;
                        if (objectSchema.numFields() != 1 || objectSchema.getPrimaryKey() == null
                                || objectSchema.getPrimaryKey().numFields() != 1) {
                            return String.format("Field path \"%s\" is not expandable in data set %s. " +
                                            "An object schema of type named \"%s\" cannot be traversed for the last segment of the partially expanded path \"%s\". "
                                            +
                                            "The schema contains more than one field, or has no primary key, or has a primary key with more than one field path.",
                                    toPathString(segments), dataset,
                                    enclosingSchema.getName(),
                                    toPathString(fieldSegments));
                        }
                    }
                    return String.format("Field path \"%s\" is not expandable in data set %s. " +
                                    "A non-object schema of type named \"%s\" and of schema type %s cannot be traversed for the last segment of the partially expanded path \"%s\".",
                            toPathString(segments), dataset,
                            enclosingSchema.getName(), enclosingSchema.getSchemaType(),
                            toPathString(fieldSegments));
                }
                default:
                    throw new InternalError("Cannot reach here");
            }
        }

        static String getLastTypeName(String rootType, List<FieldSegment> fieldSegments) {
            return fieldSegments.isEmpty()
                    ? rootType
                    : fieldSegments.get(fieldSegments.size() - 1).typeName;
        }

        static String toPathString(List<FieldSegment> segments) {
            return segments.stream().map(FieldSegment::getName).collect(joining("."));
        }

        static String toPathString(String[] segments) {
            return toPathString(segments, segments.length);
        }

        static String toPathString(String[] segments, int l) {
            return Arrays.stream(segments).limit(l).collect(joining("."));
        }
    }

    /**
     * A structured representation field path containing field segments.
     *
     * @param <T> the field segment type
     */
    public final static class FieldPath<T extends FieldSegment> {
        public static final FieldPath NOT_BOUND = new FieldPath("", new ArrayList(), true);
        final String rootType;
        final List<T> segments;
        final boolean noAutoExpand;

        FieldPath(String rootType, List<T> segments, boolean noAutoExpand) {
            this.rootType = rootType;
            this.segments = Collections.unmodifiableList(segments);
            this.noAutoExpand = noAutoExpand;
        }

        /**
         * Returns the root type from which this field path is bound.
         *
         * @return the root type
         */
        public String getRootType() {
            return rootType;
        }

        /**
         * Returns the field segments.
         *
         * @return the field segments.  The returned list is unmodifiable.
         */
        public List<T> getSegments() {
            return segments;
        }

        /**
         * Returns the field path in nominal form.
         *
         * @return the field path in nominal form
         */
        public String toString() {
            String path = segments.stream().map(FieldPaths.FieldSegment::getName)
                    .collect(joining("."));
            return noAutoExpand ? path + "!" : path;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FieldPath<?> fieldPath = (FieldPath<?>) o;
            return noAutoExpand == fieldPath.noAutoExpand &&
                    rootType.equals(fieldPath.rootType) &&
                    segments.equals(fieldPath.segments);
        }

        @Override public int hashCode() {
            return Objects.hash(rootType, segments, noAutoExpand);
        }
    }

    /**
     * A structured representation of field segment.
     */
    public static class FieldSegment {
        final HollowSchema enclosingSchema;
        final String name;
        final String typeName;

        FieldSegment(HollowSchema enclosingSchema, String name, String typeName) {
            this.name = name;
            this.typeName = typeName;
            this.enclosingSchema = enclosingSchema;
        }

        /**
         * Returns the enclosing schema that declares a field corresponding to this segment.
         *
         * @return the enclosing schema
         */
        public HollowSchema getEnclosingSchema() {
            return enclosingSchema;
        }

        /**
         * Returns the segment name.
         *
         * @return the segment name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the schema type name associated with this segment.
         *
         * @return the schema type name.
         */
        public String getTypeName() {
            return typeName;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FieldSegment that = (FieldSegment) o;
            return Objects.equals(enclosingSchema, that.enclosingSchema) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(typeName, that.typeName);
        }

        @Override public int hashCode() {
            return Objects.hash(enclosingSchema, name, typeName);
        }
    }

    /**
     * A structured representation of field segment corresponding to a field in an object schema.
     */
    public final static class ObjectFieldSegment extends FieldSegment {
        final int index;
        final HollowObjectSchema.FieldType type;

        ObjectFieldSegment(
                HollowObjectSchema enclosingSchema, String name, String typeName,
                int index) {
            super(enclosingSchema, name, typeName);
            this.index = index;
            this.type = enclosingSchema.getFieldType(index);
        }

        /**
         * {@inheritDoc}
         */
        public HollowObjectSchema getEnclosingSchema() {
            return (HollowObjectSchema) super.getEnclosingSchema();
        }

        /**
         * The field index in the enclosing object schema.
         *
         * @return the field index
         */
        public int getIndex() {
            return index;
        }

        /**
         * The field type of the field in the enclosing object schema.
         *
         * @return the field type
         */
        public HollowObjectSchema.FieldType getType() {
            return type;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ObjectFieldSegment that = (ObjectFieldSegment) o;
            return index == that.index &&
                    type == that.type;
        }

        @Override public int hashCode() {
            return Objects.hash(super.hashCode(), index, type);
        }
    }
}
