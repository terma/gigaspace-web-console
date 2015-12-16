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

import com.github.terma.gigaspacewebconsole.provider.groovy.ObjectGroovyExecuteResult;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ProviderDtoTest {

    private final Class clazz;
    private final String toString;

    public ProviderDtoTest(Class clazz, String toString) {
        this.clazz = clazz;
        this.toString = toString;
    }

    @Parameterized.Parameters
    public static List<Object[]> parms() {
        return Arrays.asList(
                new Object[]{AdminCacheItem.class, "AdminCacheItem {admin: null, lastUsage: 0}"},
                new Object[]{ObjectGroovyExecuteResult.class, "ObjectGroovyExecuteResult {header: 'null', columns: null, data: []}"},
                new Object[]{TypeDescriptor.class, "TypeDescriptor {typeName: 'null', spaceIdProperty: 'null', routingProperty: 'null'}"}
        );
    }

    @Test
    public void checkToString() throws Exception {
        Assert.assertEquals("Unexpected " + clazz.getSimpleName() + ".toString", toString, clazz.newInstance().toString());
    }

}
