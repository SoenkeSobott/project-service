package org.soenke.sobott;

import org.soenke.sobott.entity.FilterPojo;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/projects")
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectsFiltered(FilterPojo filters) {
        return projectService.getProjects(filters);
    }

    @POST
    @Path("/solution-tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolutionTags(FilterPojo filters) {
        return projectService.getSolutionTags(filters);
    }

    @GET
    @Path("/solution-tags/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllSolutionTags() {
        return projectService.getAllSolutionTagsInProjects();
    }
}