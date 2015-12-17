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

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class CoreDtoTest {

    private final Class clazz;
    private final String toString;

    public CoreDtoTest(Class clazz, String toString) {
        this.clazz = clazz;
        this.toString = toString;
    }

    @Parameterized.Parameters
    public static List<Object[]> parms() {
        return Arrays.asList(
                new Object[]{ExploreTable.class, "ExploreTable {name: 'null', columns: null}"},
                new Object[]{AppVersionRequest.class, "AppVersionRequest {appVersion: 'null'}"},
                new Object[]{CopyRequest.class, "CopyRequest {url: 'null', user: 'null', password: **** , driver: 'null', sql: 'null', targetUser: 'null', targetPassword: '****', targetUrl: 'null'}"},
                new Object[]{CopyResponse.class, "CopyResponse {count: 0}"},
                new Object[]{Count.class, "Count {name: 'null', count: 0}"},
                new Object[]{CountsResponse.class, "CountsResponse {counts: null}"},
                new Object[]{ExecuteRequest.class, "ExecuteRequest {url: 'null', user: 'null', password: **** , driver: 'null', sql: 'null'}"},
                new Object[]{ExploreRequest.class, "ExploreRequest {url: 'null', user: 'null', password: **** , driver: 'null'}"},
                new Object[]{ExploreResponse.class, "ExploreResponse {tables: null}"},
                new Object[]{ExportRequest.class, "ExportRequest {url: 'null', user: 'null', password: **** , driver: 'null', types: null}"},
                new Object[]{ImportRequest.class, "ImportRequest {url: 'null', user: 'null', password: **** , driver: 'null', file: 'null'}"}
        );
    }

    @Test
    public void checkToString() throws Exception {
        Assert.assertEquals("Unexpected " + clazz.getSimpleName() + ".toString", toString, clazz.newInstance().toString());
    }

}
