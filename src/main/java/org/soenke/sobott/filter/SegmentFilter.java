package org.soenke.sobott.filter;

import org.soenke.sobott.enums.SegmentLevelOne;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SegmentFilter {

    public String generateSegmentFilterQuery(List<String> infrastructureElements,
                                             List<String> industrialElements) {
        String filterQuery = "";
        if (infrastructureElements != null && infrastructureElements.size() > 0
                && industrialElements != null && industrialElements.size() > 0) {
            filterQuery += "{$or: [";
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Infrastructure, infrastructureElements);
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Industrial, industrialElements);
            filterQuery += "]},";
        } else if (infrastructureElements != null && infrastructureElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Infrastructure, infrastructureElements);
        } else if (industrialElements != null && industrialElements.size() > 0) {
            filterQuery += generateSpecificSegmentFilterQuery(SegmentLevelOne.Industrial, industrialElements);
        }
        return filterQuery;
    }

    private String generateSpecificSegmentFilterQuery(SegmentLevelOne segmentLevelOne, List<String> elements) {
        String filterQuery = "";
        filterQuery += "{$and: [{segmentLevelOne: \"" + segmentLevelOne + "\"},";
        filterQuery += "{segmentLevelTwo: { $in: [" + FilterUtils.wrapWithQuotesAndJoin(elements) + "]}}]},";
        return filterQuery;
    }

}
