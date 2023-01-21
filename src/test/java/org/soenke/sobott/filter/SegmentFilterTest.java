package org.soenke.sobott.filter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.SegmentLevelOne;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SegmentFilterTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @Test
    public void testProjectsEndpointSegmentInfrastructureFiltered() {
        createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Infrastructure, "Bridges");
        createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Infrastructure, "Tunnels");
        createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Infrastructure, "Water Plants");
        createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Infrastructure, "Airports");

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
        createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Industrial, "Power");
        createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Industrial, "Chemicals");
        createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Industrial, "Industrialized Manufacturing");
        createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Industrial, "Oil & Gas");

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
    public void testProjectsEndpointSegmentResidentialFiltered() {
        createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Residential, "Multi-Family housing above 10 floors");
        createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Residential, "Multi-Family housing up to 10 floors");
        createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Residential, "Single-Family Housing");
        createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Residential, "Multi-Family housing up to 10 floors");

        String filterJson = "{\"residentialElements\": [\"Multi-Family housing up to 10 floors\", \"Single-Family Housing\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].projectName", is("DUO-2"))
                .body("[1].projectName", is("DUO-3"))
                .body("[2].projectName", is("DUO-4"));
    }

    protected void createProjectWithSegmentLevelOneAndTwo(String projectNumber, String projectName, SegmentLevelOne segmentLevelOne,
                                                          String segmentLevelTwo) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setSegmentLevelOne(segmentLevelOne.getValue());
        project.setSegmentLevelTwo(segmentLevelTwo);
        project.persist();
    }
}
