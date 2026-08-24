package ApiTest.SpecBuilders;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static ApiTest.endpoints.Routes.base_URL;


**/
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