package com.netflix.hollow.core.read.filter;

import static com.netflix.hollow.core.read.filter.TypeActions.newTypeActions;
import static com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action.exclude;
import static com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action.excludeRecursive;
import static com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action.include;
import static com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action.includeRecursive;
import static com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action.next;
import static com.netflix.hollow.core.schema.HollowObjectSchema.FieldType.REFERENCE;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.netflix.hollow.core.read.filter.TypeFilter.Builder.Action;
import com.netflix.hollow.core.read.filter.TypeFilter.Builder.Rule;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType.UnrecognizedSchemaTypeException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>Filters types from a dataset by including or excluding types; and for object types, including or excluding
 * fields. For example:</p>
 *
 * <pre>{@code
 * newTypeFilter()
 *   .excludeAll()
 *   .includeRecursive("Alpha")
 *   .include("Beta")
 * }</pre>
 *
 * <p>See {@link TypeFilter.Builder} for more examples.</p>
 */
@com.netflix.hollow.PublicApi
public interface TypeFilter {
    /**
     * Creates a type filter that defaults to including all types as if by calling
     * {@code newTypeFilter().includeAll()}.
     *
     * @return the filter
     */
    public static TypeFilter.Builder newTypeFilter() {
        return new Builder().includeAll();
    }

    /**
     * Returns whether the specified type should be included.
     *
     * @param type the type to check
     * @return true if the type should be included, false otherwise
     */
    boolean includes(String type);

    /**
     * <p>Returns whether the specified field on the indicated type should be included. For non-{@code OBJECT} types
     * this method always returns false.</p>
     *
     * @param type the type to check
     * @param field the field to check
     * @return true if the field should be included, false otherwise
     * @see com.netflix.hollow.core.schema.HollowSchema.SchemaType
     */
    boolean includes(String type, String field);

    /**
     * Resolve this type filter against the provide schema. May return itself if already reasolved, otherwise
     * returns a new filter.
     *
     * @param schemas schemas to resolve against
     * @return a resolved type filter
     */
    default TypeFilter resolve(List<HollowSchema> schemas) {
        return this;
    }

    /**
     * <p>A builder for a {@code TypeFilter}. Inclusion and exclusion rules can be combined (later rules take
     * precedence over earlier ones). Examples:</p>
     *
     * <pre>{@code
     * // include everything
     * newTypeFilter()
     *
     * // exclude everything except Alpha
     * newTypeFilter()
     *   .excludeAll()
     *   .include("Alpha")
     *
     * // exclude everything except Alpha's 'a1' field
     * newTypeFilter()
     *   .excludeAll()
     *   .include("Alpha", "a1")
     *
     * // exclude Alpha
     * newTypeFilter()
     *   .exclude("Alpha")
     *
     * // exclude Alpha's 'a1' field
     * newTypeFilter()
     *   .exclude("Alpha", "a1")
     *
     * // exclude Alpha except for its 'a1' field
     * newTypeFilter()
     *   .exclude("Alpha")
     *   .include("Alpha", "a1")
     *
     * // exclude Alpha and all types reachable from Alpha
     * newTypeFilter().excludeRecursive("Alpha")
     *
     * // exclude Alpha's 'a1' field and all types reachable from it
     * newTypeFilter().excludeRecursive("Alpha", "a1")
     *
     * // Given: Omega reachable from Alpha
     * // Recursively exclude Alpha while retaining Omega and all types reachable from it
     * newTypeFilter()
     *   .excludeRecursive("Alpha")
     *   .includeRecursive("Omega")
     * }</pre>
     *
     * <p>Not thread safe.</p>
     */
    @com.netflix.hollow.PublicApi
    final class Builder {
        List<Rule> rules;

        private Builder() {
            rules = new ArrayList<>();
        }

        public Builder includeAll() {
            rules.clear();
            rules.add(INCLUDE_ALL);
            return this;
        }

        public Builder excludeAll() {
            rules.clear();
            rules.add(EXCLUDE_ALL);
            return this;
        }

        /**
         * <p>Include the specified type. Non-recursive.</p>
         *
         * @param type type to include
         * @return this builder
         * @see #includeRecursive(String)
         * @see #include(String, String)
         */
        public Builder include(String type) {
            requireNonNull(type, "type required");
            rules.add((t,f) -> type.equals(t) ? include : next);
            return this;
        }

        /**
         * <p>Include the specified type recursively.</p>
         *
         * <p>If {@code type} is an {@code OBJECT} the types referenced by each of its {@code REFERENCE} fields are included as
         * if by calling {@code #includeRecursive(referencedType)}.</p>
         *
         * @param type type to include
         * @return this builder
         */
        public Builder includeRecursive(String type) {
            requireNonNull(type, "type required");
            rules.add((t,f) -> type.equals(t) ? includeRecursive : next);
            return this;
        }

        /**
         * <p>Include the field on the specified type. Non-recursive. Has no effect on non-{@code OBJECT} types.</p>
         */
        @com.netflix.hollow.Internal
        public Builder include(String type, String field) {
            requireNonNull(type, "type required");
            requireNonNull(field, "field name required");
            rules.add((t,f) -> type.equals(t) && field.equals(f) ? include : next);
            return this;
        }

