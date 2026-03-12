package ru.tinab.model;

public class CourierLogin {
    private String login;
    private String password;

    // Пустой конструктор для Jackson
    public CourierLogin() {}

    public CourierLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
}