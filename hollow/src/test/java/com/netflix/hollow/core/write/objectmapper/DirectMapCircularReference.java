package com.netflix.hollow.core.write.objectmapper;

import java.util.Map;

/**
 * Sample type that represents a direct circular reference between 2 classes, with a Map containing the child.
 */
public class DirectMapCircularReference {

    private final String name;
    private final Map<String, DirectMapCircularReference> children;

    public DirectMapCircularReference(String name, Map<String, DirectMapCircularReference> children) {
        this.name = name;
        this.children = children;
    }
}