        /**
         * <p>Include the field on the specified type recursively. If {@code field} is reference to another type, that
         * type will be included as if calling {@code includeRecursive(referencedType)}. Has no effect on non-{@code OBJECT}
         * types.</p>
         */
        @com.netflix.hollow.Internal
        public Builder includeRecursive(String type, String field) {
            requireNonNull(type, "type required");
            requireNonNull(field, "field name required");
            rules.add((t,f) -> type.equals(t) && field.equals(f) ? includeRecursive : next);
            return this;
        }

        /**
         * <p>Exclude the specified type. Non-recursive.</p>
         *
         * @param type type to exclude
         * @return this builder
         * @see #excludeRecursive(String)
         * @see #exclude(String, String)
         */
        public Builder exclude(String type) {
            requireNonNull(type, "type required");
            rules.add((t,f) -> type.equals(t) ? exclude : next);
            return this;
        }

        /**
         * <p>Exclude the specified type recursively.</p>
         *
         * <p>If {@code type} is an {@code OBJECT} the types referenced by each of its {@code REFERENCE} fields are excluded
         * as if by calling {@code #excludeRecursive(referencedType)}.</p>
         *
         * @param type type to exclude
         * @return this builder
         */
        public Builder excludeRecursive(String type) {
            requireNonNull(type, "type required");
            rules.add((t,f) -> type.equals(t) ? excludeRecursive : next);
            return this;
        }

        /**
         * <p>Exclude the field on the specified type. Non-recursive. Has no effect on non-{@code OBJECT} types.</p>
         */
        public Builder exclude(String type, String field) {
            requireNonNull(type, "type required");
            requireNonNull(field, "field name required");
            rules.add((t,f) -> type.equals(t) && field.equals(f) ? exclude : next);
            return this;
        }

        /**
         * <p>Exclude the field on the specified type recursively. If {@code field} is reference to another type, that
         * type will be excluded as if calling {@code excludeRecursive(referencedType)}. Has no effect on
         * non-{@code OBJECT} types.</p>
         */
        public Builder excludeRecursive(String type, String field) {
            requireNonNull(type, "type required");
            requireNonNull(field, "field name required");
            rules.add((t,f) -> type.equals(t) && field.equals(f) ? excludeRecursive : next);
            return this;
        }

        public TypeFilter resolve(List<HollowSchema> schemas) {
            return new Resolver(rules, schemas).resolve();
        }

        public TypeFilter build() {
            return new UnresolvedTypeFilter(rules);
        }

        @FunctionalInterface
        @com.netflix.hollow.Internal
        interface Rule extends BiFunction<String,String, Action> {
            @Override
            Action apply(String type, String field);
        }

        private static final Rule INCLUDE_ALL = (type, field) -> include;
        private static final Rule EXCLUDE_ALL = (type, field) -> exclude;

        /**
         * Actions on a target (a type or a type's field).
         */
        @com.netflix.hollow.Internal
        enum Action {
            /** move on to the next action */
            next(false, false),

            /** include the target (non-recursive) */
            include(true, false),

            /** include the target and its descendants */
            includeRecursive(true, true),

            /** exclude the target (non-recursive) */
            exclude(false, false),

            /** exclude the target and its descendants */
            excludeRecursive(false, true);

            /** {@code true} if target should be included, {@code false} otherwise */
            final boolean included;

            /** {@code true} if action applies to target's descendants, {@code false} otherwise. */
            final boolean recursive;

            Action(boolean included, boolean recursive) {
                this.included = included;
                this.recursive = recursive;
            }
        }
    }
}

/**
 * A filter with its rules resolved against a dataset's schemas.
 */
@com.netflix.hollow.Internal
class ResolvedTypeFilter implements TypeFilter {
    private final Map<String, TypeActions> actionsMap;

    ResolvedTypeFilter(Map<String, TypeActions> actionsMap) {
        this.actionsMap = unmodifiableMap(new LinkedHashMap<>(actionsMap));
    }

    @Override
    public boolean includes(String type) {
        requireNonNull(type, "type name required");
        TypeActions ta = actionsMap.get(type);
        return ta != null && ta.actions().values().stream().anyMatch(action -> action.included);
    }

    @Override
    public boolean includes(String type, String field) {
        requireNonNull(type, "type name required");
        requireNonNull(field, "field name required");
        TypeActions ta = actionsMap.get(type);
        return ta != null && (ta.action().included || ta.action(field).included);
    }
}

/**
 * <p>A filter that needs to be resolved against a dataset's schemas before it can be used for filtering.</p>
 *
 * <p>Recursive actions require the schema to be resolved. This class retains intent until resolution. For
 * comparison, defining recursive actions using {@link HollowFilterConfig} with
 * {@link com.netflix.hollow.api.consumer.HollowConsumer.Builder} has a chicken-and-egg problem: the builder requires
 * the filter config up front and the filter requires a full schema in order to express recursive actions, but callers
 * typically don't have the schema until after a consumer has loaded a snapshot.</p>
 *
 * <p>All filtering methods in this class throw {@code IllegalStateException}. Call {@link TypeFilter#resolve(List)}
 * and use the returned filter instead.</p>
 *
 */
