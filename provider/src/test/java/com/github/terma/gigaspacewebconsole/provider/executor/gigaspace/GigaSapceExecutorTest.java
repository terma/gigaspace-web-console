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

package com.github.terma.gigaspacewebconsole.provider.executor.gigaspace;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ObjectExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.core.config.ConfigFactory;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import static org.hamcrest.CoreMatchers.nullValue;

public class GigaSapceExecutorTest {

    private static GigaSpace gigaSpace;

    @BeforeClass
    public static void init() {
        System.setProperty(ConfigFactory.CONFIG_PATH_SYSTEM_PROPERTY, ConfigFactory.NONE);
        gigaSpace = GigaSpaceUtils.getGigaSpace("/./executor");
    }

    @Test
    public void should() throws Exception {
        final String typeName = "ObjectA";
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument = new SpaceDocument(typeName);
        spaceDocument.setProperty("field1", null);
        gigaSpace.write(spaceDocument);

        ExecuteRequest request = new ExecuteRequest();
        request.url = "/./executor";
        request.sql = "select * from " + typeName;

        GigaSpaceExecutor.INSTANCE.execute(request, new ObjectExecuteResponseStream());

        Assert.assertThat(readByType(typeName).getProperty("field1"), nullValue());
    }

    private SpaceDocument readByType(final String typeName) {
        return GigaSpaceUtils.readByType(gigaSpace, typeName);
    }

}
