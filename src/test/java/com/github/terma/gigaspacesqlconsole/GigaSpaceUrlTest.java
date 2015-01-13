package com.github.terma.gigaspacesqlconsole;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class GigaSpaceUrlTest {

    @Test
    public void shouldGetLocatorFromUrl() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseLocator("jini:/*/x?locators=yy.com.ab:1200"));
    }

    @Test
    public void shouldTestIfLocalSpace() {
        assertTrue(GigaSpaceUrl.isLocal("/./fff"));
        assertFalse(GigaSpaceUrl.isLocal("jini:/*/fff?"));
    }

    @Test
    public void shouldReturnFirstLocatorFromUrlIfThemMoreOne() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseLocator("jini:/*/x?locators=yy.com.ab:1200,locator.eq:1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfEmptyLocator() {
        assertEquals("yy.com.ab:1200", GigaSpaceUrl.parseLocator("jini:/*/x?locators="));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCantFindLocator() {
        GigaSpaceUrl.parseLocator("jini:/*/x?");
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
