
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
            return objectMapper.readValue(new File("src/test/resources/schemas/schemas/user-created-schema.json"), Map.class);
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
