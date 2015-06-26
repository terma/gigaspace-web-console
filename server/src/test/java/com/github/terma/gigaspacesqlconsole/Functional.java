package com.github.terma.gigaspacewebconsole;

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Functional {

    @Test
    public void executeSqlQuery() {
        WebDriver driver = new FirefoxDriver();

        driver.get("http://localhost:8080/gs-web-console/");

        final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript(
                "document.getElementsByClassName(\"CodeMirror\")[0].CodeMirror.setValue(\"select * from Customer\")");

        WebElement execute = driver.findElement(By.partialLinkText("Execute"));
        execute.click();

        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver d) {
                return d.findElement(By.className("query")).isDisplayed();
            }

        });

        Assert.assertTrue(driver.findElement(By.className("query")).isDisplayed());

        driver.quit();
    }

}
