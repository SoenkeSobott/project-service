package org.soenke.sobott.filter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class StructureFilterTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
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
    public void testProjectsEndpointColumnLengthFiltered() {
        createProjectWithStructureLengthWidthAndHeight("1231", "DUO-1", "Column", 20.0, 20.0, 230.0);
        createProjectWithStructureLengthWidthAndHeight("2344", "DUO-2", "Column", 30.0, 20.0, 330.0);
        createProjectWithStructureLengthWidthAndHeight("9696", "DUO-3", "Column", 40.0, 20.0, 430.0);
        createProjectWithStructureLengthWidthAndHeight("8493", "DUO-4", "Wall", 50.0, 20.0, 530.0);
        createProjectWithStructureLengthWidthAndHeight("1230", "DUO-5", "Column", 60.0, 20.0, 630.0);

        String filterJson = "{\"columnFilter\": {\"minLength\":30, \"maxLength\":59.0}}";
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
    public void testProjectsEndpointColumnWidthFiltered() {
        createProjectWithStructureLengthWidthAndHeight("1231", "DUO-1", "Column", 20.0, 20.0, 230.0);
        createProjectWithStructureLengthWidthAndHeight("2344", "DUO-2", "Column", 30.0, 30.0, 330.0);
        createProjectWithStructureLengthWidthAndHeight("9696", "DUO-3", "Wall", 40.0, 50.0, 430.0);
        createProjectWithStructureLengthWidthAndHeight("8493", "DUO-4", "Column", 50.0, 70.0, 530.0);
        createProjectWithStructureLengthWidthAndHeight("1230", "DUO-5", "Column", 60.0, 120.0, 630.0);

        String filterJson = "{\"columnFilter\": {\"minWidth\":30, \"maxWidth\":119.0}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-4"));
    }

    @Test
    public void testProjectsEndpointColumnHeightFiltered() {
        createProjectWithStructureLengthWidthAndHeight("1231", "DUO-1", "Column", 20.0, 20.0, 230.0);
        createProjectWithStructureLengthWidthAndHeight("2344", "DUO-2", "Column", 30.0, 30.0, 330.0);
        createProjectWithStructureLengthWidthAndHeight("9696", "DUO-3", "Wall", 40.0, 50.0, 430.0);
        createProjectWithStructureLengthWidthAndHeight("8493", "DUO-4", "Column", 50.0, 70.0, 530.0);
        createProjectWithStructureLengthWidthAndHeight("1230", "DUO-5", "Column", 60.0, 120.0, 630.0);

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
                .body("[2].projectName", is("DUO-4"));
    }


    @Test
    public void testProjectsEndpointColumnLengthWidthAndHeightFiltered() {
        createProjectWithStructureLengthWidthAndHeight("1231", "DUO-1", "Column", 20.0, 20.0, 230.0);
        createProjectWithStructureLengthWidthAndHeight("2344", "DUO-2", "Column", 30.0, 30.0, 330.0);
        createProjectWithStructureLengthWidthAndHeight("9696", "DUO-3", "Wall", 40.0, 50.0, 430.0);
        createProjectWithStructureLengthWidthAndHeight("8493", "DUO-4", "Column", 50.0, 70.0, 530.0);
        createProjectWithStructureLengthWidthAndHeight("1230", "DUO-5", "Column", 59.0, 119.0, 610.0);
        createProjectWithStructureLengthWidthAndHeight("3232", "DUO-5", "Column", 59.0, 119.0, 630.0);
        createProjectWithStructureLengthWidthAndHeight("1123", "DUO-5", "Column", 60.0, 120.0, 730.0);

        String filterJson = "{\"columnFilter\": {\"minLength\":30, \"maxLength\":59.0, " +
                "\"minWidth\":30, \"maxWidth\":119.0," +
                "\"minHeight\":0, \"maxHeight\":629}}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-4"))
                .body("[2].projectName", is("DUO-5"));
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

    protected void createProjectWithStructureLengthWidthAndHeight(String projectNumber, String projectName, String structure, Double length, Double width, Double height) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure);
        project.setLength(length);
        project.setWidth(width);
        project.setHeight(height);
        project.persist();
    }
}
