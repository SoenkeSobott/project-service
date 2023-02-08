package org.soenke.sobott;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

//    @POST
//    @Path("/articles")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateArticleQuantity(@QueryParam(value = "articleNumber") String articleNumber,
//                                          @QueryParam(value = "newAvailability") Integer newAvailability) {
//        return warehouseService.updateArticleAvailability(articleNumber, newAvailability);
//    }

    @GET
    @Path("/articles/availability")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticlesAvailability(@QueryParam(value = "articleNumbers") String articleNumbers) {
        return warehouseService.getArticlesAvailability(articleNumbers);
    }
}