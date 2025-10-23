package api;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BookingAPITest {
    static String token;
    static int bookingId;

    @BeforeAll
    public static void setup() {
        baseURI = "https://restful-booker.herokuapp.com";
        token = given()
                .contentType("application/json")
                .body("{\"username\" : \"admin\", \"password\" : \"password123\"}")
                .when().post("/auth")
                .then().extract().path("token");
    }

    @Test
    @Order(1)
    public void createBooking() {
        Response response = given()
                .contentType("application/json")
                .body("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"totalprice\":500,\"depositpaid\":true," +
                        "\"bookingdates\":{\"checkin\":\"2023-10-01\",\"checkout\":\"2023-10-10\"},\"additionalneeds\":\"Breakfast\"}")
                .post("/booking");
        bookingId = response.then().extract().path("bookingid");
        response.then().statusCode(200).body("booking.firstname", equalTo("John"));
    }

    @Test
    @Order(2)
    public void updateBooking() {
        given().contentType("application/json")
                .cookie("token", token)
                .body("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"totalprice\":700,\"depositpaid\":true," +
                        "\"bookingdates\":{\"checkin\":\"2023-10-01\",\"checkout\":\"2023-10-10\"},\"additionalneeds\":\"Breakfast\"}")
                .put("/booking/" + bookingId)
                .then().statusCode(200)
                .body("totalprice", equalTo(700));
    }

    @Test
    @Order(3)
    public void deleteBooking() {
        given().cookie("token", token)
                .delete("/booking/" + bookingId)
                .then().statusCode(201);
    }
}
