
package ApiTest.Test.User;

import ApiTest.POJO.userLogin;
import ApiTest.Utils.UserFactory;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ApiTest.SpecBuilders.RequestBuilder.requestSpec;
import static ApiTest.SpecBuilders.ResponseBuilder.postResponse;
import static ApiTest.endpoints.Routes.create_user_endpoint;
import static ApiTest.endpoints.Routes.user_login;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * The UserTest class contains unit test cases for testing user-related API endpoints.
 */
public class UserTest {
    private static final Logger LOGGER = Logger.getLogger(UserTest.class.getName());
    private Properties props = new Properties();
    private Map<String, String> user;
    private String token;

    @BeforeClass
    public void setup() {
        user = UserFactory.getRandomUser();
    }

    @Test
    public void testCreateUser() {
        Response response = given()
                .spec(requestSpec())
                .body(user)
                .when()
                .post(create_user_endpoint)
                .then()
                .spec(postResponse())
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("src/test/resources/schemas/schemas/user-created-schema.json"))
                .extract().response();

        String token = response.jsonPath().get("token");
        user.put("token", token);
    }

    @Test(dependsOnMethods = "testCreateUser")
    public void userLogin() {
        userLogin userCredentials = new userLogin();
        userCredentials.setEmail(user.get("email"));
        userCredentials.setPassword(user.get("password"));

        Response loginResponse = given()
                .spec(requestSpec())
                .body(userCredentials)
                .when()
                .post(user_login)
                .then()
                .spec(postResponse())
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-login-schema.json"))
                .extract().response();

        token = loginResponse.jsonPath().get("token");
    }

    @AfterClass
    public void setToken() {
        if (token != null) {
            props.setProperty("token", token);
            try (FileOutputStream output = new FileOutputStream("src/test/resources/config.properties")) {
                props.store(output, "Configuration");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to write token to config.properties", e);
            }
        }
    }
}
