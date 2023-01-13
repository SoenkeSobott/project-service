package org.soenke.sobott.entity;

public class FilterPojo {

    private String searchTerm;
    private String product;
    private Double minThickness;
    private Double maxThickness;
    private Double minHeight;
    private Double maxHeight;

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

    public Double getMinThickness() {
        return minThickness;
    }

    public void setMinThickness(Double minThickness) {
        this.minThickness = minThickness;
    }

    public Double getMaxThickness() {
        return maxThickness;
    }

    public void setMaxThickness(Double maxThickness) {
        this.maxThickness = maxThickness;
    }

    public Double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Double minHeight) {
        this.minHeight = minHeight;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

}
