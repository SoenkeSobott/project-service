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
                .contentType("application/json")
                .when().post("/projects")
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
        createProject("project-number-123", "DUO-1", "DUO");
        createProject("project-number-918", "DUO-2", "DUO");
        createProject("project-number-911", "Other Product", "OtherProduct");
        createProject("project-number-900", "DUO-3", "DUO");

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

    @Test
    public void testProjectsEndpointMinAndMaxWallThicknessFiltered() {
        createProject("project-number-123", "tooSmall", "Wall", 10.0);
        createProject("project-number-918", "theSameAsMin", "Wall", 25.0);
        createProject("project-number-911", "between", "Wall", 50.0);
        createProject("project-number-911", "betweenButNotWall", "Column", 50.0);
        createProject("project-number-917", "theSameAsMax", "Wall", 55.3);
        createProject("project-number-900", "tooBig", "Wall", 80.0);

        String filterJson = "{\"wallFilter\": {\"minThickness\":25, \"maxThickness\":55.3}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
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
    public void testProjectsEndpointOnlyMinWallThicknessFiltered() {
        createProject("project-number-123", "tooSmall", "Wall", 10.0);
        createProject("project-number-918", "theSameAsMin", "Wall", 25.0);
        createProject("project-number-911", "higher", "Wall", 50.0);
        createProject("project-number-900", "wayHigher", "Wall", 80.0);

        String filterJson = "{\"wallFilter\": {\"minThickness\":25}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("theSameAsMin"))
                .body("[1].projectName", is("higher"))
                .body("[2].projectName", is("wayHigher"));
    }

    @Test
    public void testProjectsEndpointOnlyMaxWallThicknessFiltered() {
        createProject("project-number-918", "wayLower", "Wall", 25.0);
        createProject("project-number-911", "lower", "Wall", 50.0);
        createProject("project-number-910", "theSameAsMax", "Wall", 55.3);
        createProject("project-number-900", "tooBig", "Wall", 80.0);

        String filterJson = "{\"wallFilter\": {\"maxThickness\":55.3}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("wayLower"))
                .body("[1].projectName", is("lower"))
                .body("[2].projectName", is("theSameAsMax"));
    }

    @Test
    public void testProjectsEndpointMinAndMaxWallHeightFiltered() {
        createFullProject("1234", "TooSmall", "Wall", 20.0, 10.0, "DUO");
        createFullProject("5768", "SameAsMin", "Wall", 25.0, 300.0, "DUO");
        createFullProject("2345", "Between", "Wall", 30.5, 543.3, "OtherProduct");
        createFullProject("2334", "BetweenButNotWall", "Column", 30.5, 543.3, "aProduct");
        createFullProject("9453", "SameAsMax", "Wall", 87.3, 667.32, "DUO");
        createFullProject("5512", "TooBig", "Wall", 100.0, 1000.0, "DUO");

        String filterJson = "{\"wallFilter\": {\"minHeight\":300, \"maxHeight\":667.32}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("SameAsMin"))
                .body("[1].projectName", is("Between"))
                .body("[2].projectName", is("SameAsMax"));
    }

    @Test
    public void testProjectsEndpointOnlyMinWallHeightFiltered() {
        createFullProject("1234", "TooSmall", "Wall", 20.0, 10.0, "DUO");
        createFullProject("5768", "SameAsMin", "Wall", 25.0, 300.0, "DUO");
        createFullProject("2345", "Between", "Wall", 30.5, 543.3, "OtherProduct");
        createFullProject("9453", "WayBigger", "Wall", 87.3, 667.32, "DUO");

        String filterJson = "{\"wallFilter\": {\"minHeight\":300}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("SameAsMin"))
                .body("[1].projectName", is("Between"))
                .body("[2].projectName", is("WayBigger"));
    }

    @Test
    public void testProjectsEndpointOnlyMaxWallHeightFiltered() {
        createFullProject("1234", "WaySmaller", "Wall", 20.0, 10.0, "DUO");
        createFullProject("5768", "Between", "Wall", 25.0, 300.0, "DUO");
        createFullProject("2345", "SameAsMax", "Wall", 30.5, 543.3, "OtherProduct");
        createFullProject("9453", "TooBig", "Wall", 87.3, 667.32, "DUO");

        String filterJson = "{\"wallFilter\":{\"maxHeight\":543.3}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
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
        // TODO: improve search to also get the last project TunnelHonkkongtestbuilding

        String filterJson = "{\"searchTerm\": \"KONG\"}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("Mega Factory kong"))
                .body("[1].projectName", is("Honk Kong test site"));

    }

    @Test
    public void testProjectsEndpointFilteredWithAllFilters() {
        createProjects();
        // TODO: project for each case

        String filterJson = "{\"searchTerm\": \"Duo\", " +
                "\"product\": \"Duo\", " +
                "\"wallFilter\": {\"minThickness\":25, \"maxThickness\":87.3," +
                "\"minHeight\":100, \"maxHeight\":450}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-3"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-1"));
    }

    protected void createProjects() {
        createFullProject("1231", "DUOButTooLowThickness", "Wall", 20.0, 230.0, "DUO");
        createFullProject("9458", "DUO-1", "Wall", 25.0, 230.0, "DUO");
        createFullProject("9670", "BetweenButNotDuo", "Wall", 30.5, 30.0, "OtherProduct");
        createFullProject("4587", "BetweenThicknessAndDuoButTooBigHeight", "Wall", 30.5, 3000.0, "OtherProduct");
        createFullProject("1077", "DUO-2", "Wall", 87.3, 330.0, "DUO");
        createFullProject("2454", "DUO-3", "Wall", 82.0, 300.0, "DUO");
        createFullProject("3232", "DUO-3 butNotWall", "Column", 82.0, 300.0, "DUO");
        createFullProject("9845", "EverythingOkButNotSearchTermMatch", "Wall", 82.0, 300.0, "DUO");
        createFullProject("2356", "Duo", "Wall", 82.0, 500.0, "DUO");
        createFullProject("2234", "DuoButTooBigThickness", "Wall", 100.0, 230.0, "DUO");
    }

    protected void createProject(String projectNumber, String projectName) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.persist();
    }

    protected void createProject(String projectNumber, String projectName, String structure, Double thickness) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure);
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
                                     String structure,
                                     Double thickness,
                                     Double height,
                                     String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure);
        project.setThickness(thickness);
        project.setHeight(height);
        project.setProduct(product);
        project.persist();
    }

}