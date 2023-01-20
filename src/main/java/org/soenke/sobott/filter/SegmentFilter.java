package org.soenke.sobott.filter;

import org.soenke.sobott.enums.Segment;

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
        filterQuery += "{segmentLevelTwo: { $in: [" + FilterUtils.wrapWithQuotesAndJoin(elements) + "]}}]},";
        return filterQuery;
    }

}
