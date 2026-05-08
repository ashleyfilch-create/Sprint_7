package ru.tinab;

import io.restassured.RestAssured;
import org.junit.Before;
import ru.tinab.utils.Constants;

public class BaseTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASE_URL;
    }
}