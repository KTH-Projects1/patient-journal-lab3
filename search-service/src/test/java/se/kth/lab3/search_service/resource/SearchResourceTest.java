package se.kth.lab3.search_service.resource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SearchResourceTest {

    @Test
    public void testSearchPatientsEndpoint() {
        given()
                .when()
                .queryParam("query", "test")
                .get("/api/search/patients")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    public void testSearchConditionsEndpoint() {
        given()
                .when()
                .queryParam("query", "diabetes")
                .get("/api/search/conditions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    public void testSearchWithEmptyQuery() {
        given()
                .when()
                .get("/api/search/patients")
                .then()
                .statusCode(400); // Bör returnera bad request för tom query
    }
}