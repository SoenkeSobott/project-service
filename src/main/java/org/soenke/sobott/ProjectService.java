package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.HeightAndThicknessFilterPojo;
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
                                       HeightAndThicknessFilterPojo columnFilter,
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

    private String generateStructureFilterQuery(HeightAndThicknessFilterPojo wallFilter, HeightAndThicknessFilterPojo columnFilter) {
        String filterQuery = "";
        if (wallFilter != null && columnFilter != null) {
            filterQuery += "{$or: [";
            filterQuery += generateWallFilterQuery(wallFilter);
            filterQuery += generateColumnFilterQuery(columnFilter);
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
        filterQuery += generateThicknessFilterQuery("Wall", wallFilter.getMinThickness(), wallFilter.getMaxThickness());
        filterQuery += generateHeightFilterQuery("Wall", wallFilter.getMinHeight(), wallFilter.getMaxHeight());
        return filterQuery + "]}";
    }

    private String generateColumnFilterQuery(HeightAndThicknessFilterPojo columnFilter) {
        String filterQuery = "";
        filterQuery += generateThicknessFilterQuery("Column", columnFilter.getMinThickness(), columnFilter.getMaxThickness());
        filterQuery += generateHeightFilterQuery("Column", columnFilter.getMinHeight(), columnFilter.getMaxHeight());
        return filterQuery;
    }

    private String generateThicknessFilterQuery(String structure, Double minThickness, Double maxThickness) {
        String query = "{$and: [{mainStructure: \"" + structure + "\"},";
        if (minThickness != null && maxThickness != null) {
            return query + "{thickness: {$gte:" + minThickness + ", $lte:" + maxThickness + "}}]},";
        } else if (minThickness != null) {
            return query + "{thickness: {$gte:" + minThickness + "}}]},";
        } else if (maxThickness != null) {
            return query + "{thickness: {$lte:" + maxThickness + "}}]},";
        } else {
            return "";
        }
    }

    private String generateHeightFilterQuery(String structure, Double minHeight, Double maxHeight) {
        String query = "{$and: [{mainStructure: \"" + structure + "\"},";
        if (minHeight != null && maxHeight != null) {
            return query + "{height: {$gte:" + minHeight + ", $lte:" + maxHeight + "}}]},";
        } else if (minHeight != null) {
            return query + "{height: {$gte:" + minHeight + "}}]},";
        } else if (maxHeight != null) {
            return query + "{height: {$lte:" + maxHeight + "}}]},";
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
