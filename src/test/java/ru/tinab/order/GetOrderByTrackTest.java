package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import ru.tinab.client.OrderClient;
import ru.tinab.model.Order;
import ru.tinab.utils.Constants;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Тестирование ручки получения заказа по трек-номеру")
public class GetOrderByTrackTest {

    private OrderClient orderClient;
    private int validTrack;
    private final int invalidTrack = 99999999;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        orderClient = new OrderClient();

        Order order = new Order(
                "Naruto",
                "Uzumaki",
                "Kanoha, 142 apt.",
                "1",
                "+7 800 355 35 35",
                5,
                "2026-03-12",
                Constants.COLOR_BLACK
        );

        Response createResponse = orderClient.createOrder(order);

        createResponse.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());

        validTrack = createResponse.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Получение заказа по корректному трек-номеру")
    @Description("Проверка успешного получения заказа по валидному трек-номеру")
    public void shouldGetOrderByValidTrackTest() {

        Response response = getOrderByTrackStep(validTrack);

        response.then()
                .statusCode(SC_OK)
                .body("order", notNullValue())
                .body("order.track", equalTo(validTrack));
    }

    @Test
    @DisplayName("Ошибка при запросе без трек-номера")
    @Description("Проверка ошибки при попытке получить заказ без передачи трек-номера")
    public void shouldReturn400IfTrackNotProvidedTest() {

        Response response = getOrderByTrackStepMissing();

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка при использовании несуществующего трек-номера")
    @Description("Проверка ошибки при запросе заказа с несуществующим трек-номером")
    public void shouldReturn404IfTrackNotFoundTest() {

        Response response = getOrderByTrackStep(invalidTrack);

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }

    // ---------------------- Шаги Allure ----------------------

    @Step("Получение заказа по трек-номеру {track}")
    private Response getOrderByTrackStep(int track) {
        return orderClient.getOrderByTrack(track);
    }

    @Step("Попытка получения заказа без трек-номера")
    private Response getOrderByTrackStepMissing() {
        return orderClient.getOrderByTrack(0);
    }
}