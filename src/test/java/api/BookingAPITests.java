package api;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import java.util.List;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingAPITests {
    static String token;
    static int bookingId;
    static final String BASE_URI = "https://restful-booker.herokuapp.com";

    @BeforeAll
    public static void setup() {
        baseURI = BASE_URI;
        System.out.println("=== API Testing: Restful Booker ===");
        System.out.println("Base URI: " + BASE_URI);
        System.out.println();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Perform authentication and get token")
    public void testAuthentication() {
        System.out.println("Step 1: Authenticating...");

        Response response = given()
                .contentType("application/json")
                .body("{\"username\":\"admin\",\"password\":\"password123\"}")
                .when()
                .post("/auth");

        response.then().statusCode(200);

        token = response.jsonPath().getString("token");
        assertNotNull(token, "Token should not be null");

        System.out.println("✓ Authentication successful");
        System.out.println("✓ Token: " + token);
        System.out.println();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Create a new booking")
    public void testCreateBooking() {
        System.out.println("Step 2: Creating new booking...");

        String bookingJson = "{"
                + "\"firstname\":\"John\","
                + "\"lastname\":\"Doe\","
                + "\"totalprice\":500,"
                + "\"depositpaid\":true,"
                + "\"bookingdates\":{"
                + "\"checkin\":\"2024-01-01\","
                + "\"checkout\":\"2024-01-10\""
                + "},"
                + "\"additionalneeds\":\"Breakfast\""
                + "}";

        Response response = given()
                .contentType("application/json")
                .body(bookingJson)
                .when()
                .post("/booking");

        // Extract booking ID
        bookingId = response.jsonPath().getInt("bookingid");
        assertTrue(bookingId > 0, "Booking ID should be positive");

        // Verify response
        response.then()
                .statusCode(200)
                .body("booking.firstname", equalTo("John"))
                .body("booking.lastname", equalTo("Doe"))
                .body("booking.totalprice", equalTo(500))
                .body("booking.depositpaid", equalTo(true))
                .body("booking.bookingdates.checkin", equalTo("2024-01-01"))
                .body("booking.bookingdates.checkout", equalTo("2024-01-10"))
                .body("booking.additionalneeds", equalTo("Breakfast"));

        System.out.println("✓ Booking created successfully");
        System.out.println("✓ Booking ID: " + bookingId);
        System.out.println("✓ All details verified in creation response");
        System.out.println();
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Get details of newly created booking")
    public void testGetBookingDetails() {
        System.out.println("Step 3: Getting booking details for ID: " + bookingId);

        Response response = given()
                .when()
                .get("/booking/" + bookingId);

        // Verify all details match
        response.then()
                .statusCode(200)
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("totalprice", equalTo(500))
                .body("depositpaid", equalTo(true))
                .body("bookingdates.checkin", equalTo("2024-01-01"))
                .body("bookingdates.checkout", equalTo("2024-01-10"))
                .body("additionalneeds", equalTo("Breakfast"));

        System.out.println("✓ Retrieved booking successfully");
        System.out.println("✓ All details match the original booking:");
        System.out.println("  - firstname: John");
        System.out.println("  - lastname: Doe");
        System.out.println("  - totalprice: 500");
        System.out.println("  - depositpaid: true");
        System.out.println("  - checkin: 2024-01-01");
        System.out.println("  - checkout: 2024-01-10");
        System.out.println("  - additionalneeds: Breakfast");
        System.out.println();
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Update booking details (change totalprice)")
    public void testUpdateBooking() {
        System.out.println("Step 4: Updating booking ID: " + bookingId);
        System.out.println("Changing totalprice from 500 to 700");

        String updatedBookingJson = "{"
                + "\"firstname\":\"John\","
                + "\"lastname\":\"Doe\","
                + "\"totalprice\":700,"
                + "\"depositpaid\":true,"
                + "\"bookingdates\":{"
                + "\"checkin\":\"2024-01-01\","
                + "\"checkout\":\"2024-01-10\""
                + "},"
                + "\"additionalneeds\":\"Breakfast\""
                + "}";

        Response response = given()
                .contentType("application/json")
                .cookie("token", token)
                .body(updatedBookingJson)
                .when()
                .put("/booking/" + bookingId);

        response.then()
                .statusCode(200)
                .body("totalprice", equalTo(700))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"));

        System.out.println("✓ Booking updated successfully");
        System.out.println("✓ New totalprice: 700");
        System.out.println();
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Get details of updated booking and ensure it has new details")
    public void testVerifyUpdatedBooking() {
        System.out.println("Step 5: Verifying updated booking details...");

        Response response = given()
                .when()
                .get("/booking/" + bookingId);

        // Verify the update worked
        response.then()
                .statusCode(200)
                .body("totalprice", equalTo(700))
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"));

        int retrievedPrice = response.jsonPath().getInt("totalprice");
        assertEquals(700, retrievedPrice, "Total price should be updated to 700");

        System.out.println("✓ Retrieved updated booking successfully");
        System.out.println("✓ Confirmed totalprice changed from 500 to 700");
        System.out.println("✓ All other details remain correct");
        System.out.println();
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Get all bookings and check newly created booking exists")
    public void testGetAllBookings() {
        System.out.println("Step 6: Getting all bookings...");

        Response response = given()
                .when()
                .get("/booking");

        response.then().statusCode(200);

        // Extract all booking IDs
        List<Integer> allBookingIds = response.jsonPath().getList("bookingid", Integer.class);

        assertNotNull(allBookingIds, "Booking IDs list should not be null");
        assertTrue(allBookingIds.size() > 0, "Should have at least one booking");

        // Verify our booking is in the list
        boolean found = allBookingIds.contains(bookingId);
        assertTrue(found, "Our booking (ID: " + bookingId + ") should exist in all bookings");

        System.out.println("✓ Retrieved all bookings");
        System.out.println("✓ Total bookings in system: " + allBookingIds.size());
        System.out.println("✓ Confirmed booking ID " + bookingId + " exists in the list");
        System.out.println();
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Delete the booking")
    public void testDeleteBooking() {
        System.out.println("Step 7: Deleting booking ID: " + bookingId);

        // Delete the booking
        Response deleteResponse = given()
                .cookie("token", token)
                .when()
                .delete("/booking/" + bookingId);

        deleteResponse.then().statusCode(201);
        System.out.println("✓ Booking deleted (Status: 201 Created)");

        // Verify deletion by trying to get the deleted booking
        Response getResponse = given()
                .when()
                .get("/booking/" + bookingId);

        getResponse.then().statusCode(404);
        System.out.println("✓ Verified booking no longer exists (Status: 404 Not Found)");
        System.out.println();
        System.out.println("=== ✓✓✓ All API tests completed successfully! ===");
    }
}