package com.github.terma.gigaspacewebconsole.provider;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class ArrayIteratorTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfTryingToCreateWithNullArray() {
        new ArrayIterator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTryingToCreateWithNonArray() {
        new ArrayIterator(new Object());
    }

    @Test
    public void shouldIterateByEmptyArray() {
        Iterator iterator = new ArrayIterator(new Object[0]);
        Assert.assertThat(iterator.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void shouldThrowExceptionIfTryToGetNextWhenEol() {
        Iterator iterator = new ArrayIterator(new Object[] {1});
        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldIterateByArray() {
        Iterator iterator = new ArrayIterator(new Object[] {"1", "2"});
        Assert.assertThat(iterator.hasNext(), CoreMatchers.equalTo(true));
        Assert.assertThat(iterator.next(), CoreMatchers.equalTo((Object)"1"));
        Assert.assertThat(iterator.hasNext(), CoreMatchers.equalTo(true));
        Assert.assertThat(iterator.next(), CoreMatchers.equalTo((Object) "2"));
        Assert.assertThat(iterator.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionIfTryToRemoveAsNotSupported() {
        Iterator iterator = new ArrayIterator(new Object[] {"1", "2"});
        iterator.remove();
    }

}
