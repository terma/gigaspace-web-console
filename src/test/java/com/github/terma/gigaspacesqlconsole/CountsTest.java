package com.github.terma.gigaspacesqlconsole;

import junit.framework.Assert;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

public class CountsTest {

    @Test
    public void shouldReturnEmptyResultIfNoTypesInSpace() {
        GigaSpaceUtils.getGigaSpace("/./ff");

        CountsRequest countsRequest = new CountsRequest();
        countsRequest.url = "/./ff?";
        final CountsResponse countsResponse = Counts.counts(countsRequest);

        // then only Object default
        Assert.assertEquals(1, countsResponse.counts.size());
    }

    @Test
    public void shouldReturnCountsOfTypes() {
        String url = "/./ff1?";
        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(url);
        GigaSpaceUtils.registerType(gigaSpace, "com.a");

        GigaSpaceUtils.createDocument(gigaSpace, "com.a");
        GigaSpaceUtils.createDocument(gigaSpace, "com.a");
        GigaSpaceUtils.createDocument(gigaSpace, "com.a");

        CountsRequest countsRequest = new CountsRequest();
        countsRequest.url = url;
        final CountsResponse countsResponse = Counts.counts(countsRequest);

        // then
        Assert.assertEquals(2, countsResponse.counts.size());
        Assert.assertEquals("com.a", countsResponse.counts.get(1).name);
        Assert.assertEquals(3, countsResponse.counts.get(1).count);
    }

}
