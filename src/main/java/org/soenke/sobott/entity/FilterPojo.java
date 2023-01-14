package org.soenke.sobott.entity;

public class FilterPojo {

    private String searchTerm;
    private String product;
    private WallFilterPojo wallFilter;


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

    public WallFilterPojo getWallFilter() {
        return wallFilter;
    }

    public void setWallFilter(WallFilterPojo wallFilter) {
        this.wallFilter = wallFilter;
    }
}


