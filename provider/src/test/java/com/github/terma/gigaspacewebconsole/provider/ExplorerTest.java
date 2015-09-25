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

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacewebconsole.core.ExploreRequest;
import com.github.terma.gigaspacewebconsole.core.ExploreResponse;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ExplorerTest {

    @Before
    public void before() {
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./explorer");

        GigaSpaceUtils.registerType(gigaSpace, "test");

        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder("M")
                .idProperty("id").addFixedProperty("fixed1", String.class)
                .addFixedProperty("fixed2", Integer.class).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        gigaSpace.clear(null);
        SpaceDocument mSpaceDocument = new SpaceDocument("M");
        mSpaceDocument.setProperty("id", 12);
        mSpaceDocument.setProperty("dynamic1", true);
        mSpaceDocument.setProperty("dynamic2", "A");
        gigaSpace.write(mSpaceDocument);
    }

    @Test(expected = NullPointerException.class)
    public void throwExceptionIfNullRequest() throws Exception {
        Explorer.explore(null);
    }

    @Test
    public void getStructureWithTables() throws Exception {
        ExploreRequest request = new ExploreRequest();
        request.url = "/./explorer";
        ExploreResponse response = Explorer.explore(request);

        assertEquals(3, response.tables.size());
        assertEquals("java.lang.Object", response.tables.get(0).name);
        assertEquals("test", response.tables.get(1).name);
        assertEquals("M", response.tables.get(2).name);
    }

    @Test
    public void getStructureWithFixedAndDynamicColumns() throws Exception {
        ExploreRequest request = new ExploreRequest();
        request.url = "/./explorer";
        ExploreResponse response = Explorer.explore(request);
        assertThat(response.tables.get(2).columns, equalTo(Arrays.asList("fixed1", "fixed2", "id", "dynamic1", "dynamic2")));
    }

}
