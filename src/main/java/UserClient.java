import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {
    private static final String AUTH_PATH = "api/auth/";

    @Step("Создаем пользователя {user}")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(AUTH_PATH+"register")
                .then();
    }

    @Step("Удаляем пользователя с {accessToken}")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_PATH+"user")
                .then();
    }

    @Step("Логним пользователя с {credentials}")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(AUTH_PATH+"login")
                .then();
    }

    @Step("Вносим изменения в информацию о пользователе с {accessToken} , {user}")
    public ValidatableResponse patchUserInfo(String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(AUTH_PATH+"user")
                .then();
    }

}