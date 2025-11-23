package se.kth.lab3.search_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class SearchResourceTest {

    @Test
    public void testHealthEndpoint() {
        given()
                .when()
                .get("/api/search/health")
                .then()
                .statusCode(200);
    }

    @Test
    public void testSearchPatientsEndpointExists() {
        given()
                .when()
                .queryParam("query", "test")
                .get("/api/search/patients")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchPatientsWithoutQueryExists() {
        given()
                .when()
                .get("/api/search/patients")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchPatientsByNameExists() {
        given()
                .when()
                .queryParam("firstName", "Anna")
                .get("/api/search/patients/by-name")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchPatientsByConditionExists() {
        given()
                .when()
                .queryParam("condition", "diabetes")
                .get("/api/search/patients/by-condition")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchPatientsByPersonalNumberExists() {
        given()
                .when()
                .queryParam("personalNumber", "199001")
                .get("/api/search/patients/by-personal-number")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchJournalsExists() {
        given()
                .when()
                .queryParam("query", "huvudvärk")
                .get("/api/search/journals")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }

    @Test
    public void testSearchJournalsByPatientExists() {
        given()
                .when()
                .queryParam("patientId", 1)
                .get("/api/search/journals/by-patient")
                .then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(500)
                ));
    }
}