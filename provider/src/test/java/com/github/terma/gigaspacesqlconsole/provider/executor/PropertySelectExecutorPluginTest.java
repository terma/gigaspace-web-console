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
import com.github.terma.gigaspacesqlconsole.core.config.Config;
import com.github.terma.gigaspacesqlconsole.provider.GigaSpaceUtils;
import com.j_spaces.core.client.SQLQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import static org.hamcrest.CoreMatchers.is;


/*
where property 'a' and c = 12
where property 'a' or c = 12
where c = 12 and property 'a'
where c = 12 or property 'a'
where property 'b' ->
 */
public class PropertySelectExecutorPluginTest {

    private static final String url = "/./property-select";
    private static final String typeName = "A";

    private final ExecuteRequest request = new ExecuteRequest();

    private static GigaSpace gigaSpace;

    @BeforeClass
    public static void init() {
        System.setProperty("gigaspaceSqlConsoleConfig", Config.NONE);
        gigaSpace = GigaSpaceUtils.getGigaSpace(url);
    }

    @Before
    public void before() {
        request.url = url;

        gigaSpace.clear(null);
    }

    @Test
    public void shouldNotHandleSelectWithoutPropertyCondition() throws Exception {
        ExecuteRequest request = new ExecuteRequest();
        request.sql = "select * from A where b = 12";

        boolean handled = new PropertySelectExecutorPlugin().execute(request, new ObjectExecuteResponseStream());

        Assert.assertThat(handled, CoreMatchers.is(false));
    }

    @Test
    public void shouldNotSelectIfNoRequiredProperty() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "R", true);

        request.sql = "select * from A where property 'B'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        boolean handled = new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(handled, is(true));
        Assert.assertThat(responseStream, is(new ObjectExecuteResponseStream()));
    }

    @Test
    public void shouldSelectIfHasProperty() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B1", true);
        writeDocument(gigaSpace, typeName, "B", true);
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "select * from A where property 'B'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(1));
    }

    @Test
    public void shouldUpdateIfHasProperty() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B1", true);
        writeDocument(gigaSpace, typeName, "B", true);
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "update A set C = true where property 'B'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);


        Assert.assertThat(countByProperty(typeName, "C", true), is(1));
    }

    @Test
    public void shouldDeleteIfHasProperty() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B1", true);
        writeDocument(gigaSpace, typeName, "B", true);
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "delete from A where property 'B'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(countByType(typeName), is(2));
    }

    @Test
    public void shouldSelectIfHasPropertyCombinedWithOtherConditions() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B1", true, "name", "big");
        writeDocument(gigaSpace, typeName, "B", true, "name", "small");
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "select * from A where property 'B' and name = 'small'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(1));
    }

    @Test
    public void shouldSelectIfHasPropertyCombinedByOrWithOtherConditions() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B1", true, "name", "big");
        writeDocument(gigaSpace, typeName, "B", true, "name", "small");
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "select * from A where property 'B' or (name = 'big' or name = 'rumba')";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(2));
    }

    @Test
    public void shouldSelectIfMultiProperties() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "non_B", true, "superman", "aga");
        writeDocument(gigaSpace, typeName, "B", true, "superman", "aga");
        writeDocument(gigaSpace, typeName, "B", true, "non_superman", "aga");

        request.sql = "select * from A where property 'B' and property 'superman'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(1));
    }

    @Test
    public void shouldSelectIfPartialPropertyMatching() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "non_B", 1);
        writeDocument(gigaSpace, typeName, "B", 1);
        writeDocument(gigaSpace, typeName, "B1", 1);
        writeDocument(gigaSpace, typeName, "B", null);

        request.sql = "select * from A where property 'B%'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(2));
    }

    @Test
    public void shouldSelectIfSuperPartialPropertyMatching() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        writeDocument(gigaSpace, typeName, "B", 1);
        writeDocument(gigaSpace, typeName, "AB", 1);
        writeDocument(gigaSpace, typeName, "ACB", 1);
        writeDocument(gigaSpace, typeName, "G1ACB111", 1);
        writeDocument(gigaSpace, typeName, "AB1222", 1);

        request.sql = "select * from A where property '%A%B%'";

        ObjectExecuteResponseStream responseStream = new ObjectExecuteResponseStream();
        new PropertySelectExecutorPlugin().execute(request, responseStream);

        Assert.assertThat(responseStream.getData().size(), is(4));
    }

    private int countByProperty(final String typeName, final String property, final Object value) {
        SQLQuery sqlQuery = new SQLQuery(typeName, property + " = ?");
        sqlQuery.setParameter(1, value);
        return gigaSpace.readMultiple(sqlQuery).length;
    }

    private int countByType(final String typeName) {
        SQLQuery sqlQuery = new SQLQuery(typeName, "");
        return gigaSpace.readMultiple(sqlQuery).length;
    }

    private static void writeDocument(GigaSpace gigaSpace, String typeName, String property, Object value) {
        SpaceDocument spaceDocument1 = new SpaceDocument(typeName);
        spaceDocument1.setProperty(property, value);
        gigaSpace.write(spaceDocument1);
    }

    private static void writeDocument(
            GigaSpace gigaSpace, String typeName,
            String property1, Object value1, String property2, Object value2) {
        SpaceDocument spaceDocument1 = new SpaceDocument(typeName);
        spaceDocument1.setProperty(property1, value1);
        spaceDocument1.setProperty(property2, value2);
        gigaSpace.write(spaceDocument1);
    }

}
