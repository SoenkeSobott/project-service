package org.soenke.sobott;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;

@Path("/warehouse")
public class WarehouseResource {

    @Inject
    WarehouseService warehouseService;

    @GET
    @Path("/articles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchArticles(@QueryParam(value = "searchTerm") String searchTerm) {
        return warehouseService.searchArticles(searchTerm);
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

    @POST
    @Path("/articles")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadWeeklyArticles(final Map<String, InputStream> parts) {
        return warehouseService.uploadWeeklyArticles(parts);
    }

}