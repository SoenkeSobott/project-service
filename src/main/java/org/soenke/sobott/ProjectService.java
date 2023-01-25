package org.soenke.sobott;

import com.google.gson.Gson;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.FilterPojo;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.SolutionTag;
import org.soenke.sobott.filter.FilterUtils;
import org.soenke.sobott.filter.ProductFilter;
import org.soenke.sobott.filter.SegmentFilter;
import org.soenke.sobott.filter.StructureFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    @Inject
    ProductFilter productFilter;

    @Inject
    StructureFilter structureFilter;

    @Inject
    SegmentFilter segmentFilter;

    public Response getProjects(FilterPojo filters) {
        if (filters != null) {
            String filterQuery = generateFilterQuery(filters);
            if (filterQuery != null) {
                return Response.status(Response.Status.OK).entity(Project.find(filterQuery).list()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(Project.listAll()).build();
    }

    public Response getSolutionTags(FilterPojo filters) {
        List<String> solutionTags = new ArrayList<>();
        if (filters != null) {
            String filterQuery = generateFilterQuery(filters);
            if (filterQuery != null) {
                List<Project> projects = Project.find(filterQuery).list();
                projects.stream()
                        .map(Project::getSolutionTags)
                        .flatMap(Collection::stream)
                        .distinct()
                        .forEach(solutionTags::add);
            } else {
                solutionTags = getAllSolutionTags();
            }
        } else {
            solutionTags = getAllSolutionTags();
        }

        String solutionTagsJsonArray = new Gson().toJson(solutionTags);
        return Response.status(Response.Status.OK).entity("{\"solutionTags\": " + solutionTagsJsonArray + "}").build();
    }

    public List<String> getAllSolutionTagsInProjects() {
        List<String> solutionTags = new ArrayList<>();
        List<Project> projects = Project.findAll().list();
        projects.stream()
                .map(Project::getSolutionTags)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(solutionTags::add);
        return solutionTags;
    }

    private String generateFilterQuery(FilterPojo filters) {
        String filterQuery = "{$and: [";

        String searchTerm = filters.getSearchTerm();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchQuery = "{$text: { $search: \"" + searchTerm + "\", $caseSensitive: false}},";
            filterQuery += searchQuery;
        }

        filterQuery += productFilter.generateProductFilterQuery(filters.getProduct());
        filterQuery += structureFilter.generateStructureFilterQuery(filters.getWallFilter(), filters.getColumnFilter(),
                filters.getCulvertFilter(), filters.getShoringFilter());
        filterQuery += segmentFilter.generateSegmentFilterQuery(filters.getInfrastructureElements(), filters.getIndustrialElements(),
                filters.getResidentialElements(), filters.getNonResidentialElements());

        List<String> solutionTags = filters.getSolutionTags();
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

    protected List<String> getAllSolutionTags() {
        return Stream.of(SolutionTag.values())
                .map(SolutionTag::getValue)
                .collect(Collectors.toList());
    }
}
