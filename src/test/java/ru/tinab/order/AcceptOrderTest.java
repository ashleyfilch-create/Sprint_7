package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.tinab.utils.Constants;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестирование ручки принятия заказа")
public class AcceptOrderTest {

    private int existingOrderId = 1;
    private int existingCourierId = 1;
    private int nonExistentOrderId = 999999;
    private int nonExistentCourierId = 999999;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
    }

    @Test
    @DisplayName("Успешное принятие заказа")
    @Description("Проверка успешного принятия заказа курьером")
    public void acceptOrderSuccessfullyTest() {

        Response response = acceptOrder(existingOrderId, existingCourierId);

        response.then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Запрос без id заказа")
    @Description("Проверка ошибки при отсутствии id заказа")
    public void acceptOrderWithoutOrderIdTest() {

        Response response = given()
                .contentType("application/json")
                .queryParam("courierId", existingCourierId)
                .put(Constants.ACCEPT_ORDER + "");

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Запрос с несуществующим id заказа")
    @Description("Проверка ошибки при использовании несуществующего id заказа")
    public void acceptOrderWithNonExistentOrderIdTest() {

        Response response = acceptOrder(nonExistentOrderId, existingCourierId);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Test
    @DisplayName("Запрос с несуществующим id курьера")
    @Description("Проверка ошибки при использовании несуществующего id курьера")
    public void acceptOrderWithNonExistentCourierIdTest() {

        Response response = acceptOrder(existingOrderId, nonExistentCourierId);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Запрос без id курьера")
    @Description("Проверка ошибки при отсутствии id курьера")
    public void acceptOrderWithoutCourierIdTest() {

        Response response = given()
                .contentType("application/json")
                .put(Constants.ACCEPT_ORDER + existingOrderId);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    // ---------------------- Шаги для Allure ----------------------

    @Step("Принять заказ {orderId} курьером {courierId}")
    private Response acceptOrder(int orderId, int courierId) {
        return given()
                .contentType("application/json")
                .queryParam("courierId", courierId)
                .put(Constants.ACCEPT_ORDER + orderId);
    }
}