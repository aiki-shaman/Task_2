package praktikum;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetUserOrdersTest {

    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private String accessToken;

    private void createUser() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(email, "123456", "Max");

        Response response = userApi.createUser(user);
        accessToken = response.path("accessToken");
    }

    private List<String> getIngredients() {
        Response response = orderApi.getIngredients();

        String firstIngredient = response.path("data[0]._id");
        String secondIngredient = response.path("data[1]._id");

        return List.of(firstIngredient, secondIngredient);
    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    public void getOrdersAuthorizedUser() {
        createUser();

        List<String> ingredients = getIngredients();
        orderApi.createOrder(ingredients, accessToken);

        Response response = orderApi.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    public void getOrdersUnauthorizedUser() {
        Response response = orderApi.getUserOrdersWithoutAuth();

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}