package ru.tinab.utils;

import ru.tinab.model.Courier;
import java.util.UUID;

public class CourierGenerator {
    public static Courier randomCourier() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return new Courier("login_" + uuid, "pass_" + uuid, "Name_" + uuid);
    }
}