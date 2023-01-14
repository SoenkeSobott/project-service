package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.entity.WallFilterPojo;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    public Response getProjects(FilterPojo filters) {
        if (filters != null) {
            String filterQuery = generateFilterQuery(
                    filters.getSearchTerm(),
                    filters.getProduct(),
                    filters.getWallFilter());
            if (filterQuery != null) {
                return Response.status(Response.Status.OK).entity(Project.find(filterQuery).list()).build();
            }
        }

        return Response.status(Response.Status.OK).entity(Project.listAll()).build();
    }

    private String generateFilterQuery(String searchTerm,
                                       String product,
                                       WallFilterPojo wallFilter) {
        String filterQuery = "{";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchQuery = "$text: { $search: \"" + searchTerm + "\", $caseSensitive: false},";
            filterQuery += searchQuery;
        }

        if (product != null && !product.isEmpty()) {
            filterQuery += "product: \"" + product.toUpperCase() + "\",";
        }

        if (wallFilter != null) {
            filterQuery += generateThicknessFilterQuery(wallFilter.getMinThickness(), wallFilter.getMaxThickness());
            filterQuery += generateHeightFilterQuery(wallFilter.getMinHeight(), wallFilter.getMaxHeight());
        }

        if (!filterQuery.equals("{")) {
            filterQuery += "}";
            return filterQuery;
        } else {
            return null;
        }
    }

    private String generateThicknessFilterQuery(Double minThickness, Double maxThickness) {
        String query = "$and: [{mainStructure: \"Wall\"},";
        if (minThickness != null && maxThickness != null) {
            return query + "{thickness: {$gte:" + minThickness + ", $lte:" + maxThickness + "}}],";
        } else if (minThickness != null) {
            return query + "{thickness: {$gte:" + minThickness + "}}],";
        } else if (maxThickness != null) {
            return query + "{thickness: {$lte:" + maxThickness + "}}],";
        } else {
            return "";
        }
    }

    private String generateHeightFilterQuery(Double minHeight, Double maxHeight) {
        String query = "$and: [{mainStructure: \"Wall\"},";
        if (minHeight != null && maxHeight != null) {
            return query + "{height: {$gte:" + minHeight + ", $lte:" + maxHeight + "}}],";
        } else if (minHeight != null) {
            return query + "{height: {$gte:" + minHeight + "}}],";
        } else if (maxHeight != null) {
            return query + "{height: {$lte:" + maxHeight + "}}],";
        } else {
            return "";
        }
    }

}
