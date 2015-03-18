package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ObjectExecuteResponseStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public class ExecutorPluginUpdateTest {

    private static GigaSpace gigaSpace;

    @BeforeClass
    public static void init() {
        gigaSpace = GigaSpaceUtils.getGigaSpace("/./update");
    }

    @Test
    public void shouldUnsetFieldIfSetToNull() throws Exception {
        final String typeName = "ObjectA";
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument = new SpaceDocument(typeName);
        spaceDocument.setProperty("field1", "ffff");
        gigaSpace.write(spaceDocument);

        Assert.assertThat(readByType(typeName).getProperty("field1"), notNullValue());

        ExecuteRequest request = new ExecuteRequest();
        request.url = "/./update";
        request.sql = "update ObjectA set field1 = null";

        new ExecutorPluginUpdate().execute(request, new ObjectExecuteResponseStream());

        Assert.assertThat(readByType(typeName).getProperty("field1"), nullValue());
    }

    @Test
    public void shouldSetFieldWithWhereByIdProperty() throws Exception {
        final String typeName = "ObjectB";
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(typeName)
                .idProperty("autoID", true).create();
        gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument = new SpaceDocument(typeName);
        gigaSpace.write(spaceDocument);

        Assert.assertThat(readByType(typeName).getProperty("field1"), nullValue());

        ExecuteRequest request = new ExecuteRequest();
        request.url = "/./update";
        request.sql = "update ObjectB set field1 = 1 where autoID = '" + spaceDocument.getProperty("autoID") + "'";

        new ExecutorPluginUpdate().execute(request, new ObjectExecuteResponseStream());

        Assert.assertThat(readByType(typeName).getProperty("field1"), notNullValue());
    }

    private SpaceDocument readByType(final String typeName) {
        return gigaSpace.read(new SpaceDocument(typeName));
    }

}
