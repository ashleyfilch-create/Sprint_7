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

@Epic("Тестирование ручки удаления курьера")
public class DeleteCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        courierClient = new CourierClient();
        courier = CourierGenerator.randomCourier();
        Response response = createCourierStep(courier);
        response.then().statusCode(201).body("ok", equalTo(true));
        courierId = getCourierIdStep(courier);
    }

    @Test
    @Description("Успешное удаление курьера")
    public void deleteCourierSuccessfully() {
        Response response = deleteCourierStep(courierId);
        response.then().statusCode(200)
                .body("ok", equalTo(true));
        courierId = null;
    }

    @Test
    @Description("Попытка удаления курьера без id")
    public void deleteCourierWithoutId() {
        Response response = givenDeleteWithoutId();
        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @Description("Попытка удаления курьера с несуществующим id")
    public void deleteCourierNonExistentId() {
        int fakeId = 999999;
        Response response = deleteCourierStep(fakeId);
        response.then().statusCode(404)
                .body("message", equalTo("Курьера с таким id нет"));
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

    @Step("Удаление курьера с id: {id}")
    private Response deleteCourierStep(Integer id) {
        return courierClient.deleteCourier(id);
    }

    @Step("Удаление курьера без id")
    private Response givenDeleteWithoutId() {
        return io.restassured.RestAssured.given()
                .contentType("application/json")
                .body("{}")
                .delete(Constants.DELETE_COURIER);
    }

    @Step("Мягкое удаление курьера с id: {id}")
    private void deleteCourierStepSafe(Integer id) {
        try {
            courierClient.deleteCourier(id);
        } catch (Exception ignored) {}
    }
}