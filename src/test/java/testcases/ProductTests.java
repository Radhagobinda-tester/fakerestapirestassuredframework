package testcases;

import pojo.Product;
import routes.Routes;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.Payload;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

public class ProductTests extends BaseClass {

    // 1) Get all products
    @Test
    public void testGetAllProducts() {
        given()
        .when()
            .get(Routes.GET_ALL_PRODUCTS)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }

    // 2) Get single product by ID
    @Test
    public void testGetSingleProductById() {
        int productId = configReader.getIntProperty("productId");

        given()
            .pathParam("id", productId)
        .when()
            .get(Routes.GET_PRODUCT_BY_ID)
        .then()
            .statusCode(200)
            .log().body();
    }

    // 3) Get limited products
    @Test
    public void testGetLimitedProducts() {
        given()
            .pathParam("limit", 3)
        .when()
            .get(Routes.GET_PRODUCTS_WITH_LIMIT)
        .then()
            .statusCode(200)
            .body("size()", equalTo(3));
    }

    // 4) Sorted products (descending)
    @Test
    public void testGetSortedProductsDesc() {
        Response response = given()
            .pathParam("order", "desc")
        .when()
            .get(Routes.GET_PRODUCTS_SORTED)
        .then()
            .statusCode(200)
            .extract().response();

        List<Integer> productIds = response.jsonPath().getList("id", Integer.class);
        assertThat(isSortedDesceding(productIds), is(true));
    }

    // 5) Sorted products (ascending)
    @Test
    public void testGetSortedProductsAsc() {
        Response response = given()
            .pathParam("order", "asc")
        .when()
            .get(Routes.GET_PRODUCTS_SORTED)
        .then()
            .statusCode(200)
            .extract().response();

        List<Integer> productIds = response.jsonPath().getList("id", Integer.class);
        assertThat(isSortedAsceding(productIds), is(true));
    }

    // 6) Get all categories
    @Test
    public void testGetAllCategories() {
        given()
        .when()
            .get(Routes.GET_ALL_CATEGORIES)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }

    // 7) Get products by category
    @Test
    public void testGetProductsByCategory() {
        given()
            .pathParam("category", "electronics")
        .when()
            .get(Routes.GET_PRODUCTS_BY_CATEGORY)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("category", everyItem(equalTo("electronics")));
    }

    // 8) Add new product
    @Test
    public void testAddNewProduct() {
        Product newProduct = Payload.productPayload();

        int productId = given()
            .contentType(ContentType.JSON)
            .body(newProduct)
        .when()
            .post(Routes.CREATE_PRODUCT)
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo(newProduct.getTitle()))
            .extract().jsonPath().getInt("id");

        System.out.println("New Product ID: " + productId);
    }

    // 9) Update product
    @Test
    public void testUpdateProduct() {
        int productId = configReader.getIntProperty("productId");
        Product updatedProduct = Payload.productPayload();

        given()
            .contentType(ContentType.JSON)
            .body(updatedProduct)
            .pathParam("id", productId)
        .when()
            .put(Routes.UPDATE_PRODUCT)
        .then()
            .statusCode(200)
            .body("title", equalTo(updatedProduct.getTitle()));
    }

    // 10) Delete product
    @Test
    public void testDeleteProduct() {
        int productId = configReader.getIntProperty("productId");

        given()
            .pathParam("id", productId)
        .when()
            .delete(Routes.DELETE_PRODUCT)
        .then()
            .statusCode(200);
    }
}
