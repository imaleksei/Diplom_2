import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import orders.OrderData;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient{

    private static final String ORDER_PATH = "api/orders/";

    @Step("Создаем пользователя с авторизацией")
    public ValidatableResponse createOrderWithAuth(String accessToken, Order order) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Создаем пользователя без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получаем данные об ингредиентах")
    public ValidatableResponse getAllIngredientsData() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get("/api/ingredients")
                .then();
    }

    @Step("Получаем заказы конкретного пользователя")
    public ValidatableResponse getOrderDataForUserWithAuth(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получаем заказы без токена")
    public ValidatableResponse getOrderDataWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Десериализуем заказы конкретного пользователя")
    public OrderData orderData (String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .body().as(OrderData.class);
    }


}
