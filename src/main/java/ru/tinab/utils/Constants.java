package ru.tinab.utils;

public class Constants {

    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    // Courier
    public static final String CREATE_COURIER = "/api/v1/courier";
    public static final String LOGIN_COURIER = "/api/v1/courier/login";
    public static final String DELETE_COURIER = "/api/v1/courier/";

    // Order
    public static final String CREATE_ORDER = "/api/v1/orders";
    public static final String GET_ORDERS = "/api/v1/orders";
    public static final String ACCEPT_ORDER = "/api/v1/orders/accept/"; // <id> добавляется динамически
    public static final String GET_ORDER_BY_ID = "/api/v1/orders/track/"; // <track> добавляется динамически

    // Colors
    public static final String COLOR_BLACK = "BLACK";
    public static final String COLOR_GREY = "GREY";
}