package com.pandamedia.selenium;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Erika Bourque
 */
public class ManagerFilterSel {
    private WebDriver driver;
    private WebDriverWait wait;
    
    @Before
    public void setUp()
    {
        ChromeDriverManager.getInstance().setup();
        driver = new ChromeDriver();
    }
    
    @Test
    public void testManagerFilter() throws Exception
    {
        driver.get("http://waldo2.dawsoncollege.qc.ca:8080/g4w17/");
        wait = new WebDriverWait(driver, 10);
        driver.get("http://waldo2.dawsoncollege.qc.ca:8080/g4w17/manager/manager_index.xhtml");
        wait = new WebDriverWait(driver,10);
        
        wait.until(ExpectedConditions.titleIs("Login"));
//        wait.until(ExpectedConditions.titleIs(com.pandamedia.utilities.
//                Messages.getString("bundles.messages", "loginHeader", null)));        
    }
    
    @After
    public void tearDown()
    {
        driver.quit();
    }
}
