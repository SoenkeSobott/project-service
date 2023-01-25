package org.soenke.sobott.filter;

import org.soenke.sobott.entity.HeightAndThicknessFilterPojo;
import org.soenke.sobott.entity.LengthWidthAndHeightFilterPojo;
import org.soenke.sobott.enums.Structure;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StructureFilter {

    public String generateStructureFilterQuery(HeightAndThicknessFilterPojo wallFilter,
                                               LengthWidthAndHeightFilterPojo columnFilter,
                                               HeightAndThicknessFilterPojo culvertFilter,
                                               HeightAndThicknessFilterPojo shoringFilter) {
        String filterQuery = "{$or: [";
        if (wallFilter != null) {
            filterQuery += generateWallFilterQuery(wallFilter);
        }
        if (columnFilter != null) {
            filterQuery += generateColumnFilterQuery(columnFilter);
        }
        if (culvertFilter != null) {
            filterQuery += generateCulvertFilterQuery(culvertFilter);
        }
        if (shoringFilter != null) {
            filterQuery += generateShoringFilterQuery(shoringFilter);
        }
        if (filterQuery.equals("{$or: [")) {
            // Nothing was added to query
            return "";
        }
        filterQuery += "]},";
        return filterQuery;
    }

    private String generateWallFilterQuery(HeightAndThicknessFilterPojo wallFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery(Structure.Wall, "thickness", wallFilter.getMinThickness(), wallFilter.getMaxThickness());
        filterQuery += generateMinMaxFilterQuery(Structure.Wall, "height", wallFilter.getMinHeight(), wallFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateColumnFilterQuery(LengthWidthAndHeightFilterPojo columnFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery(Structure.Column, "length", columnFilter.getMinLength(), columnFilter.getMaxLength());
        filterQuery += generateMinMaxFilterQuery(Structure.Column, "width", columnFilter.getMinWidth(), columnFilter.getMaxWidth());
        filterQuery += generateMinMaxFilterQuery(Structure.Column, "height", columnFilter.getMinHeight(), columnFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateCulvertFilterQuery(HeightAndThicknessFilterPojo culvertFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery(Structure.Culvert, "thickness", culvertFilter.getMinThickness(), culvertFilter.getMaxThickness());
        filterQuery += generateMinMaxFilterQuery(Structure.Culvert, "height", culvertFilter.getMinHeight(), culvertFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateShoringFilterQuery(HeightAndThicknessFilterPojo shoringFilter) {
        String filterQuery = "{$and: [";
        filterQuery += generateMinMaxFilterQuery(Structure.Shoring, "slabThickness", shoringFilter.getMinThickness(), shoringFilter.getMaxThickness());
        filterQuery += generateMinMaxFilterQuery(Structure.Shoring, "shoringHeight", shoringFilter.getMinHeight(), shoringFilter.getMaxHeight());
        if (filterQuery.equals("{$and: [")) {
            // Nothing was added to query
            return "";
        }
        return filterQuery + "]}";
    }

    private String generateMinMaxFilterQuery(Structure structure, String metric, Double min, Double max) {
        String query = "{$and: [{mainStructure: \"" + structure.getValue() + "\"},";
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
