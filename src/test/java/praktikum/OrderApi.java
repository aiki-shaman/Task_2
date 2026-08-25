package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    @Step("Get ingredients")
    public Response getIngredients() {
        return given()
                .when()
                .get(BASE_URL + "/api/ingredients");
    }

    @Step("Create order with authorization")
    public Response createOrder(List<String> ingredients, String accessToken) {
        Map<String, List<String>> body = Map.of("ingredients", ingredients);

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post(BASE_URL + "/api/orders");
    }

    @Step("Create order without authorization")
    public Response createOrderWithoutAuth(List<String> ingredients) {
        Map<String, List<String>> body = Map.of("ingredients", ingredients);

        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post(BASE_URL + "/api/orders");
    }

    @Step("Create order without ingredients")
    public Response createOrderWithoutIngredients(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body("{}")
                .when()
                .post(BASE_URL + "/api/orders");
    }

    @Step("Get user orders")
    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URL + "/api/orders");
    }

    @Step("Get user orders without authorization")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .when()
                .get(BASE_URL + "/api/orders");
    }
}