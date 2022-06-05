import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.OrderData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GetUserOrderDataTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        ValidatableResponse ingredientsResponse = orderClient.getAllIngredientsData();
        firstIngredient = ingredientsResponse.extract().path("data[0]._id");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Авторизованный пользователь")
    @TmsLink("TMS-5.1")
    @Issue("BUG-5.1")
    public void getOrderForUserWithAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));
        orderClient.createOrderWithAuth(accessToken, order);
        String[] expected = {firstIngredient};

        ValidatableResponse getOrderResponse = orderClient.getOrderDataForUserWithAuth(accessToken);
        int statusCode = getOrderResponse.extract().statusCode();
        boolean isOrderGot = getOrderResponse.extract().path("success");
        OrderData orderData = orderClient.orderData(accessToken);

        assertEquals("Status code doesn't match", 200, statusCode);
        assertTrue("Orders didn't got", isOrderGot);
        assertThat("Order List is empty", orderData.getOrders(), is(not(empty())));
        assertThat("Order Hash doesn't match", orderData.getOrders().get(0).getIngredients(), equalTo(expected));
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Неавторизованный пользователь")
    @TmsLink("TMS-5.2")
    @Issue("BUG-5.2")
    public void getOrderForUserWithoutAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));
        orderClient.createOrderWithAuth(accessToken, order);

        ValidatableResponse getOrderResponse = orderClient.getOrderDataWithoutAuth();
        int statusCode = getOrderResponse.extract().statusCode();
        boolean isOrderGot = getOrderResponse.extract().path("success");
        String message = getOrderResponse.extract().path("message");

        assertEquals("Status code doesn't match", 401, statusCode);
        assertFalse("Orders got normally", isOrderGot);
        assertEquals("Error message not matched", "You should be authorised", message);
    }
}