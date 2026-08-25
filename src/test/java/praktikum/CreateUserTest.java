package praktikum;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest {

    private UserApi userApi = new UserApi();
    private String accessToken;

    private User getNewUser() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        return new User(email, "123456", "Max");
    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    public void createUniqueUser() {
        User user = getNewUser();

        Response response = userApi.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    public void createUserThatAlreadyExists() {
        User user = getNewUser();

        Response firstResponse = userApi.createUser(user);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userApi.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void createUserWithoutName() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(email, "123456", null);

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void createUserWithoutEmail() {
        User user = new User(null, "123456", "Max");

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void createUserWithoutPassword() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(email, null, "Max");

        Response response = userApi.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}