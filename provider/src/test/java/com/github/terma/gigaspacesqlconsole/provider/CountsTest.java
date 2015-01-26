package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import com.github.terma.gigaspacesqlconsole.core.CountsResponse;
import junit.framework.Assert;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.util.HashMap;
import java.util.Map;

public class CountsTest {

    @Test
    public void shouldReturnEmptyResultIfNoTypesInSpace() {
        GigaSpaceUtils.getGigaSpace("/./ff");

        CountsRequest countsRequest = new CountsRequest();
        countsRequest.url = "/./ff?";
        final CountsResponse countsResponse = new CountsProviderImpl().counts(countsRequest);

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
        final CountsResponse countsResponse = new CountsProviderImpl().counts(countsRequest);

        // then
        Assert.assertEquals(2, countsResponse.counts.size());
        Map<String, Integer> counts = new HashMap<>();
        counts.put(countsResponse.counts.get(0).name, countsResponse.counts.get(0).count);
        counts.put(countsResponse.counts.get(1).name, countsResponse.counts.get(1).count);
        Assert.assertEquals(new HashMap<String, Integer>() {{
            put("com.a", 3);
            put("java.lang.Object", 0);
        }}, counts);
    }

}
