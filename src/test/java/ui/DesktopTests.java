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

    // ---------- PART A ----------
    @Test
    public void testDesktopsAddMostExpensive() {
        driver.get("http://demowebshop.tricentis.com/");
        driver.findElement(By.linkText("Computers")).click();
        driver.findElement(By.linkText("Desktops")).click();

        // Verify "Desktops" page opened
        String title = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[1]/h1")).getText();
        assertEquals("Desktops", title);

        // Set "Display" to 4 per page
        Select displaySelect = new Select(driver.findElement(By.id("products-pagesize")));
        displaySelect.selectByVisibleText("4");

        // Check only 4 items displayed
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector(".product-item"), 5));
        List<WebElement> items = driver.findElements(By.cssSelector(".product-item"));
        assertEquals(4, items.size());

        // Sort by "Price: High to Low"
        Select sortSelect = new Select(driver.findElement(By.id("products-orderby")));
        sortSelect.selectByVisibleText("Price: High to Low");

        // Add first product (most expensive) to cart
        WebElement addToCart = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div[2]/div[2]/div[3]/div[1]/div/div[2]/div[3]/div[2]/input"));
        addToCart.click();

        // Wait until cart count updates
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "1"));

        // Open shopping cart
        driver.findElement(By.xpath("//*[@id='topcartlink']/a/span[1]")).click();

        // Verify product exists in cart
        assertTrue(driver.findElement(By.cssSelector(".product")).isDisplayed(), "Product not found in cart");
    }

    // ---------- PART B ----------
    @Test
    public void testBuildYourOwnComputer() {
        driver.get("http://demowebshop.tricentis.com/build-your-own-expensive-computer-2");

        // Wait for dropdown to become visible
        WebElement processorDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("select[id^='product_attribute_74']")));
        new Select(processorDropdown).selectByVisibleText("Fast");

        // Set RAM to 8GB
        WebElement ramDropdown = driver.findElement(By.cssSelector("select[id^='product_attribute_75']"));
        new Select(ramDropdown).selectByVisibleText("8GB");

        // Select all software checkboxes
        List<WebElement> softwareCheckboxes = driver.findElements(By.cssSelector("input[type='checkbox'][id^='product_attribute_']"));
        for (WebElement checkbox : softwareCheckboxes) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }

        // Click "Add to Cart"
        WebElement addToCart = driver.findElement(By.id("add-to-cart-button-74"));
        addToCart.click();

        // Wait for cart to be updated
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "1"));

        // Open shopping cart
        driver.findElement(By.xpath("//*[@id='topcartlink']/a/span[1]")).click();

        // Verify product displayed in cart
        WebElement product = driver.findElement(By.cssSelector(".product"));
        assertTrue(product.isDisplayed(), "Added computer not displayed in shopping cart");

        // Remove item from Cart
        driver.findElement(By.name("removefromcart")).click();
        driver.findElement(By.name("updatecart")).click();

        // Wait for cart to be empty
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[@id='topcartlink']/a/span[1]"), "0"));
    }
}