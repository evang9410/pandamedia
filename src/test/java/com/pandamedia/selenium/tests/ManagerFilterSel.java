package com.pandamedia.selenium.tests;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.Before;
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
    public void testFilter() throws Exception
    {
        driver.get("http://localhost:8080/pandamedia/manager/manager_index.xhtml");
        wait = new WebDriverWait(driver,10);
        
        wait.until(ExpectedConditions.titleIs(com.pandamedia.utilities.
                Messages.getString("bundles.messages", "loginHeader", null)));
    }
}