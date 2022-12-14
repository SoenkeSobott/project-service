package org.soenke.sobott;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class ProjectResourceTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
    }

    @Test
    public void testProjectEndpoint() {
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

    protected void createProject(String projectNumber, String projectName) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.persist();
    }

}