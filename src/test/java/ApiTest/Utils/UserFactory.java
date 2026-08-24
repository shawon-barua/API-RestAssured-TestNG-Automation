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
