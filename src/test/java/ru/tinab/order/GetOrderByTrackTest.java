package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tinab.client.OrderClient;
import ru.tinab.model.Order;
import ru.tinab.utils.Constants;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Тестирование ручки получения заказа по трек-номеру")
public class GetOrderByTrackTest {

    private OrderClient orderClient;
    private int validTrack;
    private int invalidTrack = 99999999; // несуществующий номер
    private int createdOrderId;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
        orderClient = new OrderClient();

        // Создаём заказ и сохраняем трек
        Order order = new Order("Naruto", "Uzumaki", "Kanoha, 142 apt.",
                "1", "+7 800 355 35 35", 5, "2026-03-12", Constants.COLOR_BLACK);
        Response createResponse = orderClient.createOrder(order);

        createResponse.then().statusCode(201)
                .body("track", notNullValue());

        validTrack = createResponse.jsonPath().getInt("track");
        createdOrderId = validTrack; // если track == id заказа, иначе можно хранить отдельно
    }

    @After
    public void tearDown() {
    }

    @Test
    @Description("Успешное получение заказа по трек-номеру")
    public void shouldGetOrderByValidTrack() {
        Response response = getOrderByTrackStep(validTrack);

        response.then().statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(validTrack));
    }

    @Test
    @Description("Ошибка при запросе без трек-номера")
    public void shouldReturn400IfTrackNotProvided() {
        Response response = getOrderByTrackStepMissing();

        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Ошибка при запросе с несуществующим трек-номером")
    public void shouldReturn404IfTrackNotFound() {
        Response response = getOrderByTrackStep(invalidTrack);

        response.then().statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }

    // ---------------------- Шаги для Allure ----------------------

    @Step("Получение заказа по трек-номеру {track}")
    private Response getOrderByTrackStep(int track) {
        return orderClient.getOrderByTrack(track);
    }

    @Step("Попытка получения заказа без трек-номера")
    private Response getOrderByTrackStepMissing() {
        return orderClient.getOrderByTrack(0); // или передать null/пустое значение, если API поддерживает
    }
}