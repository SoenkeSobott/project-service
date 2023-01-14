package org.soenke.sobott.entity;

import java.util.List;

public class FilterPojo {

    private String searchTerm;
    private String product;
    private HeightAndThicknessFilterPojo wallFilter;
    private HeightAndThicknessFilterPojo columnFilter;
    private List<String> infrastructureElements;

    // Getters & Setters

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public HeightAndThicknessFilterPojo getWallFilter() {
        return wallFilter;
    }

    public void setWallFilter(HeightAndThicknessFilterPojo wallFilter) {
        this.wallFilter = wallFilter;
    }

    public HeightAndThicknessFilterPojo getColumnFilter() {
        return columnFilter;
    }

    public void setColumnFilter(HeightAndThicknessFilterPojo columnFilter) {
        this.columnFilter = columnFilter;
    }

    public List<String> getInfrastructureElements() {
        return infrastructureElements;
    }

    public void setInfrastructureElements(List<String> infrastructureElements) {
        this.infrastructureElements = infrastructureElements;
    }
}


