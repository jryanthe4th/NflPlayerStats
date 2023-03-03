package testbase;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static java.awt.MediaTracker.COMPLETE;

public class TestBase {

    public WebDriver driver;

    @BeforeClass
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (driver != null)
            driver.quit();
    }

//    public void waitForPageLoad(WebDriver webDriver) {
//        Wait<WebDriver> wait = new WebDriverWait(webDriver, TIMEOUT_IN_SECONDS);
//        wait.until(webDriver1 -> ((JavascriptExecutor) webDriver).executeScript(DOCUMENT_READY_STATE).equals(COMPLETE));
//    }

    public void acceptCookiesPolicy(WebDriver webDriver) {
        webDriver.findElement(By.id("onetrust-accept-btn-handler")).click();
        webDriver.switchTo().defaultContent();
    }
}
