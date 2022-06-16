import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderCreatingTest {

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
    @DisplayName("Создание заказа")
    @Description("C авторизацией, с ингредиентами")
    @TmsLink("TMS-4.1")
    @Issue("BUG-4.1")
    public void creatingOrderWithAuth() {
        //Arrange
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));

        // Act
        ValidatableResponse createOrderResponse = orderClient.createOrderWithAuth(accessToken, order);
        int statusCode = createOrderResponse.extract().statusCode();
        String name = createOrderResponse.extract().path("name");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        //Assert
        assertEquals("Status code doesn't match", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order not created", isOrderCreated);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Без авторизацией, с ингредиентами")
    @TmsLink("TMS-4.2")
    @Issue("BUG-4.2")
    public void creatingOrderWithoutAuth() {
        Order order = new Order();
        order.setIngredients(Arrays.asList(firstIngredient));

        ValidatableResponse createOrderResponse = orderClient.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();
        String name = createOrderResponse.extract().path("name");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        assertEquals("Status code doesn't match", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order not created", isOrderCreated);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Без ингредиентов")
    @TmsLink("TMS-4.3")
    @Issue("BUG-4.3")
    public void creatingOrderWithoutIngredient() {
        Order order = new Order();

        ValidatableResponse createOrderResponse = orderClient.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();
        String message = createOrderResponse.extract().path("message");
        boolean isOrderCreated = createOrderResponse.extract().path("success");

        assertEquals("Status code doesn't match", 400, statusCode);
        assertFalse("Order seems to be created", isOrderCreated);
        assertEquals("Error message doesn't match", "Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("С неверным хешем ингредиентов")
    @TmsLink("TMS-4.4")
    @Issue("BUG-4.4")
    public void creatingOrderWithWrongHashes() {
        String wrongHashes = Order.getSomeRandomIngredientHash();
        Order order = new Order();
        order.setIngredients(Arrays.asList(wrongHashes));

        ValidatableResponse createOrderResponse = orderClient.createOrderWithoutAuth(order);
        int statusCode = createOrderResponse.extract().statusCode();

        assertEquals("Status code doesn't match", 500, statusCode);
    }

}
