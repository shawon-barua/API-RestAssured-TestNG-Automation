package ApiTest.Test.Contact;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

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
    
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec201;
    private ResponseSpecification responseSpec200;

    @BeforeClass
    public void setup() {
        contactData = readJsonData();
        token = System.getenv("API_TOKEN") != null ? System.getenv("API_TOKEN") : readToken();
        String baseUri = System.getenv("BASE_URI") != null ? System.getenv("BASE_URI") : "https://thinking-tester-contact-list.herokuapp.com";

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        responseSpec201 = new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType(ContentType.JSON)
                .build();

        responseSpec200 = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
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
                .spec(requestSpec)
                .body(contactData)
                .when()
                .post(add_contact)
                .then()
                .spec(responseSpec201)
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/contact-created-schema.json"))
                .extract().response();

        Assert.assertEquals(contactData.get("firstName"), response.jsonPath().get("firstName"));
        contactId = response.jsonPath().get("_id");
    }

    @Test(dependsOnMethods = "testCreateContact")
    public void testDeleteContact() {
        Response deleteContactResponse = given()
                .spec(requestSpec)
                .when()
                .delete(delete_contact + "/" + contactId)
                .then()
                .spec(responseSpec200)
                .statusCode(200)
                .extract().response();

        String responseBody = deleteContactResponse.getBody().asString();
        Assert.assertTrue(responseBody.contains("Contact deleted"));
    }
}
