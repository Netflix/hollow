package com.netflix.hollow.core.write.objectmapper;

import java.util.Collections;
import java.util.List;

/**
 * Sample type that represents a direct circular reference between 2 classes.
 */
public class DirectCircularReference {

    private final String name;
    private final List<DirectCircularReference> children;

    public DirectCircularReference(String name) {
        this(name, Collections.<DirectCircularReference>emptyList());
    }
    public DirectCircularReference(String name, List<DirectCircularReference> children) {
        this.name = name;
        this.children = children;
    }
}
