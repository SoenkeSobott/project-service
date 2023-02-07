package org.soenke.sobott;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/warehouse")
public class WarehouseResource {

    @Inject
    WarehouseService warehouseService;

    @GET
    @Path("/articles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArticles() {
        return warehouseService.getAllArticles();
    }

    @POST
    @Path("/articles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateArticleQuantity(@QueryParam(value = "articleNumber") String articleNumber,
                                          @QueryParam(value = "newQuantity") Integer newQuantity) {
        return warehouseService.updateArticleQuantity(articleNumber, newQuantity);
    }

    @GET
    @Path("/articles/availability")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticlesAvailability(@QueryParam(value = "articleNumbers") String articleNumbers) {
        return warehouseService.getArticlesAvailability(articleNumbers);
    }
}