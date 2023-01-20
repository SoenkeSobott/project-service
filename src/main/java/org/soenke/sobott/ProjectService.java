package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.HeightAndThicknessFilterPojo;
import org.soenke.sobott.entity.LengthWidthAndHeightFilterPojo;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.Segment;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

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

        filterQuery += generateStructureFilterQuery(wallFilter, columnFilter);
        filterQuery += generateSegmentFilterQuery(infrastructureElements, industrialElements);

        if (solutionTags != null && solutionTags.size() > 0) {
            filterQuery += "{solutionTags: { $in: [" + wrapWithQuotesAndJoin(solutionTags) + "]}},";
        }

        if (!filterQuery.equals("{$and: [")) {
            filterQuery += "]}";
            return filterQuery;
        } else {
            return null;
        }
    }

    private String generateStructureFilterQuery(HeightAndThicknessFilterPojo wallFilter, LengthWidthAndHeightFilterPojo columnFilter) {
        String filterQuery = "";
        if (wallFilter != null && columnFilter != null) {
            filterQuery += "{$or: [";
            filterQuery += generateWallFilterQuery(wallFilter);
            filterQuery += generateColumnFilterQuery(columnFilter);
            if (filterQuery.equals("{$or: [")) {
                // Nothing was added to query
                return "";
            }
            filterQuery += "]},";
        } else if (wallFilter != null) {
            filterQuery += generateWallFilterQuery(wallFilter);
        } else if (columnFilter != null) {
            filterQuery += generateColumnFilterQuery(columnFilter);
        }
        return filterQuery;
    }

    private String generateWallFilterQuery(HeightAndThicknessFilterPojo wallFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery("Wall", "thickness", wallFilter.getMinThickness(), wallFilter.getMaxThickness());
        filterQuery += generateMinMaxFilterQuery("Wall", "height", wallFilter.getMinHeight(), wallFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateColumnFilterQuery(LengthWidthAndHeightFilterPojo columnFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery("Column", "length", columnFilter.getMinLength(), columnFilter.getMaxLength());
        filterQuery += generateMinMaxFilterQuery("Column", "width", columnFilter.getMinWidth(), columnFilter.getMaxWidth());
        filterQuery += generateMinMaxFilterQuery("Column", "height", columnFilter.getMinHeight(), columnFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateMinMaxFilterQuery(String structure, String metric, Double min, Double max) {
        String query = "{$and: [{mainStructure: \"" + structure + "\"},";
        if (min != null && max != null) {
            return query + "{" + metric + ": {$gte:" + min + ", $lte:" + max + "}}]},";
        } else if (min != null) {
            return query + "{" + metric + ": {$gte:" + min + "}}]},";
        } else if (max != null) {
            return query + "{" + metric + ": {$lte:" + max + "}}]},";
        } else {
            return "";
        }
    }

    private String generateSegmentFilterQuery(List<String> infrastructureElements,
                                              List<String> industrialElements) {
        String filterQuery = "";
        if (infrastructureElements != null && infrastructureElements.size() > 0
                && industrialElements != null && industrialElements.size() > 0) {
            filterQuery += "{$or: [";
            filterQuery += generateSpecificSegmentFilterQuery(Segment.Infrastructure, infrastructureElements);
            filterQuery += generateSpecificSegmentFilterQuery(Segment.Industrial, industrialElements);
            filterQuery += "]},";
        } else if (infrastructureElements != null && infrastructureElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(Segment.Infrastructure, infrastructureElements);
        } else if (industrialElements != null && industrialElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(Segment.Industrial, industrialElements);
        }
        return filterQuery;
    }

    private String generateSpecificSegmentFilterQuery(Segment segment, List<String> elements) {
        String filterQuery = "";
        filterQuery += "{$and: [{segmentLevelOne: \"" + segment + "\"},";
        filterQuery += "{segmentLevelTwo: { $in: [" + wrapWithQuotesAndJoin(elements) + "]}}]},";
        return filterQuery;
    }

    private String wrapWithQuotesAndJoin(List<String> strings) {
        return strings.stream()
                .collect(Collectors.joining("\", \"", "\"", "\""));
    }

}
