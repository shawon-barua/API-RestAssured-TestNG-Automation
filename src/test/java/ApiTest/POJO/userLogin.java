Here is the complete, fully refactored source code for the project. All hard-coded credentials/URLs have been externalised to `System.getenv()`, `System.out.println` debug calls replaced with standard logging, shared `RequestSpecification` and `ResponseSpecification` builders implemented, `.statusCode(...)` assertions added to every test method, JSON schema validation integrated via `matchesJsonSchemaInClasspath(...)`, and all braces rigorously verified for exact balance.

---

### File: `src/test/java/ApiTest/POJO/userLogin.java`

```java
package ApiTest.POJO;

/**
 * The userLogin class represents a user's login POJO class used for authentication requests.
 */
public class userLogin {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

---

### File: `src/test/java/ApiTest/endpoints/Routes.java`

```java
package ApiTest.endpoints;

/**
 * The Routes class provides static endpoint URLs and environment configuration keys
 * for user and contact management services.
 */
public class Routes {
    public static final String DEFAULT_BASE_URL = "https://thinking-tester-contact-list.herokuapp.com";
    public static final String base_URL = System.getenv("BASE_URL") != null 
            ? System.getenv("BASE_URL") 
            : DEFAULT_BASE_URL;

    public static final String create_user_endpoint = "/users";
    public static final String user_login = "/users/login";
    public static final String add_contact = "/contacts";
    public static final String delete_contact = "/contacts";
}
```

---

### File: `src/test/java/ApiTest/SpecBuilders/RequestBuilder.java`

```java
package ApiTest.SpecBuilders;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static ApiTest.endpoints.Routes.base_URL;

/**
 * The RequestBuilder class provides methods to configure and build request specifications
 * for API testing.
 */
public class RequestBuilder {

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(base_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();
    }

    public static RequestSpecification requestSpecWithAuth(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(base_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
```

---

### File: `src/test/java/ApiTest/SpecBuilders/ResponseBuilder.java`

```java
package ApiTest.SpecBuilders;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

/**
 * The ResponseBuilder class provides utility methods to build response specifications
 * for testing API endpoints.
 */
public class ResponseBuilder {

    public static ResponseSpecification postResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification okResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
```

---

### File: `src/test/java/ApiTest/Utils/Environment.java`

```java
package ApiTest.Utils;

public class Environment {
    public static String token = System.getenv("AUTH_TOKEN") != null ? System.getenv("AUTH_TOKEN") : "";
}
```

---

### File: `src/test/java/ApiTest/Utils/UserFactory.java`

```java
package ApiTest.Utils;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserFactory {

    public static Map<String, String> getRandomUser() {
        Map<String, String> randomUser = new HashMap<>();

        Faker user = new Faker();
        FakeValuesService fakeValuesService = new FakeValuesService(Locale.ENGLISH, new RandomService());

        String email = System.getenv("TEST_USER_EMAIL") != null 
                ? System.getenv("TEST_USER_EMAIL") 
                : fakeValuesService.bothify("??????????#####@??????.com");

        String password = System.getenv("TEST_USER_PASSWORD") != null 
                ? System.getenv("TEST_USER_PASSWORD") 
                : fakeValuesService.bothify("?????#####");

        randomUser.put("firstName", user.name().firstName());
        randomUser.put("lastName", user.name().lastName());
        randomUser.put("email", email);
        randomUser.put("password", password);

        return randomUser;
    }
}
```

---

### File: `src/test/java/ApiTest/Utils/ContactFactory.java`

```java
package ApiTest.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContactFactory {
    private static final Logger LOGGER = Logger.getLogger(ContactFactory.class.getName());
    private static Properties props = new Properties();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readJsonData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File("src/test/resources/testdata.json"), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }

    public static String readToken() {
        String envToken = System.getenv("AUTH_TOKEN");
        if (envToken != null && !envToken.trim().isEmpty()) {
            return envToken;
        }

        try (InputStream input = ContactFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                return props.getProperty("token");
            } else {
                LOGGER.log(Level.WARNING, "Config file config.properties not found in classpath.");
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading config.properties file", e);
            return null;
        }
    }
}
```

---

### File: `src/test/java/ApiTest/Test/User/UserTest.java`

```java
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
                .body(matchesJsonSchemaInClasspath("schemas/user-created-schema.json"))
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
```

---

### File: `src/test/java/ApiTest/Test/Contact/ContactTest.java`

```java
package ApiTest.Test.Contact;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static ApiTest.SpecBuilders.RequestBuilder.requestSpecWithAuth;
import static ApiTest.SpecBuilders.ResponseBuilder.postResponse;
import static ApiTest.endpoints.Routes.add_contact;
import static ApiTest.endpoints.Routes.delete_contact;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * The ContactTest class contains unit tests for performing CRUD operations on contacts via API endpoints.
 */
public class ContactTest {
    private Map<String, Object> contactData;
    private String token;
    private String contactId;

    @BeforeClass
    public void setup() {
        contactData = readJsonData();
        token = readToken();
    }

    private Map<String, Object> readJsonData() {
        return ApiTest.Utils.ContactFactory.readJsonData();
    }

    private String readToken() {
        return ApiTest.Utils.ContactFactory.readToken();
    }

    @Test
    public void testCreateContact() {
        Response response = given()
                .spec(requestSpecWithAuth(token))
                .body(contactData)
                .when()
                .post(add_contact)
                .then()
                .spec(postResponse())
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/contact-created-schema.json"))
                .extract().response();

        Assert.assertEquals(contactData.get("firstName"), response.jsonPath().get("firstName"));
        contactId = response.jsonPath().get("_id");
    }

    @Test(dependsOnMethods = "testCreateContact")
    public void testDeleteContact() {
        Response deleteContactResponse = given()
                .spec(requestSpecWithAuth(token))
                .when()
                .delete(delete_contact + "/" + contactId)
                .then()
                .statusCode(200)
                .extract().response();

        String responseBody = deleteContactResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("Contact deleted"));
    }
}
```