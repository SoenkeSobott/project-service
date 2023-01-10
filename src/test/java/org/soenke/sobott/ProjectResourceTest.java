package org.soenke.sobott;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Indexes;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ProjectResourceTest {

    @Inject
    MongoClient mongoClient;

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @BeforeEach
    public void setUpDB() {
        mongoClient.getDatabase("perisolutionx").getCollection("projects")
                .createIndex(Indexes.text("projectName"));
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
    public void testProjectEndpointWithEmptyThicknessFilters() {
        given()
                .when().get("/projects?minThickness=null")
                .then()
                .statusCode(404);

        given()
                .when().get("/projects?maxThickness=null")
                .then()
                .statusCode(404);
    }

    @Test
    public void testProjectsEndpointMinAndMaxThicknessFiltered() {
        createProject("project-number-123", "tooSmall", 10.0);
        createProject("project-number-918", "theSameAsMin", 25.0);
        createProject("project-number-911", "between", 50.0);
        createProject("project-number-917", "theSameAsMax", 55.3);
        createProject("project-number-900", "tooBig", 80.0);

        given()
                .when().get("/projects?minThickness=25.0&maxThickness=55.3")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectNumber", is("project-number-918"))
                .body("[0].projectName", is("theSameAsMin"))
                .body("[1].projectNumber", is("project-number-911"))
                .body("[1].projectName", is("between"))
                .body("[2].projectNumber", is("project-number-917"))
                .body("[2].projectName", is("theSameAsMax"));
    }

    @Test
    public void testProjectsEndpointOnlyMinThicknessFiltered() {
        createProject("project-number-123", "tooSmall", 10.0);
        createProject("project-number-918", "theSameAsMin", 25.0);
        createProject("project-number-911", "higher", 50.0);
        createProject("project-number-900", "wayHigher", 80.0);

        given()
                .when().get("/projects?minThickness=25.0")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("theSameAsMin"))
                .body("[1].projectName", is("higher"))
                .body("[2].projectName", is("wayHigher"));
    }

    @Test
    public void testProjectsEndpointOnlyMaxThicknessFiltered() {
        createProject("project-number-918", "wayLower", 25.0);
        createProject("project-number-911", "lower", 50.0);
        createProject("project-number-910", "theSameAsMax", 55.3);
        createProject("project-number-900", "tooBig", 80.0);

        given()
                .when().get("/projects?maxThickness=55.3")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("wayLower"))
                .body("[1].projectName", is("lower"))
                .body("[2].projectName", is("theSameAsMax"));
    }

    @Test
    public void testProjectsEndpointMinAndMaxHeightFiltered() {
        createFullProject("1234", "TooSmall", 20.0, 10.0, "DUO");
        createFullProject("5768", "SameAsMin", 25.0, 300.0, "DUO");
        createFullProject("2345", "Between", 30.5, 543.3, "OtherProduct");
        createFullProject("9453", "SameAsMax", 87.3, 667.32, "DUO");
        createFullProject("5512", "TooBig", 100.0, 1000.0, "DUO");

        given()
                .when().get("/projects?minHeight=300&maxHeight=667.32")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("SameAsMin"))
                .body("[1].projectName", is("Between"))
                .body("[2].projectName", is("SameAsMax"));
    }

    @Test
    public void testProjectsEndpointOnlyMinHeightFiltered() {
        createFullProject("1234", "TooSmall", 20.0, 10.0, "DUO");
        createFullProject("5768", "SameAsMin", 25.0, 300.0, "DUO");
        createFullProject("2345", "Between", 30.5, 543.3, "OtherProduct");
        createFullProject("9453", "WayBigger", 87.3, 667.32, "DUO");

        given()
                .when().get("/projects?minHeight=300")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("SameAsMin"))
                .body("[1].projectName", is("Between"))
                .body("[2].projectName", is("WayBigger"));
    }

    @Test
    public void testProjectsEndpointOnlyMaxHeightFiltered() {
        createFullProject("1234", "WaySmaller", 20.0, 10.0, "DUO");
        createFullProject("5768", "Between", 25.0, 300.0, "DUO");
        createFullProject("2345", "SameAsMax", 30.5, 543.3, "OtherProduct");
        createFullProject("9453", "TooBig", 87.3, 667.32, "DUO");

        given()
                .when().get("/projects?maxHeight=543.3")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("WaySmaller"))
                .body("[1].projectName", is("Between"))
                .body("[2].projectName", is("SameAsMax"));
    }

    @Test
    public void testProjectsEndpointProductNameFiltered() {
        createProject("project-number-123", "Honk Kong test site", "DUO");
        createProject("project-number-918", "Mega Factory kong", "DUO");
        createProject("project-number-911", "Housing", "OtherProduct");
        createProject("project-number-900", "TunnelHonkkongtestbuilding", "DUO");

        given()
                .when().get("/projects?searchTerm=KONG")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("Mega Factory kong"))
                .body("[1].projectName", is("Honk Kong test site"));

        // TODO: improve search
    }

    @Test
    public void testProjectsEndpointFilteredWithAllFilters() {
        createFullProject("1231", "DUOButTooLowThickness", 20.0, 230.0, "DUO");
        createFullProject("9458", "DUO-1", 25.0, 230.0, "DUO");
        createFullProject("9670", "BetweenButNotDuo", 30.5, 30.0, "OtherProduct");
        createFullProject("4587", "BetweenThicknessAndDuoButTooBigHeight", 30.5, 3000.0, "OtherProduct");
        createFullProject("1077", "DUO-2", 87.3, 330.0, "DUO");
        createFullProject("1077", "DUO-3", 82.0, 300.0, "DUO");
        createFullProject("2234", "DuoButTooBigThickness", 100.0, 230.0, "DUO");

        given()
                .when().get("/projects?product=DUO&minThickness=25.0&maxThickness=87.3&minHeight=100&maxHeight=450")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"));
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

    protected void createFullProject(String projectNumber,
                                     String projectName,
                                     Double thickness,
                                     Double height,
                                     String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setThickness(thickness);
        project.setHeight(height);
        project.setProduct(product);
        project.persist();
    }

}