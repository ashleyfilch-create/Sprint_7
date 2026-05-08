package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.tinab.client.OrderClient;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("Тестирование ручки получения списка заказов")
public class GetOrdersTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка всех заказов")
    @Description("Проверка успешного получения списка всех заказов")
    public void shouldGetListOfOrdersTest() {
        Response response = orderClient.getOrders();

        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов для конкретного курьера")
    @Description("Проверка успешного получения заказов для существующего курьера")
    public void shouldGetOrdersForSpecificCourierTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", 1);

        Response response = orderClient.getOrders(params);

        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при запросе заказов несуществующего курьера")
    @Description("Проверка ошибки при попытке получить заказы для несуществующего курьера")
    public void shouldReturn404ForNonExistentCourierTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", 99999);

        Response response = orderClient.getOrders(params);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("не найден"));
    }
}