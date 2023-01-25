package org.soenke.sobott.filter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.ProjectTestUtil;
import org.soenke.sobott.entity.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ProductFilterTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @Test
    public void testProjectsEndpointProductFiltered() {
        ProjectTestUtil.createProjectWithProduct("project-number-123", "DUO-1", "DUO");
        ProjectTestUtil.createProjectWithProduct("project-number-918", "DUO-2", "DUO");
        ProjectTestUtil.createProjectWithProduct("project-number-911", "Other Product", "OtherProduct");
        ProjectTestUtil.createProjectWithProduct("project-number-900", "DUO-3", "DUO");

        String filterJson = "{\"product\": \"DUO\"}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));
    }

    @Test
    public void testProjectsEndpointProductFilteredCaseInsensitive() {
        ProjectTestUtil.createProjectWithProduct("project-number-123", "DUO-1", "DUO");
        ProjectTestUtil.createProjectWithProduct("project-number-918", "DUO-2", "DUO");
        ProjectTestUtil.createProjectWithProduct("project-number-911", "Other Product", "OtherProduct");
        ProjectTestUtil.createProjectWithProduct("project-number-900", "DUO-3", "DUO");

        String filterJson = "{\"product\": \"Duo\"}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));

        filterJson = "{\"product\": \"duO\"}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));
    }
}
