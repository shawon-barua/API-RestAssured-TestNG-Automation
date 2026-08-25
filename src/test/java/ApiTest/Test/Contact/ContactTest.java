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