package org.soenke.sobott.entity;

import java.util.List;

public class FilterPojo {

    private String searchTerm;
    private String product;
    private HeightAndThicknessFilterPojo wallFilter;
    private LengthWidthAndHeightFilterPojo columnFilter;
    private HeightAndThicknessFilterPojo culvertFilter;
    private List<String> infrastructureElements;
    private List<String> industrialElements;
    private List<String> residentialElements;
    private List<String> nonResidentialElements;
    private List<String> solutionTags;

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

    public LengthWidthAndHeightFilterPojo getColumnFilter() {
        return columnFilter;
    }

    public void setColumnFilter(LengthWidthAndHeightFilterPojo columnFilter) {
        this.columnFilter = columnFilter;
    }

    public HeightAndThicknessFilterPojo getCulvertFilter() {
        return culvertFilter;
    }

    public void setCulvertFilter(HeightAndThicknessFilterPojo culvertFilter) {
        this.culvertFilter = culvertFilter;
    }

    public List<String> getInfrastructureElements() {
        return infrastructureElements;
    }

    public void setInfrastructureElements(List<String> infrastructureElements) {
        this.infrastructureElements = infrastructureElements;
    }

    public List<String> getIndustrialElements() {
        return industrialElements;
    }

    public void setIndustrialElements(List<String> industrialElements) {
        this.industrialElements = industrialElements;
    }

    public List<String> getResidentialElements() {
        return residentialElements;
    }

    public void setResidentialElements(List<String> residentialElements) {
        this.residentialElements = residentialElements;
    }

    public List<String> getNonResidentialElements() {
        return nonResidentialElements;
    }

    public void setNonResidentialElements(List<String> nonResidentialElements) {
        this.nonResidentialElements = nonResidentialElements;
    }

    public List<String> getSolutionTags() {
        return solutionTags;
    }

    public void setSolutionTags(List<String> solutionTags) {
        this.solutionTags = solutionTags;
    }
}


