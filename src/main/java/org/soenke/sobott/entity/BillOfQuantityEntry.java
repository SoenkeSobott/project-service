package org.soenke.sobott.entity;

public class BillOfQuantityEntry {

    private String articleNumber;
    private String description;
    private String unit;
    private Integer quantity;
    private Double weightPerUnit;

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getWeightPerUnit() {
        return weightPerUnit;
    }

    public void setWeightPerUnit(Double weightPerUnit) {
        this.weightPerUnit = weightPerUnit;
    }
}