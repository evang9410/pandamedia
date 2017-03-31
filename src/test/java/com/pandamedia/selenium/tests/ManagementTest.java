
package com.pandamedia.selenium.tests;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Naasir Jusab
 */
public class ManagementTest {
    
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp()
    {
        ChromeDriverManager.getInstance().setup();
        driver = new ChromeDriver();
    }
    
    
    @Test
    public void testApprove() throws Exception 
    {

        driver.get("http://localhost:8080/pandamedia/reviews.xhtml");
        
        wait = new WebDriverWait(driver,10);
        
        
        //test the approve btn
        driver.findElement(By.id("reviewFormID:reviewTableID:0:approveBtn")).click();
        
        //delete this when the bug is fixed
        driver.findElement(By.id("reviewFormID:reviewTableID:0:approveBtn")).click();
        
        wait = new WebDriverWait(driver,10);
        
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d)
            {
                return d.findElement(By.id("reviewFormID:reviewTableID:0:approvalStatusText")).getText().contains("1");
            }


        });
        
        driver.quit();
        
        
    }
    
    @Test
    public void testDisapprove() throws Exception 
    {

        driver.get("http://localhost:8080/pandamedia/reviews.xhtml");
        
        
        wait = new WebDriverWait(driver,10);
        
        //test the disapprove btn
        driver.findElement(By.id("reviewFormID:reviewTableID:0:disapproveBtn")).click();
        
        //delete this when the bug is fixed
        driver.findElement(By.id("reviewFormID:reviewTableID:0:disapproveBtn")).click();
        
        wait = new WebDriverWait(driver,10);
        
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d)
            {
                return d.findElement(By.id("reviewFormID:reviewTableID:0:approvalStatusText")).getText().contains("0");
            }


        });
        
        driver.quit();       
        
    }
    
    @Test
    public void testCreateTrack() throws Exception
    {
        driver.get("http://localhost:8080/pandamedia/manager_index.xhtml");
        
        
        wait = new WebDriverWait(driver,10);
        
          //test the create track btn
        driver.findElement(By.id("albumFormID:albumTable:0:editAlbumBtn")).click();
        
        driver.findElement(By.id("editTrackForm:title")).sendKeys("Evan sucks");
        driver.findElement(By.id("editTrackForm:releaseDate")).sendKeys("1/1/1900");
        driver.findElement(By.id("editTrackForm:playLength")).sendKeys("1:45");
        driver.findElement(By.id("editTrackForm:dateEntered")).sendKeys("1/1/1900");
        driver.findElement(By.id("editTrackForm:partOfAlbum")).sendKeys("0");
        driver.findElement(By.id("editTrackForm:albumTrackNumber")).sendKeys("0");
        driver.findElement(By.id("editTrackForm:costPrice")).sendKeys("1.00");
        driver.findElement(By.id("editTrackForm:listPrice")).sendKeys("1.50");
        driver.findElement(By.id("editTrackForm:salePrice")).sendKeys("1.00");
        driver.findElement(By.id("editTrackForm:removalStatus")).sendKeys("0");
         
         driver.findElement(By.id("editTrackForm:editTrackBtn")).click();
               
          wait = new WebDriverWait(driver,10);
        
          wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d)
            {
                boolean isValid = false;
                isValid = d.findElement(By.id("albumFormID:albumTable:0:title")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:releaseDate")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:playLength")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:dateEntered")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:partOfAlbum")).getText().contains("Evan sucks");
                
                isValid = d.findElement(By.id("albumFormID:albumTable:0:albumTrackNumber")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:costPrice")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:listPrice")).getText().contains("Evan sucks");
                isValid = d.findElement(By.id("albumFormID:albumTable:0:salePrice")).getText().contains("Evan sucks");

                return isValid;             
            }


          });
        
          driver.quit();       
        
        
        
    }
    
}
