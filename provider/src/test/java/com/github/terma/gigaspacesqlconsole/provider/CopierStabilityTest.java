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
import org.junit.Before;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CopierStabilityTest {

    private static final String TYPE = "ObjectA";
    private static final int COUNT = 10000;

    private GigaSpace sourceGigaSpace;
    private GigaSpace targetGigaSpace;

    @Before
    public void init() {
        sourceGigaSpace = GigaSpaceUtils.getGigaSpace("/./source" + System.currentTimeMillis());
        targetGigaSpace = GigaSpaceUtils.getGigaSpace("/./target" + System.currentTimeMillis());
    }

    @Test
    public void shouldCopyCorrectly() throws Exception {
        // given
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(TYPE)
                .idProperty("autoID", true).create();
        sourceGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
        targetGigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);

        for (int i = 0; i < COUNT; i++) {
            SpaceDocument spaceDocument = new SpaceDocument(TYPE);
            sourceGigaSpace.write(spaceDocument);
        }

        assertThat(sourceGigaSpace.count(new SpaceDocument(TYPE)), equalTo(COUNT));

        // when
        CopyRequest request = new CopyRequest();
        request.sql = "copy ObjectA";
        request.url = "/./" + sourceGigaSpace.getName();
        request.targetUrl = "/./" + targetGigaSpace.getName();

        Copier.copy(request);

        // then
        assertThat(targetGigaSpace.count(new SpaceDocument(TYPE)), equalTo(COUNT));
    }

}
