package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApi {

    private static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    @Step("Create user")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/api/auth/register");
    }

    @Step("Login user")
    public Response loginUser(LoginUser loginUser) {
        return given()
                .header("Content-type", "application/json")
                .body(loginUser)
                .when()
                .post(BASE_URL + "/api/auth/login");
    }

    @Step("Update user")
    public Response updateUser(Map<String, String> data, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(data)
                .when()
                .patch(BASE_URL + "/api/auth/user");
    }

    @Step("Update user without authorization")
    public Response updateUserWithoutAuth(Map<String, String> data) {
        return given()
                .header("Content-type", "application/json")
                .body(data)
                .when()
                .patch(BASE_URL + "/api/auth/user");
    }

    @Step("Delete user")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + "/api/auth/user");
    }
}