package praktikum;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

    private UserApi userApi = new UserApi();
    private String accessToken;
    private String email;
    private String password;

    @BeforeEach
    public void createUser() {
        email = "user" + System.currentTimeMillis() + "@mail.ru";
        password = "123456";

        User user = new User(email, password, "Max");

        Response response = userApi.createUser(user);
        accessToken = response.path("accessToken");
    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    public void updateUserNameWithAuthorization() {
        Map<String, String> data = Map.of("name", "Alex");

        Response response = userApi.updateUser(data, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo("Alex"));
    }

    @Test
    public void updateUserEmailWithAuthorization() {
        String newEmail = "new" + System.currentTimeMillis() + "@mail.ru";
        Map<String, String> data = Map.of("email", newEmail);

        Response response = userApi.updateUser(data, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    public void updateUserPasswordWithAuthorization() {
        String newPassword = "654321";
        Map<String, String> data = Map.of("password", newPassword);

        Response response = userApi.updateUser(data, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));

        LoginUser loginUser = new LoginUser(email, newPassword);
        Response loginResponse = userApi.loginUser(loginUser);

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        accessToken = loginResponse.path("accessToken");
    }

    @Test
    public void updateUserNameWithoutAuthorization() {
        Map<String, String> data = Map.of("name", "Alex");

        Response response = userApi.updateUserWithoutAuth(data);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    public void updateUserEmailWithoutAuthorization() {
        Map<String, String> data = Map.of("email", "new@mail.ru");

        Response response = userApi.updateUserWithoutAuth(data);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    public void updateUserPasswordWithoutAuthorization() {
        Map<String, String> data = Map.of("password", "654321");

        Response response = userApi.updateUserWithoutAuth(data);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}