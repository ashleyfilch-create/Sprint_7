package ru.tinab.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.tinab.model.Courier;
import ru.tinab.model.CourierLogin;
import ru.tinab.utils.Constants;

import static io.restassured.RestAssured.given;

public class CourierClient {

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(Constants.CREATE_COURIER);
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierLogin courierLogin) {
        return given()
                .contentType(ContentType.JSON)
                .body(courierLogin)
                .post(Constants.LOGIN_COURIER);
    }

    @Step("Удаление курьера")
    public Response deleteCourier(int id) {
        return given()
                .delete(String.format("%s%d", Constants.DELETE_COURIER, id));
    }
}