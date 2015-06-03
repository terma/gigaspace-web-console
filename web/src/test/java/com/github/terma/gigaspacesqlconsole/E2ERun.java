package com.github.terma.gigaspacesqlconsole;

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
        driver.get("http://localhost:8080/gs-sql-console/");
        assertThat(driver.getTitle(), Matchers.containsString("GigaSpace SQL Console"));
    }

    @Ignore
    @Test
    public void shouldExecuteSqlForSelectedGsAndShowResult() throws Exception {
        driver.get("http://localhost:8080/gs-sql-console/");
        assertThat(driver.getTitle(), Matchers.containsString("GigaSpace SQL Console"));
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        driver.quit();
    }

}
