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

package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.github.terma.gigaspacesqlconsole.core.CopyRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public class CopierTest {

    private static GigaSpace sourceGigaSpace;
    private static GigaSpace targetGigaSpace;

    @BeforeClass
    public static void init() {
        sourceGigaSpace = GigaSpaceUtils.getGigaSpace("/./source");
        targetGigaSpace = GigaSpaceUtils.getGigaSpace("/./target");
    }

    @Test
    public void shouldCopyDocument() throws Exception {
        SpaceTypeDescriptor typeDescriptor = randomDescriptor();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument);

        Assert.assertThat(sourceGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(1));

        CopyRequest request = new CopyRequest();
        request.url = "/./source";
        request.sql = "copy " + typeDescriptor.getTypeName();
        request.targetUrl = "/./target";

        Copier.copy(request);

        Assert.assertThat(targetGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(1));
    }

    @Test
    public void shouldCopyDocumentFromOnly() throws Exception {
        final SpaceTypeDescriptor typeDescriptor = randomDescriptor();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument1 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument1);
        SpaceDocument spaceDocument2 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument2);
        SpaceDocument spaceDocument3 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument3);

        Assert.assertThat(sourceGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(3));

        CopyRequest request = new CopyRequest();
        request.url = "/./source";
        request.sql = "copy " + typeDescriptor.getTypeName() + " from 1 only 1";
        request.targetUrl = "/./target";

        Copier.copy(request);

        Assert.assertThat(targetGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(1));
    }

    @Test
    public void shouldCopyDocumentOnly() throws Exception {
        final SpaceTypeDescriptor typeDescriptor = randomDescriptor();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument1 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument1);
        SpaceDocument spaceDocument2 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument2);
        SpaceDocument spaceDocument3 = new SpaceDocument(typeDescriptor.getTypeName());
        sourceGigaSpace.write(spaceDocument3);

        Assert.assertThat(sourceGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(3));

        CopyRequest request = new CopyRequest();
        request.url = "/./source";
        request.sql = "copy " + typeDescriptor.getTypeName() + " only 1";
        request.targetUrl = "/./target";

        Copier.copy(request);

        Assert.assertThat(targetGigaSpace.count(new SpaceDocument(typeDescriptor.getTypeName())), equalTo(1));
    }

    private SpaceTypeDescriptor randomDescriptor() {
        return new SpaceTypeDescriptorBuilder("Object" + new Random().nextLong())
                .idProperty("autoID", true).create();
    }

    @Test
    public void shouldCopyWithWhere() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder("Moma")
                .idProperty("id", true).create();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument1 = new SpaceDocument("Moma");
        spaceDocument1.setProperty("a", 1);
        sourceGigaSpace.write(spaceDocument1);

        SpaceDocument spaceDocument2 = new SpaceDocument("Moma");
        spaceDocument2.setProperty("a", 2);
        sourceGigaSpace.write(spaceDocument2);

        SpaceDocument spaceDocument3 = new SpaceDocument("Moma");
        spaceDocument3.setProperty("a", 3);
        sourceGigaSpace.write(spaceDocument3);

        Assert.assertThat(sourceGigaSpace.count(new SpaceDocument("Moma")), equalTo(3));

        CopyRequest request = new CopyRequest();
        request.url = "/./source";
        request.sql = "copy Moma where a = 2";
        request.targetUrl = "/./target";

        Copier.copy(request);

        final SpaceDocument[] result = targetGigaSpace.readMultiple(new SpaceDocument("Moma"));
        Assert.assertThat(result.length, equalTo(1));
        Assert.assertThat(result[0].getProperty("a").toString(), equalTo("2"));
    }

    @Test
    public void shouldCopyAndReset() throws Exception {
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder("Vombat")
                .idProperty("id", true).create();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        SpaceDocument spaceDocument1 = new SpaceDocument("Vombat");
        sourceGigaSpace.write(spaceDocument1);

        Assert.assertThat(sourceGigaSpace.count(new SpaceDocument("Vombat")), equalTo(1));

        CopyRequest request = new CopyRequest();
        request.url = "/./source";
        request.sql = "copy Vombat reset id";
        request.targetUrl = "/./target";

        Copier.copy(request);

        final SpaceDocument[] result = targetGigaSpace.readMultiple(new SpaceDocument("Vombat"));
        Assert.assertThat(result.length, equalTo(1));
        Assert.assertThat(result[0].getProperty("id"), not(spaceDocument1.getProperty("id")));
    }

}
