package ru.tinab.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
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
    @Description("Проверка, что курьера можно создать")
    public void courierCanBeCreated() {
        Response response = createCourierStep(courier);

        response.then().statusCode(201)
                .body("ok", equalTo(true));

        courierId = getCourierIdStep(courier); // здесь падаем, если что-то не так
    }

    @Test
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        // первый раз создаём
        Response firstResponse = createCourierStep(courier);
        firstResponse.then().statusCode(201)
                .body("ok", equalTo(true));
        courierId = getCourierIdStep(courier);

        // второй раз — дубликат
        Response secondResponse = createCourierStep(courier);
        secondResponse.then().statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @Description("Проверка, что нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "1234", "Test");

        createCourierStep(courierWithoutLogin)
                .then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        String login = "test_" + System.currentTimeMillis();
        Courier courierWithoutPassword = new Courier(login, null, "Test");

        createCourierStep(courierWithoutPassword)
                .then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourierStepSafe(courierId);
        }
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
        assertEquals(200, loginResponse.statusCode());
        return loginResponse.jsonPath().getInt("id");
    }

    @Step("Мягкое удаление курьера с id: {id}")
    private void deleteCourierStepSafe(Integer id) {
        try {
            Response response = courierClient.deleteCourier(id);
        } catch (Exception ignored) {}
    }
}