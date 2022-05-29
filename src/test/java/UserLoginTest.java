import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.qameta.allure.Allure.step;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @Description("Логин пользователя")
    @DisplayName("Логин под существующим пользователем")
    @TmsLink("TMS-2.1")
    @Issue("BUG-2.1")
    public void userCanBeLoggedIn() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        userClient.createUser(user);

        // Act
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        int statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");

        // Assert
        assertThat("User cannot login", statusCode, equalTo(SC_OK));
        assertTrue("User is not logged-in", isUserLoggedIn);
    }

    @Test
    @Description("Логин пользователя")
    @DisplayName("Логин с неверным логином и паролем")
    @TmsLink("TMS-2.2")
    @Issue("BUG-2.2")
    public void userCannotBeLoggedInWithFakeData() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithFakeLoginAndPassword = new User(user.email+"1", user.password+"1", user.name );

        // Act
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(userWithFakeLoginAndPassword));
        int statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");

        // Assert
        assertThat("Status code doesn't match", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("User seems to be logged-in", isUserLoggedIn);
        assertEquals("Message doesn't match", "email or password are incorrect", message);
    }
}
