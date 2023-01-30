package org.soenke.sobott;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Indexes;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.SegmentLevelOne;
import org.soenke.sobott.enums.Structure;

import javax.inject.Inject;
import java.util.Arrays;

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
        ProjectTestUtil.createBaseProject("project-number-123", "Aasd");
        ProjectTestUtil.createBaseProject("project-number-918", "fdfd");
        ProjectTestUtil.createBaseProject("project-number-911", "xy");

        given()
                .contentType("application/json")
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectNumber", is("project-number-123"))
                .body("[0].projectName", is("Aasd"))
                .body("[1].projectNumber", is("project-number-918"))
                .body("[1].projectName", is("fdfd"))
                .body("[2].projectNumber", is("project-number-911"))
                .body("[2].projectName", is("xy"));
    }

    @Test
    public void testProjectsEndpointProductNameFiltered() {
        ProjectTestUtil.createBaseProject("project-number-123", "Honk Kong test site");
        ProjectTestUtil.createBaseProject("project-number-918", "Mega Factory kong");
        ProjectTestUtil.createBaseProject("project-number-911", "Housing");
        ProjectTestUtil.createBaseProject("project-number-900", "TunnelHonkkongtestbuilding");
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
        ProjectTestUtil.createProjectWithSolutionTags("DUO-1", "Basement", "Shaft", "Double-Sided", "");
        ProjectTestUtil.createProjectWithSolutionTags("DUO-2", "Underground", "Wall Post", "Double-Sided", "Shaft");
        ProjectTestUtil.createProjectWithSolutionTags("DUO-3", "Straight-Wall", "Anchor To Existing Wall", "", "");
        ProjectTestUtil.createProjectWithSolutionTags("DUO-4", "Mock-Up", "Shaft", "Single-Sided", "");
        ProjectTestUtil.createProjectWithSolutionTags("DUO-5", "Underground", "T-Wall", "Double-Sided", "");
        ProjectTestUtil.createProjectWithSolutionTags("DUO-6", "", "", "", "Anchor To Existing Wall");

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
    public void testSolutionTagsEndpoint() {
        ProjectTestUtil.createFullProject("3232", "DUO-1", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Tank", "Shaft"));
        ProjectTestUtil.createFullProject("3421", "DUO-2", Structure.Culvert, 60.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("1077", "DUO-3", Structure.Wall, 87.3, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Column W/o Tie-Rod", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("2454", "DUO-4", Structure.Wall, 82.0, 20.0, 20.0, 300.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Traveler"));
        ProjectTestUtil.createFullProject("8686", "DUO-5", Structure.Wall, 87.3, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("Slab & Beam In One Pour", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4233", "DUO-6 ButWrongWallThickness", Structure.Wall, 4.0, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("TagNotShow", "AlsoNoShow"));

        String filterJson = "{\"searchTerm\": \"Duo\", " +
                "\"product\": \"Duo\", " +
                "\"wallFilter\": {\"minThickness\":25, \"maxThickness\":87.3," +
                "\"minHeight\":100, \"maxHeight\":450}," +
                "\"columnFilter\": {\"minLength\":30, \"maxLength\":59.0, " +
                "\"minWidth\":30, \"maxWidth\":119.0," +
                "\"minHeight\":0, \"maxHeight\":629}," +
                "\"culvertFilter\": {\"minThickness\":30, \"maxThickness\":120.0, " +
                "\"minHeight\":0, \"maxHeight\":1000}," +
                "\"infrastructureElements\": [\"Tunnels\", \"Bridges\"]," +
                "\"industrialElements\": [\"Oil & Gas\", \"Industrialized Manufacturing\"]," +
                "\"residentialElements\": [\"Multi-Family housing up to 10 floors\", \"Single-Family Housing\"]," +
                "\"nonResidentialElements\": [\"Healthcare Buildings\", \"Transportation & Logistics Buildings\"]," +
                "\"solutionTags\": null}";

        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects/solution-tags")
                .then()
                .statusCode(200)
                .body("solutionTags.size()", is(7))
                .body("solutionTags[0]", is("Slab & Beam In One Pour"))
                .body("solutionTags[1]", is("Anchor To Existing Wall"))
                .body("solutionTags[2]", is("Shaft"))
                .body("solutionTags[3]", is("Basement"))
                .body("solutionTags[4]", is("Traveler"))
                .body("solutionTags[5]", is("Column W/o Tie-Rod"))
                .body("solutionTags[6]", is("Tank"));
    }

    @Test
    public void testSolutionTagsEndpointWithColumnFilter() {
        ProjectTestUtil.createFullProject("3232", "DUO-1", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("basement", "tank", "shaft"));
        ProjectTestUtil.createFullProject("3421", "DUO-2", Structure.Column, 60.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("basement", "anchor to existing wall", "shaft"));
        ProjectTestUtil.createFullProject("1077", "DUO-3", Structure.Column, 87.3, 10.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("column w/o tie-rod", "anchor to existing wall", "shaft"));
        ProjectTestUtil.createFullProject("2454", "DUO-4", Structure.Wall, 82.0, 20.0, 20.0, 300.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "NotShow", "AlsoSomethingElse"));
        ProjectTestUtil.createFullProject("8686", "DUO-5", Structure.Column, 87.3, null, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("slab & beam in one pour", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4233", "DUO-6 ButWrongWallThickness", Structure.Culvert,
                50.0, 200.0, 4.0, 20.0, 20.0, 330.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("TagNotShow", "AlsoNoShow"));

        String filterJson = "{\"columnFilter\": {\"minLength\":15, \"maxLength\":100.0, " +
                "\"minWidth\":0, \"maxWidth\":500.0," +
                "\"minHeight\":0, \"maxHeight\":1000}}";

        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects/solution-tags")
                .then()
                .statusCode(200)
                .body("solutionTags.size()", is(4))
                .body("solutionTags[0]", is("basement"))
                .body("solutionTags[1]", is("tank"))
                .body("solutionTags[2]", is("shaft"))
                .body("solutionTags[3]", is("anchor to existing wall"));
    }

    @Test
    public void testSolutionTagsEndpointWithFiltersThatReturnNoProjects() {
        ProjectTestUtil.createFullProject("3232", "DUO-1", Structure.Column, 120.0, 400.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("basement", "tank", "shaft"));
        ProjectTestUtil.createFullProject("3421", "DUO-2", Structure.Culvert, 60.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("basement", "anchor to existing wall", "shaft"));
        ProjectTestUtil.createFullProject("1077", "DUO-3", Structure.Column, 87.3, 10.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("column w/o tie-rod", "anchor to existing wall", "shaft"));
        ProjectTestUtil.createFullProject("2454", "DUO-4", Structure.Wall, 82.0, 20.0, 20.0, 300.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "NotShow", "AlsoSomethingElse"));
        ProjectTestUtil.createFullProject("8686", "DUO-5", Structure.Column, 87.3, null, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("slab & beam in one pour", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4233", "DUO-6 ButWrongWallThickness", Structure.Culvert, 4.0, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("TagNotShow", "AlsoNoShow"));

        String filterJson = "{\"columnFilter\": {\"minLength\":15, \"maxLength\":100.0, " +
                "\"minWidth\":0, \"maxWidth\":500.0," +
                "\"minHeight\":500, \"maxHeight\":1000}}";

        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects/solution-tags")
                .then()
                .statusCode(200)
                .body("solutionTags.size()", is(0));
    }

    @Test
    public void testSolutionTagsEndpointWithNoFilterReturnsAllSolutionTags() {
        ProjectTestUtil.createFullProject("3232", "DUO-1", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Tank", "Shaft"));

        given()
                .contentType("application/json")
                .when().post("/projects/solution-tags")
                .then()
                .statusCode(200)
                .body("solutionTags.size()", is(51));
    }

    @Test
    public void testSolutionTagsEndpointWithEmptyFilters() {
        ProjectTestUtil.createFullProject("3232", "DUO-1", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Tank", "Shaft"));
        ProjectTestUtil.createFullProject("3421", "DUO-2", Structure.Culvert, 60.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("1077", "DUO-3", Structure.Wall, 87.3, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Column W/o Tie-Rod", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("2454", "DUO-4", Structure.Wall, 82.0, 20.0, 20.0, 300.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Traveler"));
        ProjectTestUtil.createFullProject("8686", "DUO-5", Structure.Wall, 87.3, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("Slab & Beam In One Pour", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4233", "DUO-6 ButWrongWallThickness", Structure.Wall, 4.0, 20.0, 20.0, 330.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas",
                Arrays.asList("TagNotShow", "AlsoNoShow"));

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
                .when().post("/projects/solution-tags")
                .then()
                .statusCode(200)
                .body("solutionTags.size()", is(51));
    }

    @Test
    public void testProjectsEndpointFilteredWithAllFiltersOnDefault() {
        ProjectTestUtil.createBaseProject("project-number-123", "DUO-1");
        ProjectTestUtil.createProjectWithProduct("12312343", "DUO-2", "DUO");
        ProjectTestUtil.createProjectWithStructureThicknessAndHeight("8502", "DUO-3", Structure.Column, 50.0, 100.0);
        ProjectTestUtil.createProjectWithStructureThicknessAndHeight("23123", "DUO-4", Structure.Column, 50.0, 100.0);
        ProjectTestUtil.createProjectWithProduct("32233", "DUO-5", "DUO");

        String filterJson = "{\"searchTerm\": \"\", " +
                "\"product\": \"\", " +
                "\"wallFilter\": {\"minThickness\":null, \"maxThickness\":null," +
                "\"minHeight\":null, \"maxHeight\":null}," +
                "\"columnFilter\": {\"minLength\":null, \"maxLength\":null, " +
                "\"minWidth\":null, \"maxWidth\":null," +
                "\"minHeight\":null, \"maxHeight\":null}," +
                "\"shoringFilter\": {\"minThickness\":null, \"maxThickness\":null," +
                "\"minHeight\":null, \"maxHeight\":null}," +
                "\"infrastructureElements\": []," +
                "\"industrialElements\": []," +
                "\"residentialElements\": []," +
                "\"nonResidentialElements\": []," +
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
                "\"culvertFilter\": {\"minThickness\":30, \"maxThickness\":120.0, " +
                "\"minHeight\":0, \"maxHeight\":1000}," +
                "\"shoringFilter\": {\"minThickness\":30, \"maxThickness\":65.0, " +
                "\"minHeight\":30, \"maxHeight\":1000}," +
                "\"infrastructureElements\": [\"Tunnels\", \"Bridges\"]," +
                "\"industrialElements\": [\"Oil & Gas\", \"Industrialized Manufacturing\"]," +
                "\"residentialElements\": [\"Multi-Family housing up to 10 floors\", \"Single-Family Housing\"]," +
                "\"nonResidentialElements\": [\"Healthcare Buildings\", \"Transportation & Logistics Buildings\"]," +
                "\"solutionTags\": [\"Basement\", \"Anchor To Existing Wall\", \"Shaft\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(12))
                .body("[0].projectName", is("DUO NonResidential"))
                .body("[1].projectName", is("DUO Residential"))
                .body("[2].projectName", is("Not a DUO PS100"))
                .body("[3].projectName", is("DUO Culvert"))
                .body("[4].projectName", is("DUO CorrectColumnSizes"))
                .body("[5].projectName", is("DUO-7"))
                .body("[6].projectName", is("DUO CorrectWallHeightAndThickness"))
                .body("[7].projectName", is("DUO-5"))
                .body("[8].projectName", is("DUO-4"))
                .body("[9].projectName", is("DUO-3"))
                .body("[10].projectName", is("DUO-2"))
                .body("[11].projectName", is("DUO-1"));
    }

    protected void createProjectsForAllFiltersTest() {
        createWallProjectWithAllFields();
        createColumnProjectsWithAllFields();
        createCulvertProjectsWithAllFields();
        createShoringProjectsWithAllFields();
        createResidentialProjectsWithAllFields();
        createNonResidentialProjectsWithAllFields();
    }

    protected void createWallProjectWithAllFields() {
        ProjectTestUtil.createFullProject("1231", "DUO ButTooLowThickness", Structure.Wall, 20.0, 20.0, 20.0, 230.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("9458", "DUO-1", Structure.Wall, 25.0, 20.0, 20.0, 230.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("9670", "BetweenButNot Duo", Structure.Wall, 30.5, 20.0, 20.0, 30.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4587", "BetweenThicknessAnd Duo ButTooBigHeight", Structure.Wall, 30.5, 20.0, 20.0, 3000.0, 50.0, 200.0, "OtherProduct", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("1077", "DUO-2", Structure.Wall, 87.3, 20.0, 20.0, 330.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("2454", "DUO-3", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4055", "DUO-3 ButWaterPlants", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Water Plants", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("3234", "DUO-4", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Bridges", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4055", "DUO-5", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("9283", "DUO CorrectWallHeightAndThickness", Structure.Wall, 50.0, 20.0, 20.0, 200.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("9845", "EverythingOkButNotSearchTermMatch", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4342", "DUO-6", Structure.Wall, 82.0, 20.0, 20.0, 300.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Straight Wall", "Double Sided"));
        ProjectTestUtil.createFullProject("2356", "Duo", Structure.Wall, 82.0, 20.0, 20.0, 500.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("2234", "Duo ButTooBigThickness", Structure.Wall, 100.0, 20.0, 20.0, 230.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("8686", "DUO-7", Structure.Wall, 87.3, 20.0, 20.0, 330.0, 50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Oil & Gas", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("5523", "DUO-2 with wrong industrial", Structure.Wall, 87.3, 20.0, 20.0, 330.0, 50.0, 200.0, "DUO", SegmentLevelOne.Industrial, "Power", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createColumnProjectsWithAllFields() {
        ProjectTestUtil.createFullProject("3232", "DUO CorrectColumnSizes", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("4994", "DUO ColumnLengthTooSmall", Structure.Column, 120.0, 20.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("0901", "DUO ColumnWidthTooHigh", Structure.Column, 120.0, 40.0, 180.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createCulvertProjectsWithAllFields() {
        ProjectTestUtil.createFullProject("3232", "DUO Culvert", Structure.Culvert, 60.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("3232", "DUO CulvertToLowThickness", Structure.Culvert, 29.0, 40.0, 400.0, 520.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("3232", "DUO CulvertToHighHeight", Structure.Culvert, 60.0, 40.0, 400.0, 1120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createResidentialProjectsWithAllFields() {
        ProjectTestUtil.createFullProject("3232", "DUO Residential", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Residential, "Single-Family Housing",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("3232", "DUO ResidentialWrongLevelTwo", Structure.Column, 120.0, 40.0, 40.0, 120.0,
                50.0, 200.0, "DUO", SegmentLevelOne.Residential, "Multi-Family housing above 10 floors",
                Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createShoringProjectsWithAllFields() {
        ProjectTestUtil.createFullProject("3004", "Not a DUO PS100", Structure.Shoring, 87.3, 20.0, 20.0, 330.0, 50.0, 200.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("1200", "Not a DUO PS100", Structure.Shoring, 87.3, 20.0, 20.0, 330.0, 20.0, 10.0, "DUO", SegmentLevelOne.Infrastructure, "Tunnels", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }

    protected void createNonResidentialProjectsWithAllFields() {
        ProjectTestUtil.createFullProject("3232", "DUO NonResidential", Structure.Column,
                120.0, 40.0, 40.0, 120.0, 50.0, 200.0, "DUO", SegmentLevelOne.NonResidential,
                "Healthcare Buildings", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
        ProjectTestUtil.createFullProject("3232", "DUO NonResidentialWrongLevelTwo", Structure.Column,
                120.0, 40.0, 40.0, 120.0, 50.0, 200.0, "DUO", SegmentLevelOne.NonResidential,
                "Leisure & Hospitality Buildings", Arrays.asList("Basement", "Anchor To Existing Wall", "Shaft"));
    }
}