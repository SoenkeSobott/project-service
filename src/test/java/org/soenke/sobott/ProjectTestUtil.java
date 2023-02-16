package org.soenke.sobott;

import org.apache.commons.lang3.RandomStringUtils;
import org.soenke.sobott.entity.Project;
import org.soenke.sobott.enums.SegmentLevelOne;
import org.soenke.sobott.enums.Structure;

import java.util.Arrays;
import java.util.List;

public class ProjectTestUtil {

    public static void createBaseProject(String projectNumber, String projectName) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.persist();
    }

    public static void createBaseProjectWithPricePerUnit(String projectNumber, String projectName, Double pricePerUnit) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProjectPricePerUnit(pricePerUnit);
        project.persist();
    }

    public static void createProjectWithProduct(String projectNumber, String projectName, String product) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.persist();
    }

    public static void createProjectWithProductAndPricePerUnit(String projectNumber, String projectName, String product, Double pricePerUnit) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.setProjectPricePerUnit(pricePerUnit);
        project.persist();
    }

    public static void createProjectWithSolutionTags(String projectName, String solutionTagOne, String solutionTagTwo,
                                                     String solutionTagThree, String solutionTagFour) {
        Project project = new Project();
        project.setProjectNumber(RandomStringUtils.random(8));
        project.setProjectName(projectName);
        project.setSolutionTags(Arrays.asList(solutionTagOne, solutionTagTwo, solutionTagThree, solutionTagFour));
        project.persist();
    }

    public static void createProjectWithStructureThicknessAndHeight(String projectNumber, String projectName, Structure structure, Double thickness, Double height) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure.getValue());
        project.setThickness(thickness);
        project.setHeight(height);
        project.persist();
    }

    public static void createProjectWithShoringThicknessAndHeight(String projectNumber, String projectName, Double slabThickness, Double shoringHeight) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(Structure.Shoring.getValue());
        project.setSlabThickness(slabThickness);
        project.setShoringHeight(shoringHeight);
        project.persist();
    }

    public static void createProjectWithStructureLengthWidthAndHeight(String projectNumber, String projectName, Structure structure, Double length, Double width, Double height) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure.getValue());
        project.setLength(length);
        project.setWidth(width);
        project.setHeight(height);
        project.persist();
    }

    public static void createProjectWithSegmentLevelOneAndTwo(String projectNumber, String projectName, SegmentLevelOne segmentLevelOne,
                                                              String segmentLevelTwo) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setSegmentLevelOne(segmentLevelOne.getValue());
        project.setSegmentLevelTwo(segmentLevelTwo);
        project.persist();
    }

    public static void createFullProject(String projectNumber,
                                         String projectName,
                                         Structure structure,
                                         Double thickness,
                                         Double length,
                                         Double width,
                                         Double height,
                                         Double slabThickness,
                                         Double shoringHeight,
                                         String product,
                                         SegmentLevelOne segmentLevelOne,
                                         String segmentLevelTwo,
                                         List<String> solutionTags) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setMainStructure(structure.getValue());
        project.setThickness(thickness);
        project.setLength(length);
        project.setWidth(width);
        project.setHeight(height);
        project.setSlabThickness(slabThickness);
        project.setShoringHeight(shoringHeight);
        project.setProduct(product);
        project.setSegmentLevelOne(segmentLevelOne.getValue());
        project.setSegmentLevelTwo(segmentLevelTwo);
        project.setSolutionTags(solutionTags);
        project.persist();
    }
}
