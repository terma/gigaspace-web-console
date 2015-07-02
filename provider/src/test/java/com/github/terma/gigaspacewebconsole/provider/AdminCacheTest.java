package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.core.GeneralRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AdminCacheTest {

    @Test
    public void shouldCloseExpiredAdmins() throws InterruptedException {
        // given
        final AdminCache adminCache = new AdminCache(0);
        final GeneralRequest request = new GeneralRequest();

        request.url = "/./admin-cache-test";
        adminCache.createOrGet(request);

        request.url = "/./admin-cache-test1";
        request.user = "admin";
        adminCache.createOrGet(request);

        assertThat(adminCache.size(), equalTo(2));

        // when
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        adminCache.clearExpired();

        // then
        assertThat(adminCache.size(), equalTo(0));
    }

    @Test
    public void shouldNotCloseNotExpiredAdmin() throws InterruptedException {
        // given
        final AdminCache adminCache = new AdminCache(TimeUnit.MINUTES.toMillis(1));
        final GeneralRequest request = new GeneralRequest();

        request.url = "/./admin-cache-test";
        adminCache.createOrGet(request);

        assertThat(adminCache.size(), equalTo(1));

        // when
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        adminCache.clearExpired();

        // then
        assertThat(adminCache.size(), equalTo(1));
    }

}
