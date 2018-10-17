package mario.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.jupiter.api.Assertions.fail;

class FailingTestsClass {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.gecko.driver", "target/test-classes/geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        driver = new FirefoxDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void firstTestMethod() {
        driver.get("https://www.google.pl");
        driver.findElement(By.cssSelector(".gsfi"))
                .sendKeys("Mario");
        driver.findElement(By.cssSelector("input[name='btnK']")).click();
        fail();
    }

    @Test
    void secondTestMethod() {
        driver.get("https://www.google.pl");
        driver.findElement(By.cssSelector(".notExistingClass"))
                .sendKeys("Wario");
        driver.findElement(By.cssSelector("input[name='btnK']")).click();
    }
}
