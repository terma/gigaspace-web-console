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

import com.github.terma.gigaspacewebconsole.provider.driver.DestroyableGigaSpace;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.junit.After;
import org.junit.Before;
import org.openspaces.core.GigaSpace;

@SuppressWarnings("WeakerAccess")
public abstract class TestWithGigaSpace {

    protected DestroyableGigaSpace destroyableGigaSpace;
    protected GigaSpace gigaSpace;
    protected String gigaSpaceUrl;

    @Before
    public void init() {
        destroyableGigaSpace = GigaSpaceUtils.getUniqueDestroeableGigaSpace();
        gigaSpace = destroyableGigaSpace.getGigaSpace();
        gigaSpaceUrl = destroyableGigaSpace.getGigaSpace().getSpace().getURL().getURL();
    }

    @After
    public void destroy() throws Exception {
        if (destroyableGigaSpace != null) destroyableGigaSpace.destroy();
    }

}
