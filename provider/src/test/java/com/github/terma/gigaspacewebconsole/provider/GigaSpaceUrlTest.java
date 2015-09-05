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

import org.junit.Test;

import static junit.framework.Assert.*;

public class GigaSpaceUrlTest {

    @Test
    public void shouldGetLocatorFromUrl() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseLocators("jini:/*/x?locators=yy.com.ab:1200"));
    }

    @Test
    public void shouldTestIfLocalSpace() {
        assertTrue(GigaSpaceUrl.isLocal("/./fff"));
        assertFalse(GigaSpaceUrl.isLocal("jini:/*/fff?"));
    }

    @Test
    public void shouldReturnAllLocators() {
        assertEquals("yy.com.ab:1200,locator.eq:1", GigaSpaceUrl.parseLocators("jini:/*/x?locators=yy.com.ab:1200,locator.eq:1"));
        assertEquals("loc.col.com:1200,loc.com:1200", GigaSpaceUrl.parseLocators("jini:/*/x?locators=loc.col.com:1200,loc.com:1200&group=1"));
        assertEquals("loc.col.com:1200", GigaSpaceUrl.parseLocators("jini:/*/x?locators=loc.col.com:1200"));
        assertEquals("loc.col.com", GigaSpaceUrl.parseLocators("jini:/*/x?locators=loc.col.com"));
        assertEquals("loc.col.com", GigaSpaceUrl.parseLocators("jini:/*/x?groups=mega&locators=loc.col.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfEmptyLocator() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseLocators("jini:/*/x?locators="));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCantFindLocator() {
        GigaSpaceUrl.parseLocators("jini:/*/x?");
    }

    @Test
    public void shouldGetSpaceNameFromUrl() {
        assertEquals("space", GigaSpaceUrl.parseSpace("jini:/*/space?locators=yy.com.ab:1200"));
        assertEquals("space", GigaSpaceUrl.parseSpace("jini:/*/space?"));
        assertEquals("space", GigaSpaceUrl.parseSpace("/./space?"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfEmptySpaceName() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseSpace("jini:/*/?"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCantFindSpaceName() {
        GigaSpaceUrl.parseSpace("jini:/*/");
    }

}
