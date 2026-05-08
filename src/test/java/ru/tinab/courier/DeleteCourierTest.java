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
import static org.apache.http.HttpStatus.*;
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
        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        courierId = getCourierIdStep(courier);
    }

    @Test
    @Description("Проверка, что курьер может быть успешно удалён")
    public void deleteCourierSuccessfullyTest() {

        Response response = deleteCourierStep(courierId);

        response.then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));

        courierId = null;
    }

    @Test
    @Description("Проверка, что нельзя удалить курьера без передачи id")
    public void deleteCourierWithoutIdTest() {

        Response response = givenDeleteWithoutId();

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @Description("Проверка удаления курьера с несуществующим id")
    public void deleteCourierNonExistentIdTest() {

        int fakeId = 999999;

        Response response = deleteCourierStep(fakeId);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id нет"));
    }

    @Test
    @Description("Проверка, что авторизация без пароля невозможна")
    public void loginCourierWithoutPasswordTest() {

        CourierLogin login = new CourierLogin(courier.getLogin(), null);

        Response response = courierClient.loginCourier(login);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Проверка, что авторизация с неверным паролем невозможна")
    public void loginCourierWithWrongPasswordTest() {

        CourierLogin login = new CourierLogin(courier.getLogin(), "wrongPassword");

        Response response = courierClient.loginCourier(login);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourierStepSafe(courierId);
        }
    }

    // ---------------------- Шаги Allure ----------------------

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

    @Step("Удаление курьера с id: {id}")
    private Response deleteCourierStep(Integer id) {
        return courierClient.deleteCourier(id);
    }

    @Step("Удаление курьера без id")
    private Response givenDeleteWithoutId() {
        return RestAssured.given()
                .contentType("application/json")
                .body("{}")
                .delete(Constants.DELETE_COURIER);
    }

    @Step("Мягкое удаление курьера с id: {id}")
    private void deleteCourierStepSafe(Integer id) {
        try {
            courierClient.deleteCourier(id);
        } catch (Exception ignored) {
        }
    }
}