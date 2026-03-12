package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.tinab.utils.Constants;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестирование ручки принятия заказа")
public class AcceptOrderTest {

    private int existingOrderId = 1; // здесь можно поставить реальный существующий id заказа
    private int existingCourierId = 1; // здесь можно поставить реальный id курьера
    private int nonExistentOrderId = 999999;
    private int nonExistentCourierId = 999999;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
    }

    @Test
    @Description("Успешное принятие заказа")
    public void acceptOrderSuccessfully() {
        Response response = acceptOrder(existingOrderId, existingCourierId);
        response.then().statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Запрос без id заказа")
    public void acceptOrderWithoutOrderId() {
        Response response = given()
                .contentType("application/json")
                .queryParam("courierId", existingCourierId)
                .put(Constants.ACCEPT_ORDER + ""); // пустой id
        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Запрос с несуществующим id заказа")
    public void acceptOrderWithNonExistentOrderId() {
        Response response = acceptOrder(nonExistentOrderId, existingCourierId);
        response.then().statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Test
    @Description("Запрос с несуществующим id курьера")
    public void acceptOrderWithNonExistentCourierId() {
        Response response = acceptOrder(existingOrderId, nonExistentCourierId);
        response.then().statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @Description("Запрос без id курьера")
    public void acceptOrderWithoutCourierId() {
        Response response = given()
                .contentType("application/json")
                .put(Constants.ACCEPT_ORDER + existingOrderId);
        response.then().statusCode(400)
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