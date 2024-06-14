import io.restassured.response.Response;
import models.RegisterDTO;
import models.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ReqresTests extends TestBase {

    @DisplayName("Check that user data are received")
    @Tag("API")
    @Test
    void fetchUserInfoTest() {
        when().
                get("users/{id}", 1).
                then().
                assertThat().
                statusCode(200).
                body("data.first_name", equalTo("George"),
                        "data.email", equalTo("george.bluth@reqres.in"));
    }

    @DisplayName("Check that new user can be created")
    @Tag("API")
    @Test
    void createNewUserTest() {
        UserDTO user = new UserDTO("Vlad", "QA");
        String id = given()
                .body(user)
                .when()
                .post("/users/")
                .then()
                .assertThat()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .body("job", equalTo(user.getJob()))
                .body("id", notNullValue())
                .extract().path("id");
        given().delete("users/" + id);
    }

    @DisplayName("Check that user can be deleted")
    @Tag("API")
    @Test
    void deleteUserTest() {
        UserDTO user = new UserDTO("Ivan", "DevOps");
        int id = Integer.parseInt(
                given()
                        .body(user)
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .response()
                        .body()
                        .path("id"));

        given()
                .delete("users/" + id)
                .then()
                .assertThat()
                .statusCode(204)
                .body(equalTo(""));
    }

    @Test
    @DisplayName("Check that error is returned when user can't register")
    @Tag("API")
    void unsuccessfulRegistrationTest() {
        RegisterDTO regData = new RegisterDTO("vlad@reqres.in", "test");
        given()
                .body(regData)
                .when()
                .post("/register")
                .then().
                assertThat()
                .statusCode(400)
                .body("error", equalTo("Note: Only defined users succeed registration"));
    }

    @Test
    @DisplayName("Check that not existed user can't login")
    @Tag("API")
    void notExistingUserLoginTest() {
        RegisterDTO authData = new RegisterDTO("vlad@reqres.in", "test");

        given()
                .body(authData)
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("error", equalTo("user not found"));
    }

    @Test
    @DisplayName("Check that login with empty body returns error")
    @Tag("API")
    void loginWithEmptyDataTest() {
        given()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
    }
}
