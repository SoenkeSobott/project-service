package org.soenke.sobott;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ProjectResourceTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @Test
    public void testProjectEndpointWithNoFilters() {
        createProject("project-number-123", "project-name-123");
        createProject("project-number-918", "project-name-918");
        createProject("project-number-911", "project-name-911");

        given()
                .when().get("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectNumber", is("project-number-123"))
                .body("[0].projectName", is("project-name-123"))
                .body("[1].projectNumber", is("project-number-918"))
                .body("[1].projectName", is("project-name-918"))
                .body("[2].projectNumber", is("project-number-911"))
                .body("[2].projectName", is("project-name-911"));
    }

    @Test
    public void testProjectEndpointWithEmptyThicknessFilter() {
        given()
                .when().get("/projects?thickness=null")
                .then()
                .statusCode(404);
    }

    @Test
    public void testProjectsEndpointThicknessFiltered() {
        createProject("project-number-123", "toSmall", 10.0);
        createProject("project-number-918", "theSame", 25.0);
        createProject("project-number-911", "greater", 50.0);
        createProject("project-number-900", "wayGreater", 80.0);


        given()
                .when().get("/projects?thickness=25.0")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectNumber", is("project-number-918"))
                .body("[0].projectName", is("theSame"))
                .body("[1].projectNumber", is("project-number-911"))
                .body("[1].projectName", is("greater"))
                .body("[2].projectNumber", is("project-number-900"))
                .body("[2].projectName", is("wayGreater"));
    }

    @Test
    public void testProjectsEndpointProductFiltered() {
        createProject("project-number-123", "DUO-1", "DUO");
        createProject("project-number-918", "DUO-2", "DUO");
        createProject("project-number-911", "Other Product", "OtherProduct");
        createProject("project-number-900", "DUO-3", "DUO");

        given()
                .when().get("/projects?product=DUO")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));
    }

    @Test
    public void testProjectsEndpointProductFilteredCaseInsensitive() {
        createProject("project-number-123", "DUO-1", "DUO");
        createProject("project-number-918", "DUO-2", "DUO");
        createProject("project-number-911", "Other Product", "OtherProduct");
        createProject("project-number-900", "DUO-3", "DUO");

        given()
                .when().get("/projects?product=Duo")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));

        given()
                .when().get("/projects?product=duO")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));
    }

    @Test
    public void testProjectsEndpointFilteredWithAllFilters() {
        createFullProject("project-number-123", "DUO but to small", 20.0, "DUO");
        createFullProject("project-number-918", "DUO-1", 25.0, "DUO");
        createFullProject("project-number-911", "Big but not Duo", 30.5, "OtherProduct");
        createFullProject("project-number-900", "DUO-2", 87.3, "DUO");

        given()
                .when().get("/projects?product=DUO&thickness=25.0")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"));
    }

    protected void createProject(String projectNumber, String projectName) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.persist();
    }

    protected void createProject(String projectNumber, String projectName, Double thickness) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setThickness(thickness);
        project.persist();
    }

    protected void createProject(String projectNumber, String projectName, String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.persist();
    }

    protected void createFullProject(String projectNumber, String projectName, Double thickness, String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setThickness(thickness);
        project.setProduct(product);
        project.persist();
    }

}