package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;
import io.restassured.http.ContentType;
import payloads.Payload;
import pojo.Login;
import routes.Routes;

public class LoginTests extends BaseClass {

	@Test
	public void testInvalidUserLogin() {
	    Login newLogin = new Login("wrongUser", "wrongPassword");  // invalid creds

	    given()
	        .contentType(ContentType.JSON)
	        .body(newLogin)
	    .when()
	        .post(Routes.AUTH_LOGIN)
	    .then()
	        .log().body()
	        .statusCode(401)
	        .body(equalTo("username or password is incorrect"));  // plain text check
	}

    @Test
    public void testValidUserLogin() {
        String username = configReader.getProperty("username");
        String password = configReader.getProperty("password");

        Login newLogin = new Login(username, password);

        given()
            .contentType(ContentType.JSON)
            .body(newLogin)
        .when()
            .post(Routes.AUTH_LOGIN)
        .then()
            .log().body()
            .statusCode(201) // ✅ updated to match actual response
            .body("token", notNullValue()); // token must exist
    }
}
