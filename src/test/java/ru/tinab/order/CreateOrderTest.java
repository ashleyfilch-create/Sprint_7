package ru.tinab.order;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Тестирование ручки создания заказа")
@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final List<String> color;
    private final OrderClient orderClient = new OrderClient();

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Создание заказа. Цвет = {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {Arrays.asList(Constants.COLOR_BLACK)},
                {Arrays.asList(Constants.COLOR_GREY)},
                {Arrays.asList(Constants.COLOR_BLACK, Constants.COLOR_GREY)},
                {null}
        };
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка успешного создания заказа с одним цветом, двумя цветами и без цвета")
    public void shouldCreateOrderSuccessfullyTest() {

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
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
}