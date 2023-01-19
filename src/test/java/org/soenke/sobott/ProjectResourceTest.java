package org.soenke.sobott;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Indexes;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.Segment;

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
    public void testProjectsEndpointMinAndMaxWallThicknessFiltered() {
        createProjectWithStructureThicknessAndHeight("1234", "tooSmall", "Wall", 10.0, 100.0);
        createProjectWithStructureThicknessAndHeight("9595", "theSameAsMin", "Wall", 25.0, 100.0);
        createProjectWithStructureThicknessAndHeight("0349", "between", "Wall", 50.0, 100.0);
        createProjectWithStructureThicknessAndHeight("8502", "betweenButNotWall", "Column", 50.0, 100.0);
        createProjectWithStructureThicknessAndHeight("0459", "theSameAsMax", "Wall", 55.3, 100.0);
        createProjectWithStructureThicknessAndHeight("3432", "tooBig", "Wall", 80.0, 100.0);

        String filterJson = "{\"wallFilter\": {\"minThickness\":25, \"maxThickness\":55.3}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("theSameAsMin"))
                .body("[1].projectName", is("between"))
                .body("[2].projectName", is("theSameAsMax"));
    }

    @Test
    public void testProjectsEndpointOnlyMinWallThicknessFiltered() {
        createProjectWithStructureThicknessAndHeight("2349", "tooSmall", "Wall", 10.0, 100.0);
        createProjectWithStructureThicknessAndHeight("0349", "theSameAsMin", "Wall", 25.0, 100.0);
        createProjectWithStructureThicknessAndHeight("3249", "higher", "Wall", 50.0, 100.0);
        createProjectWithStructureThicknessAndHeight("3400", "wayHigher", "Wall", 80.0, 100.0);

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
        createProjectWithStructureThicknessAndHeight("3423", "wayLower", "Wall", 25.0, 100.0);
        createProjectWithStructureThicknessAndHeight("3432", "lower", "Wall", 50.0, 100.0);
        createProjectWithStructureThicknessAndHeight("9040", "theSameAsMax", "Wall", 55.3, 100.0);
        createProjectWithStructureThicknessAndHeight("9003", "tooBig", "Wall", 80.0, 100.0);

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
        createProjectWithStructureThicknessAndHeight("1234", "TooSmall", "Wall", 20.0, 10.0);
        createProjectWithStructureThicknessAndHeight("5768", "SameAsMin", "Wall", 25.0, 300.0);
        createProjectWithStructureThicknessAndHeight("2345", "Between", "Wall", 30.5, 543.3);
        createProjectWithStructureThicknessAndHeight("2334", "BetweenButNotWall", "Column", 30.5, 543.3);
        createProjectWithStructureThicknessAndHeight("9453", "SameAsMax", "Wall", 87.3, 667.32);
        createProjectWithStructureThicknessAndHeight("5512", "TooBig", "Wall", 100.0, 1000.0);

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
        createProjectWithStructureThicknessAndHeight("1234", "TooSmall", "Wall", 20.0, 10.0);
        createProjectWithStructureThicknessAndHeight("5768", "SameAsMin", "Wall", 25.0, 300.0);
        createProjectWithStructureThicknessAndHeight("2345", "Between", "Wall", 30.5, 543.3);
        createProjectWithStructureThicknessAndHeight("9453", "WayBigger", "Wall", 87.3, 667.32);

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
        createProjectWithStructureThicknessAndHeight("1234", "WaySmaller", "Wall", 20.0, 10.0);
        createProjectWithStructureThicknessAndHeight("5768", "Between", "Wall", 25.0, 300.0);
        createProjectWithStructureThicknessAndHeight("2345", "SameAsMax", "Wall", 30.5, 543.3);
        createProjectWithStructureThicknessAndHeight("9453", "TooBig", "Wall", 87.3, 667.32);

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
    public void testProjectsEndpointWallThicknessAndHeightFiltered() {
        createProjectWithStructureThicknessAndHeight("1231", "DUO-1", "Wall", 20.0, 230.0);
        createProjectWithStructureThicknessAndHeight("2344", "DUO-2", "Wall", 30.0, 330.0);
        createProjectWithStructureThicknessAndHeight("9696", "DUO-3", "Wall", 40.0, 430.0);
        createProjectWithStructureThicknessAndHeight("8493", "DUO-4", "Wall", 50.0, 530.0);
        createProjectWithStructureThicknessAndHeight("1230", "DUO-5", "Wall", 60.0, 630.0);
        createProjectWithStructureThicknessAndHeight("0909", "DUO-6", "Wall", 70.0, 730.0);

        String filterJson = "{\"wallFilter\": {\"minThickness\":30, \"maxThickness\":65.0, " +
                "\"minHeight\":0, \"maxHeight\":1000}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-3"))
                .body("[2].projectName", is("DUO-4"))
                .body("[3].projectName", is("DUO-5"));
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
    public void testProjectsEndpointColumnThicknessFiltered() {
        createProjectWithStructureThicknessAndHeight("1231", "DUO-1", "Column", 20.0, 230.0);
        createProjectWithStructureThicknessAndHeight("2344", "DUO-2", "Column", 30.0, 330.0);
        createProjectWithStructureThicknessAndHeight("9696", "DUO-3", "Column", 40.0, 430.0);
        createProjectWithStructureThicknessAndHeight("8493", "DUO-4", "Wall", 50.0, 530.0);
        createProjectWithStructureThicknessAndHeight("1230", "DUO-5", "Column", 60.0, 630.0);

        String filterJson = "{\"columnFilter\": {\"minThickness\":30, \"maxThickness\":59.0}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-3"));
    }

    @Test
    public void testProjectsEndpointColumnHeightFiltered() {
        createProjectWithStructureThicknessAndHeight("1231", "DUO-1", "Column", 20.0, 230.0);
        createProjectWithStructureThicknessAndHeight("2344", "DUO-2", "Column", 30.0, 330.0);
        createProjectWithStructureThicknessAndHeight("9696", "DUO-3", "Column", 40.0, 430.0);
        createProjectWithStructureThicknessAndHeight("8493", "DUO-4", "Wall", 50.0, 530.0);
        createProjectWithStructureThicknessAndHeight("1230", "DUO-5", "Column", 60.0, 630.0);

        String filterJson = "{\"columnFilter\": {\"minHeight\":230, \"maxHeight\":530.0}}";
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
    public void testProjectsEndpointColumnThicknessAndHeightFiltered() {
        createProjectWithStructureThicknessAndHeight("1231", "DUO-1", "Column", 20.0, 230.0);
        createProjectWithStructureThicknessAndHeight("2344", "DUO-2", "Column", 30.0, 330.0);
        createProjectWithStructureThicknessAndHeight("9696", "DUO-3", "Column", 40.0, 430.0);
        createProjectWithStructureThicknessAndHeight("8493", "DUO-4", "Wall", 50.0, 530.0);
        createProjectWithStructureThicknessAndHeight("1230", "DUO-5", "Column", 60.0, 630.0);
        createProjectWithStructureThicknessAndHeight("0909", "DUO-6", "Column", 70.0, 730.0);

        String filterJson = "{\"columnFilter\": {\"minThickness\":30, \"maxThickness\":65.0, " +
                "\"minHeight\":0, \"maxHeight\":1000}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-3"))
                .body("[2].projectName", is("DUO-5"));
    }

    @Test
    public void testProjectsEndpointSegmentInfrastructureFiltered() {
        createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", Segment.Infrastructure, "Bridges");
        createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", Segment.Infrastructure, "Tunnels");
        createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", Segment.Infrastructure, "Water Plants");
        createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", Segment.Infrastructure, "Airports");

        String filterJson = "{\"infrastructureElements\": [\"Tunnels\", \"Water Plants\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-3"));
    }

    @Test
    public void testProjectsEndpointSegmentIndustrialFiltered() {
        createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", Segment.Industrial, "Power");
        createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", Segment.Industrial, "Chemicals");
        createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", Segment.Industrial, "Industrialized Manufacturing");
        createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", Segment.Industrial, "Oil & Gas");

        String filterJson = "{\"industrialElements\": [\"Oil & Gas\", \"Industrialized Manufacturing\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("DUO-3"))
                .body("[1].projectName", is("DUO-4"));
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
    public void testProjectsEndpointFilteredWithAllFilters() {
        createProjectsForAllFiltersTest();

        String filterJson = "{\"searchTerm\": \"Duo\", " +
                "\"product\": \"Duo\", " +
                "\"wallFilter\": {\"minThickness\":25, \"maxThickness\":87.3," +
                "\"minHeight\":100, \"maxHeight\":450}," +
                "\"columnFilter\": {\"minThickness\":25, \"maxThickness\":137.3," +
                "\"minHeight\":100, \"maxHeight\":450}, " +
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
                .body("[0].projectName", is("DUO-7"))
                .body("[1].projectName", is("DUO CorrectWallHeightAndThicknessButWrongColumnHeightAndThickness"))
                .body("[2].projectName", is("DUO CorrectColumnHeightAndThicknessButWrongWallHeightAndThickness"))
                .body("[3].projectName", is("DUO-5"))
                .body("[4].projectName", is("DUO-4"))
                .body("[5].projectName", is("DUO-3"))
                .body("[6].projectName", is("DUO-2"))
                .body("[7].projectName", is("DUO-1"));
    }

    protected void createProjectsForAllFiltersTest() {
        createFullProject("1231", "DUO ButTooLowThickness", "Wall", 20.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9458", "DUO-1", "Wall", 25.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9670", "BetweenButNot Duo", "Wall", 30.5, 30.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4587", "BetweenThicknessAnd Duo ButTooBigHeight", "Wall", 30.5, 3000.0, "OtherProduct", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("1077", "DUO-2", "Wall", 87.3, 330.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("2454", "DUO-3", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4055", "DUO-3 ButWaterPlants", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Water Plants",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("3234", "DUO-4", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Bridges",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4055", "DUO-5", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("3232", "DUO CorrectColumnHeightAndThicknessButWrongWallHeightAndThickness", "Column", 120.0, 120.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9283", "DUO CorrectWallHeightAndThicknessButWrongColumnHeightAndThickness", "Wall", 50.0, 200.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("9845", "EverythingOkButNotSearchTermMatch", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("4342", "DUO-6", "Wall", 82.0, 300.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Straight Wall", "Double Sided"));
        createFullProject("2356", "Duo", "Wall", 82.0, 500.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("2234", "Duo ButTooBigThickness", "Wall", 100.0, 230.0, "DUO", "Infrastructure", "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("8686", "DUO-7", "Wall", 87.3, 330.0, "DUO", "Industrial", "Oil & Gas",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        createFullProject("8686", "DUO-2 with wrong industrial", "Wall", 87.3, 330.0, "DUO", "Industrial", "Power",
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

    protected void createProjectWithSegmentLevelOneAndTwo(String projectNumber, String projectName, Segment segmentLevelOne,
                                                          String segmentLevelTwo) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setSegmentLevelOne(segmentLevelOne.getValue());
        project.setSegmentLevelTwo(segmentLevelTwo);
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
        project.setHeight(height);
        project.setProduct(product);
        project.setSegmentLevelOne(segmentLevelOne);
        project.setSegmentLevelTwo(segmentLevelTwo);
        project.setSolutionTags(solutionTags);
        project.persist();
    }

}