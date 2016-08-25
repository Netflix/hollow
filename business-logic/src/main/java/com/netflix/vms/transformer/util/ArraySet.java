package com.netflix.vms.transformer.util;

import java.util.ArrayList;

import java.util.List;
import java.util.Iterator;
import java.util.AbstractSet;

public class ArraySet<T> extends AbstractSet<T> {

    private final List<T> elements;
    
    public ArraySet() {
        this.elements = new ArrayList<>();
    }
    
    @Override
    public boolean add(T e) {
        return elements.add(e);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

}
