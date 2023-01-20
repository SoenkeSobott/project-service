package org.soenke.sobott;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Indexes;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

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
        createProjectWithStructureThicknessAndHeight("project-number-123", "project-name-123");
        createProjectWithStructureThicknessAndHeight("project-number-918", "project-name-918");
        createProjectWithStructureThicknessAndHeight("project-number-911", "project-name-911");

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
        createProjectWithProduct("project-number-123", "DUO-1", "DUO");
        createProjectWithProduct("project-number-918", "DUO-2", "DUO");
        createProjectWithProduct("project-number-911", "Other Product", "OtherProduct");
        createProjectWithProduct("project-number-900", "DUO-3", "DUO");

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
        createProjectWithProduct("project-number-123", "DUO-1", "DUO");
        createProjectWithProduct("project-number-918", "DUO-2", "DUO");
        createProjectWithProduct("project-number-911", "Other Product", "OtherProduct");
        createProjectWithProduct("project-number-900", "DUO-3", "DUO");

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
    public void testProjectsEndpointProductNameFiltered() {
        createProjectWithStructureThicknessAndHeight("project-number-123", "Honk Kong test site");
        createProjectWithStructureThicknessAndHeight("project-number-918", "Mega Factory kong");
        createProjectWithStructureThicknessAndHeight("project-number-911", "Housing");
        createProjectWithStructureThicknessAndHeight("project-number-900", "TunnelHonkkongtestbuilding");
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
    public void testProjectsEndpointSolutionTagFiltered() {
        createProjectWithSolutionTags("DUO-1", "Basement", "Shaft", "Double-Sided", "");
        createProjectWithSolutionTags("DUO-2", "Underground", "Wall Post", "Double-Sided", "Shaft");
        createProjectWithSolutionTags("DUO-3", "Straight-Wall", "Anchor To Existing Wall", "", "");
        createProjectWithSolutionTags("DUO-4", "Mock-Up", "Shaft", "Single-Sided", "");
        createProjectWithSolutionTags("DUO-5", "Underground", "T-Wall", "Double-Sided", "");
        createProjectWithSolutionTags("DUO-6", "", "", "", "Anchor To Existing Wall");

        String filterJson = "{\"solutionTags\": [\"Basement\", \"Anchor To Existing Wall\", \"Shaft\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(5))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"))
                .body("[3].projectName", is("DUO-4"))
                .body("[4].projectName", is("DUO-6"));
    }

    @Test
    public void testProjectsEndpointFilteredWithAllFiltersOnDefault() {
        createProjectWithStructureThicknessAndHeight("project-number-123", "DUO-1");
        createProjectWithProduct("12312343", "DUO-2", "DUO");
        createProjectWithStructureThicknessAndHeight("8502", "DUO-3", "Column", 50.0, 100.0);
        createProjectWithStructureThicknessAndHeight("23123", "DUO-4", "Column", 50.0, 100.0);
        createProjectWithProduct("32233", "DUO-5", "DUO");

        String filterJson = "{\"searchTerm\": \"\", " +
                "\"product\": \"\", " +
                "\"wallFilter\": {\"minThickness\":null, \"maxThickness\":null," +
                "\"minHeight\":null, \"maxHeight\":null}," +
                "\"columnFilter\": {\"minLength\":null, \"maxLength\":null, " +
                "\"minWidth\":null, \"maxWidth\":null," +
                "\"minHeight\":null, \"maxHeight\":null}," +
                "\"infrastructureElements\": []," +
                "\"industrialElements\": []," +
                "\"solutionTags\": []}";

        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(5))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-2"))
                .body("[2].projectName", is("DUO-3"))
                .body("[3].projectName", is("DUO-4"))
                .body("[4].projectName", is("DUO-5"));
    }

    @Test
    public void testProjectsEndpointFilteredWithAllFilters() {
        createProjectsForAllFiltersTest();

        String filterJson = "{\"searchTerm\": \"Duo\", " +
                "\"product\": \"Duo\", " +
                "\"wallFilter\": {\"minThickness\":25, \"maxThickness\":87.3," +
                "\"minHeight\":100, \"maxHeight\":450}," +
                "\"columnFilter\": {\"minLength\":30, \"maxLength\":59.0, " +
                "\"minWidth\":30, \"maxWidth\":119.0," +
                "\"minHeight\":0, \"maxHeight\":629}," +
                "\"infrastructureElements\": [\"Tunnels\", \"Bridges\"]," +
                "\"industrialElements\": [\"Oil & Gas\", \"Industrialized Manufacturing\"]," +
                "\"solutionTags\": [\"Basement\", \"Anchor To Existing Wall\", \"Shaft\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(8))
                .body("[0].projectName", is("DUO CorrectColumnSizes"))
                .body("[1].projectName", is("DUO-7"))
                .body("[2].projectName", is("DUO CorrectWallHeightAndThickness"))
                .body("[3].projectName", is("DUO-5"))
                .body("[4].projectName", is("DUO-4"))
                .body("[5].projectName", is("DUO-3"))
                .body("[6].projectName", is("DUO-2"))
                .body("[7].projectName", is("DUO-1"));
    }

    protected void createProjectsForAllFiltersTest() {
        createWallProjectWithAllFields();
        createFullProject("3232", "DUO CorrectColumnSizes", "Column", 120.0, 40.0, 40.0, 120.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4994", "DUO ColumnLengthTooSmall", "Column", 120.0, 20.0, 40.0, 120.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("0901", "DUO ColumnWidthTooHigh", "Column", 120.0, 40.0, 180.0, 120.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createWallProjectWithAllFields() {
        createFullProject("1231", "DUO ButTooLowThickness", "Wall", 20.0, 20.0, 20.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9458", "DUO-1", "Wall", 25.0, 20.0, 20.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9670", "BetweenButNot Duo", "Wall", 30.5, 20.0, 20.0, 30.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4587", "BetweenThicknessAnd Duo ButTooBigHeight", "Wall", 30.5, 20.0, 20.0, 3000.0, "OtherProduct", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("1077", "DUO-2", "Wall", 87.3, 20.0, 20.0, 330.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("2454", "DUO-3", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4055", "DUO-3 ButWaterPlants", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Water Plants",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("3234", "DUO-4", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Bridges",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4055", "DUO-5", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9283", "DUO CorrectWallHeightAndThickness", "Wall", 50.0, 20.0, 20.0, 200.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9845", "EverythingOkButNotSearchTermMatch", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4342", "DUO-6", "Wall", 82.0, 20.0, 20.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Straight Wall", "Double Sided"));
        createFullProject("2356", "Duo", "Wall", 82.0, 20.0, 20.0, 500.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("2234", "Duo ButTooBigThickness", "Wall", 100.0, 20.0, 20.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("8686", "DUO-7", "Wall", 87.3, 20.0, 20.0, 330.0, "DUO", "Industrial", "Oil & Gas",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("5523", "DUO-2 with wrong industrial", "Wall", 87.3, 20.0, 20.0, 330.0, "DUO", "Industrial", "Power",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createProjectWithStructureThicknessAndHeight(String projectNumber, String projectName) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.persist();
    }

    protected void createProjectWithStructureThicknessAndHeight(String projectNumber, String projectName, String structure, Double thickness, Double height) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure);
        project.setThickness(thickness);
        project.setHeight(height);
        project.persist();
    }

    protected void createProjectWithProduct(String projectNumber, String projectName, String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.persist();
    }

    protected void createProjectWithSolutionTags(String projectName, String solutionTagOne, String solutionTagTwo,
                                                 String solutionTagThree, String solutionTagFour) {
        Project project = new Project();
        project.setProjectNumber(RandomStringUtils.random(8));
        project.setProjectName(projectName);
        project.setSolutionTags(Arrays.asList(solutionTagOne, solutionTagTwo, solutionTagThree, solutionTagFour));
        project.persist();
    }

    protected void createFullProject(String projectNumber,
                                     String projectName,
                                     String structure,
                                     Double thickness,
                                     Double length,
                                     Double width,
                                     Double height,
                                     String product,
                                     String segmentLevelOne,
                                     String segmentLevelTwo,
                                     List<String> solutionTags) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure);
        project.setThickness(thickness);
        project.setLength(length);
        project.setWidth(width);
        project.setHeight(height);
        project.setProduct(product);
        project.setSegmentLevelOne(segmentLevelOne);
        project.setSegmentLevelTwo(segmentLevelTwo);
        project.setSolutionTags(solutionTags);
        project.persist();
    }

}