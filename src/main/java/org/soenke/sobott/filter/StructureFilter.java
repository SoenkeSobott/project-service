package org.soenke.sobott.filter;

import org.soenke.sobott.entity.HeightAndThicknessFilterPojo;
import org.soenke.sobott.entity.LengthWidthAndHeightFilterPojo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StructureFilter {

    public String generateStructureFilterQuery(HeightAndThicknessFilterPojo wallFilter, LengthWidthAndHeightFilterPojo columnFilter) {
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

}
