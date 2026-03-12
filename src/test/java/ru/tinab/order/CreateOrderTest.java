package ru.tinab.order;

import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.tinab.BaseTest;
import ru.tinab.client.OrderClient;
import ru.tinab.model.Order;
import ru.tinab.utils.Constants;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final List<String> color;
    private final OrderClient orderClient = new OrderClient();

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {Arrays.asList(Constants.COLOR_BLACK)},
                {Arrays.asList(Constants.COLOR_GREY)},
                {Arrays.asList(Constants.COLOR_BLACK, Constants.COLOR_GREY)},
                {null}
        };
    }

    @Test
    public void shouldCreateOrderSuccessfully() {

        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha 1",
                4,
                "+79998887766",
                5,
                "2026-06-06",
                "test order",
                color
        );

        Response response = orderClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}