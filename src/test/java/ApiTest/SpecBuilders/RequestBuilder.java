package ApiTest.SpecBuilders;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * The RequestBuilder class provides methods to configure and build request specifications
 * and response specifications for API testing.
 */
public class RequestBuilder {

    private static final String BASE_URL = System.getenv("BASE_URL") != null 
            ? System.getenv("BASE_URL") 
            : ApiTest.endpoints.Routes.base_URL;

    private static final String AUTH_TOKEN = System.getenv("AUTH_TOKEN") != null 
            ? System.getenv("AUTH_TOKEN") 
            : "";

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();
    }

    public static RequestSpecification requestSpecWithAuth(String token) {
        String effectiveToken = (token != null && !token.isEmpty()) ? token : AUTH_TOKEN;
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + effectiveToken)
                .build();
    }

    public static ResponseSpecification responseSpec(int expectedStatusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpecWithSchema(int expectedStatusCode, String schemaPath) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .expectContentType(ContentType.JSON)
                .expectBody(matchesJsonSchemaInClasspath(schemaPath))
                .build();
    }
}
