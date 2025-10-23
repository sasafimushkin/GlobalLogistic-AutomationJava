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

        new Select(driver.findElement(By.id("products-pagesize"))).selectByVisibleText("4");
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".product-item"), 4));
        List<WebElement> items = driver.findElements(By.cssSelector(".product-item"));
        assertEquals(4, items.size(), "Displayed items not equal to 4");

        new Select(driver.findElement(By.id("products-orderby"))).selectByVisibleText("Price: High to Low");

        driver.findElement(By.cssSelector(".product-item:first-child input[value='Add to cart']")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#topcartlink > a > span.cart-label"), "1"));

        driver.findElement(By.linkText("Shopping cart")).click();

        assertTrue(driver.findElement(By.cssSelector(".product")).isDisplayed(), "Item not found in cart");
    }

    @Test
    public void testBuildYourOwnComputerAddAndRemove() {
        driver.get("http://demowebshop.tricentis.com/build-your-own-expensive-computer-2");

        WebElement processorSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id^='product_attribute_74']")));
        new Select(processorSelect).selectByVisibleText("Fast");

        WebElement ramSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id^='product_attribute_75']")));
        new Select(ramSelect).selectByVisibleText("8GB");

        List<WebElement> softwareCheckboxes = driver.findElements(
                By.cssSelector("input[type='checkbox'][id^='product_attribute_']"));
        for (WebElement cb : softwareCheckboxes) {
            if (!cb.isSelected()) {
                cb.click();
            }
        }

        driver.findElement(By.id("add-to-cart-button-72")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("#topcartlink > a > span.cart-label"), "1"));

        driver.findElement(By.linkText("Shopping cart")).click();

        assertTrue(driver.findElement(By.cssSelector(".product")).isDisplayed(), "Built computer not found in cart");

        driver.findElement(By.name("removefromcart")).click();
        driver.findElement(By.name("updatecart")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("#topcartlink > a > span.cart-label"), "0"));
    }
}
