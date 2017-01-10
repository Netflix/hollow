package com.netflix.hollow.core.write.objectmapper;

import java.util.Collections;
import java.util.List;

/**
 * Sample type that represents a direct circular reference between 2 classes, with a List containing the child.
 */
public class DirectListCircularReference {

    private final String name;
    private final List<DirectListCircularReference> children;

    public DirectListCircularReference(String name, List<DirectListCircularReference> children) {
        this.name = name;
        this.children = children;
    }
}
