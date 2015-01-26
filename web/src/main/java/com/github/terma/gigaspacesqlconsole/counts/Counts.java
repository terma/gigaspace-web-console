package com.github.terma.gigaspacesqlconsole.counts;

import com.github.terma.gigaspacesqlconsole.CachedProviderResolver;
import com.github.terma.gigaspacesqlconsole.core.Count;
import com.github.terma.gigaspacesqlconsole.core.CountsRequest;
import com.github.terma.gigaspacesqlconsole.core.CountsResponse;

import java.util.ArrayList;
import java.util.Random;

public class Counts {

    public static CountsResponse counts(CountsRequest request) {
        if (request.url.equals("/./test")) {
            return createTestResponse();
        }

        return CachedProviderResolver.getProvider(request.gs).counts(request);
    }

    private static CountsResponse createTestResponse() {
        CountsResponse countsResponse = new CountsResponse();
        countsResponse.counts = new ArrayList<>();

        Count count3 = new Count();
        count3.name = "com.github.terma.gigaspacesqlconsole.ZT";
        count3.count = new Random().nextInt(100);
        countsResponse.counts.add(count3);

        Count count4 = new Count();
        count4.name = "com.github.terma.gigaspacesqlconsole.AT";
        count4.count = new Random().nextInt(1000);
        countsResponse.counts.add(count4);

        Count count5 = new Count();
        count5.name = "com.github.terma.gigaspacesqlconsole.Time";
        count5.count = (int) (System.currentTimeMillis() / 1000);
        countsResponse.counts.add(count5);

        Count count7 = new Count();
        count7.name = "com.github.terma.gigaspacesqlconsole.Zero";
        count7.count = 0;
        countsResponse.counts.add(count7);

        Count count1 = new Count();
        count1.name = "com.github.terma.gigaspacesqlconsole.TestType";
        count1.count = new Random().nextInt(10);
        countsResponse.counts.add(count1);

        Count count2 = new Count();
        count2.name = "com.github.terma.gigaspacesqlconsole.Momo";
        count2.count = 1;
        countsResponse.counts.add(count2);

        if (new Random().nextBoolean()) {
            Count count6 = new Count();
            count6.name = "com.github.terma.gigaspacesqlconsole.Temp";
            count6.count = 1;
            countsResponse.counts.add(count6);
        }

        return countsResponse;
    }

}
