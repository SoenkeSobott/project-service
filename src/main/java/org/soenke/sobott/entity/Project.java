package org.soenke.sobott.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.List;

@MongoEntity(collection = "projects")
public class Project extends PanacheMongoEntity {

    // Unique ID
    private String projectNumber;
    private String projectName;
    private String drawingNumber;
    private String drawingLink;
    private List<BillOfQuantityEntry> billOfQuantity;
    private String originBQLink;
    private List<String> pictures;
    private Double height;
    private Double thickness;
    private Double length;
    private Double width;
    private Double areaWidth;
    private Double maxPourHeight;
    private Double m2OfFormwork;
    private Double m2OfConcrete;
    private String planViewSize; // One object with two values?
    private String location; // Location object?
    private String coordinates;
    private String product;
    private String mainStructure;
    private String SegmentLevelOne;
    private String SegmentLevelTwo;
    private String SegmentLevelThree;
    private List<String> solutionTags;
    private Double projectPrice;

    // PS100
    private Double shoringHeight;
    private Double slabThickness;
    private Double m3OfShoring;

    public static Project findByProjectNumber(String projectNumber) {
        return find("projectNumber", projectNumber).firstResult();
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public String getDrawingLink() {
        return drawingLink;
    }

    public void setDrawingLink(String drawingLink) {
        this.drawingLink = drawingLink;
    }

    public List<BillOfQuantityEntry> getBillOfQuantity() {
        return billOfQuantity;
    }

    public void setBillOfQuantity(List<BillOfQuantityEntry> billOfQuantity) {
        this.billOfQuantity = billOfQuantity;
    }

    public String getOriginBQLink() {
        return originBQLink;
    }

    public void setOriginBQLink(String originBQLink) {
        this.originBQLink = originBQLink;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getAreaWidth() {
        return areaWidth;
    }

    public void setAreaWidth(Double areaWidth) {
        this.areaWidth = areaWidth;
    }

    public Double getMaxPourHeight() {
        return maxPourHeight;
    }

    public void setMaxPourHeight(Double maxPourHeight) {
        this.maxPourHeight = maxPourHeight;
    }

    public Double getM2OfFormwork() {
        return m2OfFormwork;
    }

    public void setM2OfFormwork(Double m2OfFormwork) {
        this.m2OfFormwork = m2OfFormwork;
    }

    public Double getM2OfConcrete() {
        return m2OfConcrete;
    }

    public void setM2OfConcrete(Double m2OfConcrete) {
        this.m2OfConcrete = m2OfConcrete;
    }

    public String getPlanViewSize() {
        return planViewSize;
    }

    public void setPlanViewSize(String planViewSize) {
        this.planViewSize = planViewSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMainStructure() {
        return mainStructure;
    }

    public void setMainStructure(String mainStructure) {
        this.mainStructure = mainStructure;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getSegmentLevelOne() {
        return SegmentLevelOne;
    }

    public void setSegmentLevelOne(String segmentLevelOne) {
        SegmentLevelOne = segmentLevelOne;
    }

    public String getSegmentLevelTwo() {
        return SegmentLevelTwo;
    }

    public void setSegmentLevelTwo(String segmentLevelTwo) {
        SegmentLevelTwo = segmentLevelTwo;
    }

    public String getSegmentLevelThree() {
        return SegmentLevelThree;
    }

    public void setSegmentLevelThree(String segmentLevelThree) {
        SegmentLevelThree = segmentLevelThree;
    }

    public List<String> getSolutionTags() {
        return solutionTags;
    }

    public void setSolutionTags(List<String> solutionTags) {
        this.solutionTags = solutionTags;
    }

    // PS100
    public Double getShoringHeight() {
        return shoringHeight;
    }

    public void setShoringHeight(Double shoringHeight) {
        this.shoringHeight = shoringHeight;
    }

    public Double getSlabThickness() {
        return slabThickness;
    }

    public void setSlabThickness(Double slabThickness) {
        this.slabThickness = slabThickness;
    }

    public Double getM3OfShoring() {
        return m3OfShoring;
    }

    public void setM3OfShoring(Double m3OfShoring) {
        this.m3OfShoring = m3OfShoring;
    }

    public Double getProjectPrice() {
        return projectPrice;
    }

    public void setProjectPrice(Double projectPrice) {
        this.projectPrice = projectPrice;
    }

}