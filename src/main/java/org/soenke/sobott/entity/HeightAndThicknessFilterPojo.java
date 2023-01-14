package org.soenke.sobott.entity;

public class HeightAndThicknessFilterPojo {
    private Double minThickness;
    private Double maxThickness;
    private Double minHeight;
    private Double maxHeight;

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
