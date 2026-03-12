package ru.tinab.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.tinab.utils.Constants;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {

    static {
        RestAssured.baseURI = Constants.BASE_URL; // обязательно укажи базовый URL
    }

    private static final String CREATE_ORDER = Constants.CREATE_ORDER;
    private static final String GET_ORDERS = Constants.GET_ORDERS;
    private static final String ACCEPT_ORDER = Constants.ACCEPT_ORDER;
    private static final String GET_ORDER_BY_ID = Constants.GET_ORDER_BY_ID;

    @Step("Создание заказа")
    public Response createOrder(Object order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get(GET_ORDERS);
    }

    @Step("Получение списка заказов с параметрами")
    public Response getOrders(Map<String, Object> queryParams) {
        return given()
                .header("Content-type", "application/json")
                .queryParams(queryParams)
                .get(GET_ORDERS);
    }

    @Step("Принять заказ с id {orderId} курьером {courierId}")
    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .put(ACCEPT_ORDER + orderId);
    }

    @Step("Получить заказ по трек-номеру {track}")
    public Response getOrderByTrack(int track) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("t", track)
                .get(GET_ORDER_BY_ID);
    }
}