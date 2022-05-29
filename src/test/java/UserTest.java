import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;

import static io.qameta.allure.Allure.step;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        try{
        userClient.deleteUser(accessToken);
        } catch(java.lang.IllegalArgumentException error) {
            //client doesnt exist
        }
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать уникального пользователя")
    @TmsLink("TMS-1.1")
    @Issue("BUG-1.1")
    public void userCanBeCreated() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();

        // Act
        ValidatableResponse createResponse = userClient.createUser(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        accessToken = createResponse.extract().path("accessToken");

        // Assert
        assertThat("User cannot create", statusCode, equalTo(SC_OK));
        assertTrue("User is not created", isUserCreated);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @TmsLink("TMS-1.2")
    @Issue("BUG-1.2")
    public void userThatExistsCannotBeCreated() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = createResponse.extract().path("accessToken");

        // Act
        ValidatableResponse secondCreateResponse = userClient.createUser(user);
        int statusCode = secondCreateResponse.extract().statusCode();
        boolean isUserCreated = secondCreateResponse.extract().path("success");
        String message = secondCreateResponse.extract().path("message");

        // Assert
        assertThat("User could be created", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("User created", isUserCreated);
        assertEquals("Message doesn't match", "User already exists", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @TmsLink("TMS-1.3.1")
    @Issue("BUG-1.3.1")
    public void userCannotBeCreatedWithoutEmail() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        User userWithoutEmail = new User("", user.password, user.name);

        // Act
        ValidatableResponse createResponse = userClient.createUser(userWithoutEmail);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        // Assert
        assertThat("User could be created", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("User created", isUserCreated);
        assertEquals("Message doesn't match", "Email, password and name are required fields", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @TmsLink("TMS-1.3.2")
    @Issue("BUG-1.3.2")
    public void userCannotBeCreatedWithoutPassword() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        User userWithoutPassword = new User(user.email, "", user.name);

        // Act
        ValidatableResponse createResponse = userClient.createUser(userWithoutPassword);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        // Assert
        assertThat("User could be created", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("User created", isUserCreated);
        assertEquals("Message doesn't match", "Email, password and name are required fields", message);
    }

    @Test
    @Description("Создание пользователя")
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей.")
    @TmsLink("TMS-1.3.3")
    @Issue("BUG-1.3.3")
    public void userCannotBeCreatedWithoutName() {
        // Arrange
        step("Готовим тестовые данные");
        User user = User.getRandom();
        User userWithoutName = new User(user.email, user.password, "");

        // Act
        ValidatableResponse createResponse = userClient.createUser(userWithoutName);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");

        // Assert
        assertThat("User could be created", statusCode, equalTo(SC_FORBIDDEN));
        assertFalse("User created", isUserCreated);
        assertEquals("Message doesn't match", "Email, password and name are required fields", message);
    }
}
