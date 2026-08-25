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