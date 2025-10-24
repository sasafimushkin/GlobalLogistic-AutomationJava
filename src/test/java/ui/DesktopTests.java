package ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DesktopTests {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testDesktopsListAndAddMostExpensive() {
        driver.get("http://demowebshop.tricentis.com/computers");
        driver.findElement(By.linkText("Desktops")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[1]/h1")));
        String title = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[1]/h1")).getText();
        assertEquals("Desktops", title);

        // Set display to 4
        WebElement displayDropdown = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[2]/div[1]/div[3]/span[1]"));
        displayDropdown.click();
        // Select value "4"
        // Better to use Select on the select element near this span, if exists

        // Sort "Price: High to Low"
        WebElement sortDropdown = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[2]/div[1]/div[2]/span"));
        sortDropdown.click();
        // Select sort option "Price: High to Low"

        // Add the most expensive item by given xpath
        WebElement addToCartButton = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[2]/div[3]/div[1]/div/div[2]/div[3]/div[2]/input"));
        addToCartButton.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "1"));

        driver.findElement(By.xpath("//*[@id='topcartlink']/a/span[1]")).click();

        assertTrue(driver.findElement(By.cssSelector(".product")).isDisplayed());
    }

    @Test
    public void testBuildYourOwnComputerAddAndRemove() {
        driver.get("http://demowebshop.tricentis.com/build-your-own-expensive-computer-2");

        // Wait for and select Processor dropdown dynamically
        WebElement processorSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[id^='product_attribute_74']")));
        new Select(processorSelect).selectByVisibleText("Fast");

        WebElement ramSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[id^='product_attribute_75']")));
        new Select(ramSelect).selectByVisibleText("8GB");

        // Select all software checkboxes
        List<WebElement> softwareCheckboxes = driver.findElements(By.cssSelector("input[type='checkbox'][id^='product_attribute_']"));
        for (WebElement checkbox : softwareCheckboxes) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }

        driver.findElement(By.id("add-to-cart-button-74")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "1"));

        driver.findElement(By.xpath("//*[@id='topcartlink']/a/span[1]")).click();

        assertTrue(driver.findElement(By.cssSelector(".product")).isDisplayed());

        driver.findElement(By.name("removefromcart")).click();
        driver.findElement(By.name("updatecart")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "0"));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[id^='product_attribute_74']")));

    }
}
