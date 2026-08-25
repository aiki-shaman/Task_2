package praktikum;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private UserApi userApi = new UserApi();
    private String accessToken;

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    public void loginExistingUser() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        String password = "123456";

        User user = new User(email, password, "Max");

        Response createResponse = userApi.createUser(user);
        accessToken = createResponse.path("accessToken");

        LoginUser loginUser = new LoginUser(email, password);

        Response response = userApi.loginUser(loginUser);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("Max"))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    public void loginWithWrongEmailAndPassword() {
        LoginUser loginUser = new LoginUser(
                "wrong" + System.currentTimeMillis() + "@mail.ru",
                "wrongpassword"
        );

        Response response = userApi.loginUser(loginUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}