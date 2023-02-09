package org.soenke.sobott;

import com.google.gson.Gson;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.BillOfQuantityEntry;
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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ProjectService implements PanacheMongoRepository<Project> {

    Logger LOGGER = Logger.getLogger(ProjectService.class.getName());

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
                List<Project> projects = Project.find(filterQuery).list();
                projects.sort(Comparator.comparing(Project::getProjectName));
                return Response.status(Response.Status.OK).entity(projects).build();
            }
        }
        return Response.status(Response.Status.OK).entity(Project.listAll(Sort.by("projectName"))).build();
    }

    public Response getProjectPrice(String projectNumber) {
        Project project = Project.findByProjectNumber(projectNumber);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Project with Project number: '" + projectNumber + "' not found.").build();
        }

        List<BillOfQuantityEntry> billOfQuantityEntries = project.getBillOfQuantity();
        if (billOfQuantityEntries == null || billOfQuantityEntries.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Project with Project number: '" + projectNumber + "' has no BQ.").build();
        }

        // Calculate price
        Double projectPrice = 0.0;
        for (BillOfQuantityEntry entry : billOfQuantityEntries) {
            String articleNumber = entry.getArticleNumber();
            Article article = Article.findByArticleNumber(articleNumber);
            if (article == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Couldn't find article from BQ with Article number: " + articleNumber).build();
            }

            Float articlePrice = article.getListPrice();
            Integer quantity = entry.getQuantity();
            Response response = checkArticlePriceAndQuantityAndReturnErrorResponseIfNecessary(articlePrice, quantity, articleNumber);
            if (response != null) {
                return response;
            }

            projectPrice += (articlePrice * quantity);
        }

        // Get unit
        String product = project.getProduct();
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Couldn't find Product unit(m2/m3) for Project number: " + projectNumber).build();
        }
        String unit = getUnitFromProduct(product);

        String responseJson = "{\"price\": " + projectPrice + "," +
                "\"currency\": \"HKD\"," +
                "\"unit\": \"" + unit + "\"}";
        return Response.status(Response.Status.OK).entity(responseJson).build();
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

    // Helper methods

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

    protected Response checkArticlePriceAndQuantityAndReturnErrorResponseIfNecessary(Float articlePrice, Integer quantity, String articleNumber) {
        if (articlePrice == null || articlePrice == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Article price is not present or zero for Article number: " + articleNumber).build();
        }

        if (quantity == null || quantity == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("BQ entry quantity is not present or zero for BQ entry Article number: " + articleNumber).build();
        }
        return null;
    }

    protected String getUnitFromProduct(String product) {
        if (product.equals("PS100")) {
            return "M3";
        }
        if (product.equals("DUO")) {
            return "M2";
        }
        LOGGER.log(Level.WARNING, "No Product found for product '" + product + "', using default 'M2'");
        return "M2"; // Default
    }

    protected List<String> getAllSolutionTags() {
        return Stream.of(SolutionTag.values())
                .map(SolutionTag::getValue)
                .collect(Collectors.toList());
    }
}
