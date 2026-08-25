package ApiTest.Utils;

public class Environment {
    public static String token = System.getenv("AUTH_TOKEN") != null ? System.getenv("AUTH_TOKEN") : "";
}
