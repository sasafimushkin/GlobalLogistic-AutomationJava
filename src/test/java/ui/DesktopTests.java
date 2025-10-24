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
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test A: Desktop products - display 4, sort, and add to cart")
    public void testDesktopsListAndAddMostExpensive() throws InterruptedException {
        // Navigate to Computers -> Desktops
        driver.get("http://demowebshop.tricentis.com/computers");
        driver.findElement(By.linkText("Desktops")).click();

        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("products-pagesize")));

        System.out.println("✓ Navigated to Desktops page");

        // Set display to 4 items per page
        WebElement displaySelect = driver.findElement(By.id("products-pagesize"));
        Select displayDropdown = new Select(displaySelect);
        displayDropdown.selectByVisibleText("4");

        // Wait for page refresh after changing display
        Thread.sleep(1500);

        System.out.println("✓ Set display to 4 items per page");

        // Verify exactly 4 items are displayed
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-item"));
        assertEquals(4, productItems.size(), "Expected 4 items to be displayed");
        System.out.println("✓ Verified 4 items displayed: " + productItems.size());

        // Sort by "Price: High to Low"
        WebElement sortSelect = driver.findElement(By.id("products-orderby"));
        Select sortDropdown = new Select(sortSelect);
        sortDropdown.selectByVisibleText("Price: High to Low");

        // Wait for page refresh after sorting
        Thread.sleep(1500);

        System.out.println("✓ Sorted by Price: High to Low");

        // Add the first (most expensive) item to cart
        List<WebElement> addToCartButtons = driver.findElements(
                By.cssSelector(".product-item .button-2[value='Add to cart']"));

        assertTrue(addToCartButtons.size() > 0, "Should have at least one Add to Cart button");
        addToCartButtons.get(0).click();

        System.out.println("✓ Clicked Add to Cart for most expensive item");

        // Wait for item to be added to cart
        Thread.sleep(2000);

        // Navigate to Shopping cart
        driver.findElement(By.linkText("Shopping cart")).click();

        // Wait for cart page to load
        wait.until(ExpectedConditions.urlContains("cart"));

        System.out.println("✓ Navigated to Shopping cart");

        // Verify item is in the cart
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item-row"));
        assertTrue(cartItems.size() > 0, "Shopping cart should contain at least one item");

        System.out.println("✓ Verified item is in shopping cart");
        System.out.println("✓✓✓ Test A completed successfully!");
    }

    @Test
    @DisplayName("Test B: Build custom computer, add to cart, and remove")
    public void testBuildYourOwnComputerAddAndRemove() throws InterruptedException {
        // Navigate to build computer page
        driver.get("http://demowebshop.tricentis.com/build-your-own-expensive-computer-2");

        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product_attribute_16_5_4")));

        System.out.println("✓ Navigated to Build Computer page");

        // Set Processor: Fast (2.5 GHz Intel Pentium Dual-Core E2200)
        WebElement processorSelect = driver.findElement(By.id("product_attribute_16_5_4"));
        Select processor = new Select(processorSelect);
        processor.selectByVisibleText("2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]");

        System.out.println("✓ Selected Fast Processor");

        // Set RAM: 8GB
        WebElement ramSelect = driver.findElement(By.id("product_attribute_16_6_5"));
        Select ram = new Select(ramSelect);
        ram.selectByVisibleText("8GB [+$60.00]");

        System.out.println("✓ Selected 8GB RAM");

        // Select all available software checkboxes
        List<WebElement> softwareCheckboxes = driver.findElements(
                By.cssSelector("input[name='product_attribute_16_8_6']"));

        int selectedCount = 0;
        for (WebElement checkbox : softwareCheckboxes) {
            if (!checkbox.isSelected()) {
                checkbox.click();
                selectedCount++;
            }
        }

        System.out.println("✓ Selected all software (" + softwareCheckboxes.size() + " items)");

        // Wait a moment for price calculation
        Thread.sleep(500);

        // Click Add to cart
        WebElement addToCartBtn = driver.findElement(By.id("add-to-cart-button-16"));
        addToCartBtn.click();

        System.out.println("✓ Clicked Add to Cart");

        // Wait for notification bar to appear and item to be added
        Thread.sleep(2500);

        // Navigate to Shopping cart
        driver.findElement(By.linkText("Shopping cart")).click();

        // Wait for cart page
        wait.until(ExpectedConditions.urlContains("cart"));

        System.out.println("✓ Navigated to Shopping cart");

        // Verify item is in cart
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item-row"));
        assertTrue(cartItems.size() > 0, "Cart should contain the configured computer");

        System.out.println("✓ Verified item is in cart (count: " + cartItems.size() + ")");

        // Verify it's the expensive computer
        WebElement productName = driver.findElement(By.cssSelector(".product-name a"));
        String productText = productName.getText();
        assertTrue(productText.contains("Build your own expensive computer"),
                "Should be the expensive computer");

        System.out.println("✓ Verified correct product: " + productText);

        // Get the price
        WebElement priceElement = driver.findElement(By.cssSelector(".product-unit-price"));
        String price = priceElement.getText();
        System.out.println("✓ Product price in cart: " + price);

        // Remove item from cart
        WebElement removeCheckbox = driver.findElement(By.name("removefromcart"));
        if (!removeCheckbox.isSelected()) {
            removeCheckbox.click();
        }

        System.out.println("✓ Checked remove checkbox");

        // Click update cart button
        WebElement updateButton = driver.findElement(By.name("updatecart"));
        updateButton.click();

        System.out.println("✓ Clicked Update Cart");

        // Wait for cart to update
        Thread.sleep(2000);

        // Verify cart is empty
        try {
            WebElement emptyMessage = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".order-summary-content")));
            String messageText = emptyMessage.getText();

            assertTrue(messageText.contains("Your Shopping Cart is empty!"),
                    "Cart should be empty");
            System.out.println("✓ Verified cart is empty");
        } catch (Exception e) {
            // Alternative verification: check no cart items
            List<WebElement> remainingItems = driver.findElements(By.cssSelector(".cart-item-row"));
            assertEquals(0, remainingItems.size(), "Cart should have 0 items");
            System.out.println("✓ Verified cart has no items");
        }

        System.out.println("✓✓✓ Test B completed successfully!");
    }
}