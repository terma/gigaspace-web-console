/*
Copyright 2015-2016 Artem Stasiuk

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

import junit.framework.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class ArraySqlResultTest {

    @Test
    public void shouldNotSupportRowTypesAndGetEmpty() throws SQLException {
        ArraySqlResult arraySqlResult = new ArraySqlResult(
                "", Arrays.asList("a", "b"), Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4")));

        Assert.assertTrue(arraySqlResult.next());
        Assert.assertEquals(Collections.EMPTY_LIST, arraySqlResult.getRowTypes());
    }

}
