package com.netflix.hollow.core.write.objectmapper;


/**
 * Sample type that represents a direct circular reference between 2 classes.
 */
public class DirectCircularReference {

    private final String name;
    private final DirectCircularReference child;

    public DirectCircularReference(String name, DirectCircularReference child) {
        this.name = name;
        this.child = child;
    }
}
