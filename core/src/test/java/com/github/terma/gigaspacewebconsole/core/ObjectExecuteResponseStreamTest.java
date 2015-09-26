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

package com.github.terma.gigaspacewebconsole.core;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ObjectExecuteResponseStreamTest {

    @Test
    public void keepAllWrittenData() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeRow(Arrays.asList("a", "b"));
        responseStream.writeRow(Arrays.asList("c"));

        assertThat(responseStream.getData(),
                equalTo(
                        Arrays.asList(Arrays.asList("a", "b"),
                                Arrays.asList("c"))));
    }

    @Test
    public void keepHeader() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(Arrays.asList("c", "1"));

        assertThat(responseStream.getColumns(), equalTo(Arrays.asList("c", "1")));
    }

    @Test
    public void supportEquals() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(Arrays.asList("c", "1"));
        responseStream.writeRow(new ArrayList<String>());
        responseStream.writeRow(Arrays.asList("a", "b"));

        ObjectExecuteResponseStream equalsResponseStream = new ObjectExecuteResponseStream();
        equalsResponseStream.writeHeader(Arrays.asList("c", "1"));
        equalsResponseStream.writeRow(new ArrayList<String>());
        equalsResponseStream.writeRow(Arrays.asList("a", "b"));

        ObjectExecuteResponseStream anotherResponseStream = new ObjectExecuteResponseStream();
        anotherResponseStream.writeHeader(Arrays.asList("c", "1"));

        assertThat(responseStream, equalTo(equalsResponseStream));
        assertThat(responseStream, not(equalTo(anotherResponseStream)));
    }

    @Test
    public void supportHashCode() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(Arrays.asList("c", "1"));
        responseStream.writeRow(new ArrayList<String>());
        responseStream.writeRow(Arrays.asList("a", "b"));

        ObjectExecuteResponseStream equalResponseStream = new ObjectExecuteResponseStream();
        equalResponseStream.writeHeader(Arrays.asList("c", "1"));
        equalResponseStream.writeRow(new ArrayList<String>());
        equalResponseStream.writeRow(Arrays.asList("a", "b"));

        ObjectExecuteResponseStream anotherResponseStream = new ObjectExecuteResponseStream();
        anotherResponseStream.writeHeader(Arrays.asList("c", "1"));

        assertThat(responseStream.hashCode(), equalTo(equalResponseStream.hashCode()));
        assertThat(responseStream.hashCode(), not(equalTo(anotherResponseStream.hashCode())));
    }

    @Test
    public void niceToString() throws IOException {
        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        responseStream.writeHeader(Arrays.asList("c", "1"));
        responseStream.writeRow(new ArrayList<String>());
        responseStream.writeRow(Arrays.asList("a", "b"));

        assertThat(responseStream.toString(), equalTo("ObjectExecuteResponseStream{columns=[c, 1], data=[[], [a, b]]}"));
    }

}
