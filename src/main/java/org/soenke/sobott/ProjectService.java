package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.HeightAndThicknessFilterPojo;
import org.soenke.sobott.entity.LengthWidthAndHeightFilterPojo;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.filter.FilterUtils;
import org.soenke.sobott.filter.SegmentFilter;
import org.soenke.sobott.filter.StructureFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    @Inject
    StructureFilter structureFilter;

    @Inject
    SegmentFilter segmentFilter;

    public Response getProjects(FilterPojo filters) {
        if (filters != null) {
            String filterQuery = generateFilterQuery(
                    filters.getSearchTerm(),
                    filters.getProduct(),
                    filters.getWallFilter(),
                    filters.getColumnFilter(),
                    filters.getInfrastructureElements(),
                    filters.getIndustrialElements(),
                    filters.getSolutionTags());
            if (filterQuery != null) {
                return Response.status(Response.Status.OK).entity(Project.find(filterQuery).list()).build();
            }
        }

        return Response.status(Response.Status.OK).entity(Project.listAll()).build();
    }

    private String generateFilterQuery(String searchTerm,
                                       String product,
                                       HeightAndThicknessFilterPojo wallFilter,
                                       LengthWidthAndHeightFilterPojo columnFilter,
                                       List<String> infrastructureElements,
                                       List<String> industrialElements,
                                       List<String> solutionTags) {
        String filterQuery = "{$and: [";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchQuery = "{$text: { $search: \"" + searchTerm + "\", $caseSensitive: false}},";
            filterQuery += searchQuery;
        }

        if (product != null && !product.isEmpty()) {
            filterQuery += "{product: \"" + product.toUpperCase() + "\"},";
        }

        filterQuery += structureFilter.generateStructureFilterQuery(wallFilter, columnFilter);
        filterQuery += segmentFilter.generateSegmentFilterQuery(infrastructureElements, industrialElements);

        if (solutionTags != null && solutionTags.size() > 0) {
            filterQuery += "{solutionTags: { $in: [" + FilterUtils.wrapWithQuotesAndJoin(solutionTags) + "]}},";
        }

        if (!filterQuery.equals("{$and: [")) {
            filterQuery += "]}";
            return filterQuery;
        } else {
            return null;
        }
    }

}
