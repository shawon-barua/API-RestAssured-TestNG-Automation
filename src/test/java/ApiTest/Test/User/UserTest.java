package ApiTest.Test.User;

import ApiTest.POJO.userLogin;
import ApiTest.Utils.UserFactory;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ApiTest.endpoints.Routes.base_URL;
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

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec201;
    private ResponseSpecification responseSpec200;

    @BeforeClass
    public void setup() {
        user = UserFactory.getRandomUser();

        String baseUri = System.getenv("BASE_URI") != null ? System.getenv("BASE_URI") : base_URL;
        String authToken = System.getenv("AUTH_TOKEN") != null ? System.getenv("AUTH_TOKEN") : "";

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);

        if (!authToken.isEmpty()) {
            reqBuilder.addHeader("Authorization", "Bearer " + authToken);
        }
        requestSpec = reqBuilder.build();

        responseSpec201 = new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .build();

        responseSpec200 = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void testCreateUser() {
        Response response = given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post(create_user_endpoint)
                .then()
                .spec(responseSpec201)
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/contact-created-schema.json"))
                .extract().response();

        String extractedToken = response.jsonPath().get("token");
        if (extractedToken != null) {
            user.put("token", extractedToken);
        }
    }

    @Test(dependsOnMethods = "testCreateUser")
    public void userLogin() {
        userLogin userCredentials = new userLogin();
        userCredentials.setEmail(user.get("email"));
        userCredentials.setPassword(user.get("password"));

        Response loginResponse = given()
                .spec(requestSpec)
                .body(userCredentials)
                .when()
                .post(user_login)
                .then()
                .spec(responseSpec200)
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/contact-created-schema.json"))
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
