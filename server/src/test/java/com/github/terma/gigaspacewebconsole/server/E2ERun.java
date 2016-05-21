/*
Copyright 2015-2016 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.server;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;

public class E2ERun {

    private Runner server;
    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        server = new Runner();
        server.start();

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void shouldShowMainPage() throws Exception {
        driver.get("http://localhost:8080/driver-web-console/");
        assertThat(driver.getTitle(), Matchers.containsString("GigaSpace SQL Console"));
    }

    @Ignore
    @Test
    public void shouldExecuteSqlForSelectedGsAndShowResult() throws Exception {
        driver.get("http://localhost:8080/driver-web-console/");
        assertThat(driver.getTitle(), Matchers.containsString("GigaSpace SQL Console"));
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        driver.quit();
    }

}
