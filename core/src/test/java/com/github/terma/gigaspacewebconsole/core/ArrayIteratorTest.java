package com.github.terma.gigaspacewebconsole.core;

import org.junit.Test;

public class ArrayIteratorTest {

    @Test(expected = UnsupportedOperationException.class)
    public void doesntSupportRemoveAndThrowException() {
        ArrayIterator iterator = new ArrayIterator(new Object[]{"A", "B"});
        iterator.next();
        iterator.remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfInputParamNotArray() {
        new ArrayIterator("");
    }

}
