package com.github.terma.gigaspacesqlconsole.server;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class E2EIT {

    private static Runner server;
    private static WebDriver driver;

    @BeforeClass
    public static void startBrowser() {
        server = new Runner();
        server.start();

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.get(Runner.ADDRESS);
    }

    @AfterClass
    public static void stopBrowser() {
        server.stop();
        driver.quit();
        server = null;
        driver = null;
    }

    @Test
    public void showMainPage() throws Exception {
        assertThat(driver.getTitle(), containsString("GigaSpace Web Console"));

//        Thread.sleep(1000L);

//        List<WebElement> apps = driver.findElements(By.tagName("a"));
//        assertThat(apps, hasSize(3));

//        List<String> texts = getTexts(apps);
//        assertThat(texts, Matchers.equalTo(Arrays.asList("app-local", "app-remote", "app-without-servers")));
    }

    private static List<String> getTexts(List<WebElement> apps) {
        List<String> texts = new ArrayList<>();
        for (WebElement element : apps) {
            texts.add(element.getText());
        }
        return texts;
    }

    @Test
    public void canExecuteQuery() throws Exception {
        driver.findElement(By.linkText("Query")).click();

        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("$('.CodeMirror')[0].CodeMirror.setValue('create table TT(id int)')");
        driver.findElement(By.linkText("Execute")).click();
        Thread.sleep(5000);

        assertThat(getTexts(driver.findElements(By.tagName("p"))), Matchers.contains("Results 1 for"));
    }

    @Test
    public void canSeeTypes() throws Exception {
        driver.findElement(By.linkText("Types")).click();
        driver.findElement(By.linkText("Start Again")).click();

        Thread.sleep(5000);

        assertThat(getTexts(driver.findElements(By.tagName("td"))), contains("java.lang.Object"));
    }

    @Test
    public void canCopy() throws Exception {


        driver.findElement(By.linkText("Copy")).click();
        driver.findElements(By.linkText("LOCAL")).get(1).click();
        driver.findElement(By.id("copy-textarea")).sendKeys("copy java.lang.Object");
        driver.findElements(By.linkText("Copy")).get(1).click();

//        driver.manage().window().

        Thread.sleep(5000);

//        assertThat(getTexts(driver.findElements(By.tagName("td"))), contains("java.lang.Object"));
    }

    @Test
    public void canExport() throws Exception {
        driver.findElement(By.linkText("Export/Import")).click();
        driver.findElement(By.linkText("Export")).click();

//        driver.manage().window().

        Thread.sleep(5000);

//        assertThat(getTexts(driver.findElements(By.tagName("td"))), contains("java.lang.Object"));
    }

}
