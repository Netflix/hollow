package com.netflix.hollow.core.write.objectmapper;

import java.util.Set;

/**
 * Sample type that represents a direct circular reference between 2 classes, with a Set containing the child.
 */
public class DirectSetCircularReference {

    private final String name;
    private final Set<DirectSetCircularReference> children;

    public DirectSetCircularReference(String name, Set<DirectSetCircularReference> children) {
        this.name = name;
        this.children = children;
    }
}
