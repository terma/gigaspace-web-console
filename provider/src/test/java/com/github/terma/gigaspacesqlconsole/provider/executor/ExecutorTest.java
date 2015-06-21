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

package com.github.terma.gigaspacesqlconsole.provider.executor;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ObjectExecuteResponseStream;
import com.github.terma.gigaspacesqlconsole.provider.GigaSpaceUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;

public class ExecutorTest {

    private static final String url = "/./executor";

    private static GigaSpace gigaSpace;

    @BeforeClass
    public static void init() {
        gigaSpace = GigaSpaceUtils.getGigaSpace(url);
    }

    @Test
    public void shouldNotSelectIfNoRequiredProperty() throws Exception {
        final String typeName = "A";
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "R", true);
        writeDocument(gigaSpace, typeName, "R", null);
        writeDocument(gigaSpace, typeName, "R1", 90);

        ExecuteRequest request = new ExecuteRequest();
        request.url = url;
        request.sql = "select * from A where R is not null";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        Executor.execute(request, responseStream);

        Assert.assertThat(responseStream, is(new ObjectExecuteResponseStream()));
    }

    private SpaceDocument readByType(final String typeName) {
        return gigaSpace.read(new SpaceDocument(typeName));
    }

    private static void writeDocument(GigaSpace gigaSpace, String typeName, String property, Object value) {
        SpaceDocument spaceDocument1 = new SpaceDocument(typeName);
        spaceDocument1.setProperty(property, value);
        gigaSpace.write(spaceDocument1);
    }

}
