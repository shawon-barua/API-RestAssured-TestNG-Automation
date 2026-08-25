
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
