/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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
