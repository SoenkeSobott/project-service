package org.soenke.sobott.filter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.ProjectTestUtil;
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
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Infrastructure, "Bridges");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Infrastructure, "Tunnels");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Infrastructure, "Water Plants");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Infrastructure, "Airports");

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
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Industrial, "Power");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Industrial, "Chemicals");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Industrial, "Industrialized Manufacturing");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Industrial, "Oil & Gas");

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
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.Residential, "Multi-Family housing above 10 floors");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.Residential, "Multi-Family housing up to 10 floors");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.Residential, "Single-Family Housing");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.Residential, "Multi-Family housing up to 10 floors");

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

    @Test
    public void testProjectsEndpointSegmentNonResidentialFiltered() {
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("3423", "DUO-1", SegmentLevelOne.NonResidential, "Office Buildings");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("9900", "DUO-2", SegmentLevelOne.NonResidential, "Retail Buildings");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("2341", "DUO-3", SegmentLevelOne.NonResidential, "Leisure & Hospitality Buildings");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("0993", "DUO-4", SegmentLevelOne.NonResidential, "Transportation & Logistics Buildings");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("0989", "DUO-5", SegmentLevelOne.NonResidential, "Cultural & Institutional Buildings");
        ProjectTestUtil.createProjectWithSegmentLevelOneAndTwo("2412", "DUO-6", SegmentLevelOne.NonResidential, "Healthcare Buildings");

        String filterJson = "{\"nonResidentialElements\": [\"Healthcare Buildings\", \"Transportation & Logistics Buildings\", \"Leisure & Hospitality Buildings\", \"Office Buildings\"]}";
        given()
                .contentType("application/json")
                .body(filterJson)
                .when().post("/projects")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("[0].projectName", is("DUO-1"))
                .body("[1].projectName", is("DUO-3"))
                .body("[2].projectName", is("DUO-4"))
                .body("[3].projectName", is("DUO-6"));
    }
}
