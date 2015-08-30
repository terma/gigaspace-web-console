package com.github.terma.gigaspacewebconsole.provider;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Copy of org.hamcrest.internal.ArrayIterator
 */
public class ArrayIterator implements Iterator<Object> {

    private final Object array;
    private int currentIndex = 0;

    public ArrayIterator(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("not an array");
        } else {
            this.array = array;
        }
    }

    public boolean hasNext() {
        return this.currentIndex < Array.getLength(this.array);
    }

    public Object next() {
        return Array.get(this.array, this.currentIndex++);
    }

    public void remove() {
        throw new UnsupportedOperationException("cannot remove items from an array");
    }
}