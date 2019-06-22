package com.netflix.vms.transformer.helper;

import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;

/**
 * Our version of a {@link com.netflix.hollow.test.HollowReadStateEngineBuilder}, which initializes
 * our state engine with all types.
 */
public class HollowReadStateEngineBuilder extends
        com.netflix.hollow.test.HollowReadStateEngineBuilder {
    private static final Set<Class<?>> TOP_LEVEL_TYPES = new Reflections(
            "com.netflix.vms.transformer.converterpojos").getSubTypesOf(Cloneable.class)
            .stream().map(t -> (Class<?>) t).collect(Collectors.toSet());

    public HollowReadStateEngineBuilder() {
        super(TOP_LEVEL_TYPES);
    }

}