@com.netflix.hollow.Internal
class UnresolvedTypeFilter implements TypeFilter {
    private final List<Rule> rules;

    UnresolvedTypeFilter(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean includes(String type) {
        requireNonNull(type);
        throw new IllegalStateException("unresolved type filter");
    }

    @Override
    public boolean includes(String type, String field) {
        requireNonNull(type);
        throw new IllegalStateException("unresolved type filter");
    }

    @Override
    public TypeFilter resolve(List<HollowSchema> schemas) {
        return new Resolver(rules, schemas).resolve();
    }
}

@com.netflix.hollow.Internal
final class Resolver {
    private final Map<String,HollowSchema> schemas;
    private final List<Rule> rules;

    Resolver(List<Rule> rules, List<HollowSchema> schemas) {
        assert !rules.isEmpty();
        this.rules = rules;
        this.schemas = schemas.stream().collect(toMap(HollowSchema::getName, identity()));
    }

    TypeFilter resolve() {
        Map<String, TypeActions> resolved = rules
                .stream()
                .flatMap(rule -> schemas
                        .values()
                        .stream()
                        .flatMap(schema -> descendants(rule, schema)))
                .filter(ta -> ta.actions().values().stream().anyMatch(a -> a != next))
                .collect(toMap(TypeActions::type, identity(), TypeActions::merge));

        return new ResolvedTypeFilter(resolved);
    }

    private Stream<TypeActions> descendants(Rule rule, HollowSchema schema) {
        String type = schema.getName();
        Action action = rule.apply(type, null);
        TypeActions parent = TypeActions.newTypeActions(type, action);

        switch (schema.getSchemaType()) {
            case OBJECT:
                HollowObjectSchema os = (HollowObjectSchema) schema;
                return IntStream
                        .range(0, os.numFields())
                        .boxed()
                        .flatMap(i -> {
                            String field = os.getFieldName(i);
                            Action fa = rule.apply(type, field);
                            if (fa == next) return Stream.empty();
                            TypeActions child = newTypeActions(type, field, fa);
                            Action descendantAction = fa.recursive ? fa : action;
                            if (descendantAction.recursive && os.getFieldType(i) == REFERENCE) {
                                String refType = os.getReferencedType(i);
                                HollowSchema refSchema = schemas.get(refType);
                                assert refSchema != null;
                                Stream<TypeActions> descendants = descendants((t,f) -> descendantAction, refSchema);
                                return Stream.concat(Stream.of(parent, child), descendants);
                            } else {
                                return Stream.of(parent, child);
                            }
                        });

            case SET:
            case LIST:
                if (action == next) {
                    return Stream.empty();
                } else if (action.recursive) {
                    HollowCollectionSchema cs = (HollowCollectionSchema) schema;

                    HollowSchema elemSchema = schemas.get(cs.getElementType());
                    assert elemSchema != null;

                    Stream<TypeActions> descendants = descendants((t, f) -> action, elemSchema);
                    return Stream.concat(Stream.of(parent), descendants);
                } else {
                    return Stream.of(parent);
                }

            case MAP:
                if (action == next) {
                    return Stream.empty();
                } else if (action.recursive) {
                    HollowMapSchema ms = (HollowMapSchema) schema;
                    HollowSchema kSchema = schemas.get(ms.getKeyType());
                    HollowSchema vSchema = schemas.get(ms.getValueType());
                    Stream<TypeActions> descendants = Stream.concat(
                            descendants((t, f) -> action, kSchema),
                            descendants((t1, f1) -> action, vSchema));
                    return Stream.concat(Stream.of(parent), descendants);
                } else {
                    return Stream.of(parent);
                }

            default:
                throw new UnrecognizedSchemaTypeException(type, schema.getSchemaType());
        }
    }
}

@com.netflix.hollow.Internal
class TypeActions {
    private static final String ALL = new String("*"); // avoid interning
    static TypeActions newTypeActions(String type, Action action) {
        return new TypeActions(type, singletonMap(ALL, action));
    }

    static TypeActions newTypeActions(String type, String field, Action action) {
        return new TypeActions(type, singletonMap(field, action));
    }

    String type() {
        return type;
    }

    private final String type;
    private final Map<String, Action> actions;

    private TypeActions(String type, Map<String, Action> actions) {
        this.type = type;
        this.actions = actions;
    }

    Map<String, Action> actions() {
        return actions;
    }

    Action action() {
        return actions.getOrDefault(ALL, next);
    }

    Action action(String field) {
        return actions.getOrDefault(field, next);
    }

    TypeActions merge(TypeActions other) {
        Map<String, Action> m = new LinkedHashMap<>();

        m.putAll(actions);
        m.putAll(other.actions);

        Action all = m.get(ALL);
        m = m.entrySet().stream()
             .filter(entry -> entry.getKey() == ALL || entry.getValue() != all)
             .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new TypeActions(type, m);
    }

    @Override
    public String toString() {
        return type + actions;
    }
}
