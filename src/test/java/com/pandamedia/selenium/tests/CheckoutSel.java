package com.pandamedia.selenium.tests;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Erika Bourque
 */
public class CheckoutSel {
    private WebDriver driver;
    private WebDriverWait wait;
    
    @Before
    public void setUp()
    {
        ChromeDriverManager.getInstance().setup();
        driver = new ChromeDriver();
    }
    
    @Test
    public void testPurchase() throws Exception
    {
        driver.get("http://localhost:8080/pandamedia/shop/browsealbums.xhtml");
        wait = new WebDriverWait(driver,10);
        
        // once albums on browse albums page have id, input it here
        //driver.findElement(By.id("reviewFormID:reviewTableID:0:approveBtn")).click();
        wait = new WebDriverWait(driver,10);
        
        driver.findElement(By.id("addToCartForm:addToCartBtn")).click();
        wait = new WebDriverWait(driver,10);
    }
}
