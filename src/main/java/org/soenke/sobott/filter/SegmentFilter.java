package org.soenke.sobott.filter;

import org.soenke.sobott.enums.SegmentLevelOne;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SegmentFilter {

    public String generateSegmentFilterQuery(List<String> infrastructureElements,
                                             List<String> industrialElements,
                                             List<String> residentialElements,
                                             List<String> nonResidentialElements) {
        String filterQuery = "{$or: [";
        if (infrastructureElements != null && infrastructureElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Infrastructure, infrastructureElements);
        }
        if (industrialElements != null && industrialElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Industrial, industrialElements);
        }
        if (residentialElements != null && residentialElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Residential, residentialElements);
        }
        if (nonResidentialElements != null && nonResidentialElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.NonResidential, nonResidentialElements);
        }
        if (filterQuery.equals("{$or: [")) {
            // Nothing was added to query
            return "";
        }
        filterQuery += "]},";
        return filterQuery;
    }

    private String generateSpecificSegmentFilterQuery(SegmentLevelOne segmentLevelOne, List<String> elements) {
        String filterQuery = "";
        filterQuery += "{$and: [{segmentLevelOne: \"" + segmentLevelOne.getValue() + "\"},";
        filterQuery += "{segmentLevelTwo: { $in: [" + FilterUtils.wrapWithQuotesAndJoin(elements) + "]}}]},";
        return filterQuery;
    }

}
