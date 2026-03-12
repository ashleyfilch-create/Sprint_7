package ru.tinab.order;

import io.restassured.response.Response;
import org.junit.Test;
import ru.tinab.client.OrderClient;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    public void shouldGetListOfOrders() {
        Response response = orderClient.getOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue());
    }

    @Test
    public void shouldGetOrdersForSpecificCourier() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", 1);

        Response response = orderClient.getOrders(params);

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    public void shouldReturn404ForNonExistentCourier() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", 99999); // несуществующий ID

        Response response = orderClient.getOrders(params);

        response.then()
                .statusCode(404)
                .body("message", containsString("не найден"));
    }
}