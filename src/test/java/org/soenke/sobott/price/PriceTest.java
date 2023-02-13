package org.soenke.sobott.price;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PriceTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @Test
    public void testDUOProjectPriceEndpoint() {
        createProjectWithPrice("911", "test", "DUO", 154.44);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(154.44f))
                .body("currency", is("HKD"))
                .body("unit", is("M2"));
    }

    @Test
    public void testShoringProjectPriceEndpoint() {
        createProjectWithPrice("911", "test", "PS100", 303.69);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(303.69f))
                .body("currency", is("HKD"))
                .body("unit", is("M3"));
    }

    @Test
    public void testProjectPriceEndpointWithMissingProduct() {
        createProjectWithPrice("911", "test", null, 34.4);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("Couldn't find Product unit(m2/m3) for Project number: 911"));
    }

    @Test
    public void testProjectPriceEndpointUsesDefaultUnitIfInvalidProductPassed() {
        createProjectWithPrice("911", "test", "InvalidProductType", 250.0);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(250.0f))
                .body("currency", is("HKD"))
                .body("unit", is("M2"));
    }

    protected void createProjectWithPrice(String projectNumber, String projectName,
                                          String product, Double projectPrice) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.setProjectPrice(projectPrice);
        project.persist();
    }
}