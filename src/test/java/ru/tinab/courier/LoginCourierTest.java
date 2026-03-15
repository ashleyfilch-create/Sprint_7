package ru.tinab.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tinab.client.CourierClient;
import ru.tinab.model.Courier;
import ru.tinab.model.CourierLogin;
import ru.tinab.utils.Constants;
import ru.tinab.utils.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Тестирование ручки логина курьера")
public class LoginCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        courierClient = new CourierClient();
        courier = CourierGenerator.randomCourier();

        createCourierStep(courier);
    }

    @Test
    @DisplayName("Курьер может залогиниться")
    @Description("Проверка успешного логина курьера")
    public void courierCanLoginTest() {

        CourierLogin login = new CourierLogin(courier.getLogin(), courier.getPassword());

        Response response = loginCourierStep(login);

        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());

        courierId = response.jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Ошибка при логине без логина")
    @Description("Проверка ошибки если не передан логин")
    public void cannotLoginWithoutLoginTest() {

        CourierLogin login = new CourierLogin(null, courier.getPassword());

        loginCourierStep(login)
                .then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Ошибка при логине без пароля")
    @Description("Проверка ошибки если не передан пароль")
    public void cannotLoginWithoutPasswordTest() {

        CourierLogin login = new CourierLogin(courier.getLogin(), null);

        loginCourierStep(login)
                .then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Ошибка при логине с неправильным паролем")
    @Description("Проверка ошибки при неправильном пароле")
    public void cannotLoginWithWrongPasswordTest() {

        CourierLogin login = new CourierLogin(courier.getLogin(), "wrong_password");

        loginCourierStep(login)
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @After
    public void tearDown() {

        try {
            courierId = getCourierIdStep(courier);

            if (courierId != null) {
                courierClient.deleteCourier(courierId);
            }

        } catch (Exception ignored) {}
    }

    // -------- Allure steps --------

    @Step("Создание курьера")
    private Response createCourierStep(Courier courier) {
        return courierClient.createCourier(courier);
    }

    @Step("Логин курьера")
    private Response loginCourierStep(CourierLogin login) {
        return courierClient.loginCourier(login);
    }

    @Step("Получение id курьера")
    private Integer getCourierIdStep(Courier courier) {

        CourierLogin login = new CourierLogin(courier.getLogin(), courier.getPassword());

        Response response = courierClient.loginCourier(login);

        if (response.statusCode() == SC_OK) {
            return response.jsonPath().getInt("id");
        }

        return null;
    }
}