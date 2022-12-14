package org.soenke.sobott;

import org.soenke.sobott.entity.Project;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/projects")
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }
}