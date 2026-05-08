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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

@Epic("Тестирование ручки создания курьера")
public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        courierClient = new CourierClient();
        courier = CourierGenerator.randomCourier();
    }

    @Test
    @DisplayName("Курьер может быть создан")
    @Description("Проверка, что курьера можно создать")
    public void courierCanBeCreatedTest() {

        Response response = createCourierStep(courier);

        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourierTest() {

        Response firstResponse = createCourierStep(courier);

        firstResponse.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        Response secondResponse = createCourierStep(courier);

        secondResponse.then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    @Description("Проверка, что нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLoginTest() {

        Courier courierWithoutLogin = new Courier(null, "1234", "Test");

        createCourierStep(courierWithoutLogin)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPasswordTest() {

        String login = "test_" + System.currentTimeMillis();
        Courier courierWithoutPassword = new Courier(login, null, "Test");

        createCourierStep(courierWithoutPassword)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {

        try {
            courierId = getCourierIdStep(courier);

            if (courierId != null) {
                deleteCourierStepSafe(courierId);
            }

        } catch (Exception ignored) {}
    }

    // ---------------------- Шаги для Allure ----------------------

    @Step("Создание курьера: {courier.login}")
    private Response createCourierStep(Courier courier) {
        return courierClient.createCourier(courier);
    }

    @Step("Получение id курьера: {courier.login}")
    private Integer getCourierIdStep(Courier courier) {

        CourierLogin login = new CourierLogin(courier.getLogin(), courier.getPassword());

        Response loginResponse = courierClient.loginCourier(login);

        assertEquals(SC_OK, loginResponse.statusCode());

        return loginResponse.jsonPath().getInt("id");
    }

    @Step("Мягкое удаление курьера с id: {id}")
    private void deleteCourierStepSafe(Integer id) {
        try {
            courierClient.deleteCourier(id);
        } catch (Exception ignored) {}
    }
}