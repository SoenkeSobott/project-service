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
    private List<String> pictures;
    private Double height;
    private Double thickness;
    private String planViewSize; // One object with two values?
    private String location; // Location object?
    private String product;
    private String structure;
    private String solutionOne;
    private String solutionTwo;
    private String solutionThree;
    private String solutionFour;


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

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getSolutionOne() {
        return solutionOne;
    }

    public void setSolutionOne(String solutionOne) {
        this.solutionOne = solutionOne;
    }

    public String getSolutionTwo() {
        return solutionTwo;
    }

    public void setSolutionTwo(String solutionTwo) {
        this.solutionTwo = solutionTwo;
    }

    public String getSolutionThree() {
        return solutionThree;
    }

    public void setSolutionThree(String solutionThree) {
        this.solutionThree = solutionThree;
    }

    public String getSolutionFour() {
        return solutionFour;
    }

    public void setSolutionFour(String solutionFour) {
        this.solutionFour = solutionFour;
    }

}