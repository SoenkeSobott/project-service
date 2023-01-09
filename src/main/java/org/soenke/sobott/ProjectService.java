package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Project;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    public Response getProjects(String product, Double thickness) {
        String filterQuery = generateFilterQuery(product, thickness);

        if (filterQuery != null) {
            return Response.status(Response.Status.OK).entity(Project.find(filterQuery).list()).build();
        }
        return Response.status(Response.Status.OK).entity(Project.listAll()).build();
    }

    private String generateFilterQuery(String product, Double thickness) {
        String filterQuery = "{";
        if (thickness != null) {
            filterQuery += "thickness: {$gte:" + thickness + "},";
        }
        if (product != null && !product.isEmpty()) {
            filterQuery += "product: \"" + product.toUpperCase() + "\",";
        }

        if (!filterQuery.equals("{")) {
            filterQuery += "}";
            return filterQuery;
        } else {
            return null;
        }
    }

}
