package ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DesktopTests {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://demowebshop.tricentis.com/computers");
        driver.findElement(By.linkText("Desktops")).click();
    }

    @Test
    public void verifyDisplayAndSortAndAddToCart() {
        new Select(driver.findElement(By.id("products-pagesize"))).selectByVisibleText("4");
        List<WebElement> items = driver.findElements(By.cssSelector(".product-item"));
        assertEquals(4, items.size(), "Displayed items not equal to 4");

        new Select(driver.findElement(By.id("products-orderby"))).selectByVisibleText("Price: High to Low");
        driver.findElement(By.cssSelector(".product-item:first-child input[value='Add to cart']")).click();
        driver.findElement(By.linkText("Shopping cart")).click();
        assertTrue(driver.findElement(By.cssSelector("#topcartlink > a > span.cart-label")).isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
