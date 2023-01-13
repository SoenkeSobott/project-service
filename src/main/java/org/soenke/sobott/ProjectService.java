package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.Project;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    public Response getProjects(FilterPojo filters) {
        if (filters != null) {
            String filterQuery = generateFilterQuery(
                    filters.getSearchTerm(),
                    filters.getProduct(),
                    filters.getMinThickness(),
                    filters.getMaxThickness(),
                    filters.getMinHeight(),
                    filters.getMaxHeight());
            if (filterQuery != null) {
                return Response.status(Response.Status.OK).entity(Project.find(filterQuery).list()).build();
            }
        }

        return Response.status(Response.Status.OK).entity(Project.listAll()).build();
    }

    private String generateFilterQuery(String searchTerm,
                                       String product,
                                       Double minThickness,
                                       Double maxThickness,
                                       Double minHeight,
                                       Double maxHeight) {
        String filterQuery = "{";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchQuery = "$text: { $search: \"" + searchTerm + "\", $caseSensitive: false},";
            filterQuery += searchQuery;
        }

        if (product != null && !product.isEmpty()) {
            filterQuery += "product: \"" + product.toUpperCase() + "\",";
        }

        filterQuery += generateThicknessFilterQuery(minThickness, maxThickness);
        filterQuery += generateHeightFilterQuery(minHeight, maxHeight);

        if (!filterQuery.equals("{")) {
            filterQuery += "}";
            return filterQuery;
        } else {
            return null;
        }
    }

    private String generateThicknessFilterQuery(Double minThickness, Double maxThickness) {
        if (minThickness != null && maxThickness != null) {
            return "thickness: {$gte:" + minThickness + ", $lte:" + maxThickness + "},";
        } else if (minThickness != null) {
            return "thickness: {$gte:" + minThickness + "},";
        } else if (maxThickness != null) {
            return "thickness: {$lte:" + maxThickness + "},";
        } else {
            return "";
        }
    }

    private String generateHeightFilterQuery(Double minHeight, Double maxHeight) {
        if (minHeight != null && maxHeight != null) {
            return "height: {$gte:" + minHeight + ", $lte:" + maxHeight + "},";
        } else if (minHeight != null) {
            return "height: {$gte:" + minHeight + "},";
        } else if (maxHeight != null) {
            return "height: {$lte:" + maxHeight + "},";
        } else {
            return "";
        }
    }

}
