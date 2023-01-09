package org.soenke.sobott;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/projects")
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects(
            @QueryParam(value = "product") String product,
            @QueryParam(value = "minThickness") Double minThickness,
            @QueryParam(value = "maxThickness") Double maxThickness,
            @QueryParam(value = "minHeight") Double minHeight,
            @QueryParam(value = "maxHeight") Double maxHeight
    ) {
        return projectService.getProjects(product, minThickness, maxThickness, minHeight, maxHeight);
    }
}