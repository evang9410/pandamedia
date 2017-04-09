package com.pandamedia.selenium;

//
//package com.pandamedia.selenium.tests;
//
//import io.github.bonigarcia.wdm.ChromeDriverManager;
//import org.junit.Before;
//import org.junit.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
///**
// *
// * @author Naasir Jusab
// */
//public class ManagementTest {
//    
//    private WebDriver driver;
//    private WebDriverWait wait;
//
//    @Before
//    public void setUp()
//    {
//        ChromeDriverManager.getInstance().setup();
//        driver = new ChromeDriver();
//        wait = new WebDriverWait(driver,10);
//    }
//    
//    /**
//     * The most basic Selenium test method that tests to see if the page name
//     * matches a specific name.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testSimple() throws Exception {
//        // Normally an executable that matches the browser you are using must
//        // be in the classpath. The webdrivermanager library by Boni Garcia
//        // downloads the required driver and makes it available
//        ChromeDriverManager.getInstance().setup();
//
//        // Create a new instance of the Chrome driver
//        // Notice that the remainder of the code relies on the interface,
//        // not the implementation.
//        driver = new ChromeDriver();
//        //WebDriver driver = new FirefoxDriver();
//
//        // And now use this to visit a web site
//        driver.get("http://www.google.com/");
//
//        // driver.navigate().to("http://www.google.com/");
//        driver.findElement(By.name("q")).sendKeys("Dawson College\n");
//        // click search
//        driver.findElement(By.name("btnG")).click();
//
//        wait = new WebDriverWait(driver, 10);
//
//        wait.until(new ExpectedCondition<Boolean>() {
//            @Override
//            public Boolean apply(WebDriver d) {
//                return d.findElement(By.tagName("body")).getText().contains("dawsoncollege.qc.ca");
//            }
//        });
//        driver.quit();
//
//    }
//    
//    @Test
//    public void testApprove() throws Exception 
//    {
//        ChromeDriverManager.getInstance().setup();
//        
//        driver = new ChromeDriver();
//        driver.get("http://localhost:8080/pandamedia/reviews.xhtml");
//        
//        //test the approve btn
//        driver.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=options]/commandButton[id=approveBtn]"));
//        
//        //delete this when the bug is fixed
//        driver.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=options]/commandButton[id=approveBtn]"));
//        
//        wait = new WebDriverWait(driver,10);
//        
//        wait.until(new ExpectedCondition<Boolean>() {
//            @Override
//            public Boolean apply(WebDriver d)
//            {
//                return d.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=approvalStatusID]/outputText[id=approvalStatusText]")).getText().contains("1");
//            }
//
//
//        });
//        
//        driver.quit();
//        
//        
//    }
//    
//    @Test
//    public void testDisapprove() throws Exception 
//    {
//        ChromeDriverManager.getInstance().setup();
//        
//        driver = new ChromeDriver();
//        driver.get("http://localhost:8080/pandamedia/reviews.xhtml");
//        
//        //test the approve btn
//        driver.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=options]/commandButton[id=disapproveBtn]"));
//        
//        //delete this when the bug is fixed
//        driver.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=options]/commandButton[id=disapproveBtn]"));
//        
//        wait = new WebDriverWait(driver,10);
//        
//        wait.until(new ExpectedCondition<Boolean>() {
//            @Override
//            public Boolean apply(WebDriver d)
//            {
//                return d.findElement(By.xpath("/form[@id=reviewFormID]/column[@id=approvalStatusID]/outputText[id=approvalStatusText]")).getText().contains("0");
//            }
//
//
//        });
//        
//        driver.quit();       
//        
//    }
//    
//}
