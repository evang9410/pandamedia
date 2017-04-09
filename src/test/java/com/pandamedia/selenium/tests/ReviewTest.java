package com.pandamedia.selenium.tests;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Evan Glicakis
 */
public class ReviewTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        ChromeDriverManager.getInstance().setup();
        driver = new ChromeDriver();
    }

//    @After
//    public void destroy(){
//        driver.quit();
//    }
    @Ignore
    @Test
    public void testGoToWriteAReview() {
        driver.get("http://localhost:8080/pandamedia/");
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.id("browse-album-caption")).click();
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.xpath("//div[contains(@id, 'popular-browse')]/*[1]")).click();
        wait = new WebDriverWait(driver, 10);
//       driver.findElement(By.xpath("//*[@id=\"style-1\"]/tr[1]/td[3]/form/button")).click();
        driver.findElement(By.xpath("//*[@id=\"style-1\"]/tr[1]/td[1]/form")).click();
        driver.quit();

    }
    @Ignore
    @Test
    public void testAddPunkAlbumToCart() {
        driver.get("http://localhost:8080/pandamedia/");
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.id("browse-album-caption")).click();
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.xpath("//*[@id=\"j_idt79:j_idt80:2:j_idt81\"]/h4")).click();

    }
    
    @Test
    public void testCheckoutFilter(){
        driver.get("http://localhost:8080/pandamedia/");
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.id("browse-album-caption")).click();
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.xpath("//div[contains(@id, 'popular-browse')]/*[1]")).click();
        wait = new WebDriverWait(driver, 10);
        driver.findElement(By.xpath("//*[@id=\"style-1\"]/tr[1]/td[3]/form/button")).click();
        driver.get("http://localhost:8080/pandamedia/shop/cart.xhtml");
        driver.findElement(By.id("checkoutForm:checkoutBtn")).click();
        wait = new WebDriverWait(driver,10);
        driver.findElement(By.id("loginForm:emailInput")).sendKeys("e@e.com");
        driver.findElement(By.id("loginForm:passwordInput")).sendKeys("e");
        driver.findElement(By.id("loginForm:loginBtn")).click();
        
    }

}
