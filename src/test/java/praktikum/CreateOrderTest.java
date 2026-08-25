package praktikum;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private String accessToken;

    private List<String> getIngredients() {
        Response response = orderApi.getIngredients();

        String firstIngredient = response.path("data[0]._id");
        String secondIngredient = response.path("data[1]._id");

        return List.of(firstIngredient, secondIngredient);
    }

    private void createUser() {
        String email = "user" + System.currentTimeMillis() + "@mail.ru";
        User user = new User(email, "123456", "Max");

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
    public void createOrderWithAuthorizationAndIngredients() {
        createUser();
        List<String> ingredients = getIngredients();

        Response response = orderApi.createOrder(ingredients, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithoutAuthorizationAndWithIngredients() {
        List<String> ingredients = getIngredients();

        Response response = orderApi.createOrderWithoutAuth(ingredients);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithoutIngredients() {
        createUser();

        Response response = orderApi.createOrderWithoutIngredients(accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithWrongIngredientHash() {
        List<String> ingredients = List.of("wrongIngredientHash");

        Response response = orderApi.createOrderWithoutAuth(ingredients);

        response.then()
                .statusCode(500)
                .body(not(emptyOrNullString()));
    }
}