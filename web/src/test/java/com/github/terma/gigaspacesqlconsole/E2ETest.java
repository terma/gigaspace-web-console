package com.github.terma.gigaspacesqlconsole;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class E2ETest {

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
    public void testSimple() throws Exception {
        driver.get("http://localhost:8080/gs-sql-console/");
        Assert.assertThat(driver.getTitle(), Matchers.containsString("GigaSpace SQL Console"));
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        driver.quit();
    }

}
