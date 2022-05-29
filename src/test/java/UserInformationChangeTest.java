import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static io.qameta.allure.Allure.step;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserInformationChangeTest {

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
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @TmsLink("TMS-3.1.1")
    @Issue("BUG-3.1.1")
    public void userEmailCanBeChangedWithAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedEmail = new User(user.email + "1", user.password, user.name);

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo(accessToken, userWithChangedEmail);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewEmail = patchUserInfoResponse.extract().path("user");

        // Assert
        assertThat("User info not changed", statusCode, equalTo(SC_OK));
        assertTrue("User info not changed", isUserInfoChanged);
        assertTrue(userNewEmail.containsValue((user.email + "1").toLowerCase()));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @TmsLink("TMS-3.1.2")
    @Issue("BUG-3.1.2")
    public void userPasswordCanBeChangedWithAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedPassword = new User(user.email, user.password + "1", user.name);

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo(accessToken, userWithChangedPassword);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewPassword = patchUserInfoResponse.extract().path("user");

        // Assert
        assertThat("User info not changed", statusCode, equalTo(SC_OK));
        assertTrue("User info not changed", isUserInfoChanged);
        assertTrue(userNewPassword.containsValue(user.name));
        assertTrue(userNewPassword.containsValue((user.email).toLowerCase()));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("С авторизацией")
    @TmsLink("TMS-3.1.3")
    @Issue("BUG-3.1.3")
    public void userNameCanBeChangedWithAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        User userWithChangedName = new User(user.email, user.password, user.name + "1");

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo(accessToken, userWithChangedName);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        LinkedHashMap userNewName = patchUserInfoResponse.extract().path("user");

        // Assert
        assertThat("User info not changed", statusCode, equalTo(SC_OK));
        assertTrue("User info not changed", isUserInfoChanged);
        assertTrue(userNewName.containsValue(user.name + "1"));
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @TmsLink("TMS-3.2.1")
    @Issue("BUG-3.2.1")
    public void userEmailCannotBeChangedWithoutAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedEmail = new User(user.email + "1", user.password, user.name);

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo("", userWithChangedEmail);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        // Assert
        assertThat("User info changed", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("User info changed", isUserInfoChanged);
        assertEquals("Message doesn't match", "You should be authorised", message);
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @TmsLink("TMS-3.2.2")
    @Issue("BUG-3.2.2")
    public void userPasswordCannotBeChangedWithoutAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedPassword = new User(user.email, user.password + "1", user.name);

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo("", userWithChangedPassword);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        // Assert
        assertThat("User info changed", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("User info changed", isUserInfoChanged);
        assertEquals("Message doesn't match", "You should be authorised", message);
    }

    @Test
    @Description("Изменение данных пользователя")
    @DisplayName("Без авторизации")
    @TmsLink("TMS-3.2.3")
    @Issue("BUG-3.2.3")
    public void userNameCannotBeChangedWithoutAuth() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");
        User userWithChangedName = new User(user.email, user.password, user.name + "1");

        // Act
        ValidatableResponse patchUserInfoResponse = userClient.patchUserInfo("", userWithChangedName);
        int statusCode = patchUserInfoResponse.extract().statusCode();
        boolean isUserInfoChanged = patchUserInfoResponse.extract().path("success");
        String message = patchUserInfoResponse.extract().path("message");

        // Assert
        assertThat("User info changed", statusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("User info changed", isUserInfoChanged);
        assertEquals("Message doesn't match", "You should be authorised", message);
    }
}
