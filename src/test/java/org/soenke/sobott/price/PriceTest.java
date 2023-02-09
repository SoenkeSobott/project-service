package org.soenke.sobott.price;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.BillOfQuantityEntry;
import org.soenke.sobott.entity.Project;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PriceTest {

    @BeforeEach
    public void cleanDB() {
        Project.deleteAll();
        Article.deleteAll();
    }

    @Test
    public void testDUOProjectPriceEndpoint() {
        createArticle("20", 10.5f);
        createArticle("40", 5f);
        createArticle("60", 10f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 12));
        bq.add(createBQEntry("40", 5));
        bq.add(createBQEntry("60", 1));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(161f))
                .body("currency", is("HKD"))
                .body("unit", is("M2"));
    }

    @Test
    public void testShoringProjectPriceEndpoint() {
        createArticle("20", 10.5f);
        createArticle("40", 15.73f);
        createArticle("60", 10f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 10));
        bq.add(createBQEntry("40", 7));
        bq.add(createBQEntry("60", 3));

        createProjectWithBQ("911", "test", "PS100", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(245.11f))
                .body("currency", is("HKD"))
                .body("unit", is("M3"));
    }

    @Test
    public void testProjectPriceEndpointInvalidProjectNumber() {
        given()
                .when().get("/projects/918/price")
                .then()
                .statusCode(404)
                .body(containsString("Project with Project number: '918' not found."));
    }

    @Test
    public void testProjectPriceEndpointProjectWithoutBQ() {
        createProjectWithBQ("917", "test", "DUO", null);

        given()
                .when().get("/projects/917/price")
                .then()
                .statusCode(404)
                .body(containsString("Project with Project number: '917' has no BQ."));
    }

    @Test
    public void testProjectPriceEndpointWithMissingArticle() {
        createArticle("20", 10.5f);
        createArticle("60", 10f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 12));
        bq.add(createBQEntry("40", 5));
        bq.add(createBQEntry("60", 1));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("Couldn't find article from BQ with Article number: 40"));
    }

    @Test
    public void testProjectPriceEndpointWithMissingArticlePrice() {
        createArticle("20", 10.5f);
        createArticle("60", null);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 12));
        bq.add(createBQEntry("60", 1));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("Article price is not present or zero for Article number: 60"));
    }

    @Test
    public void testProjectPriceEndpointWithArticlePriceZero() {
        createArticle("20", 10.5f);
        createArticle("60", 0f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 12));
        bq.add(createBQEntry("60", 1));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("Article price is not present or zero for Article number: 60"));
    }

    @Test
    public void testProjectPriceEndpointWithMissingQuantityInBQEntry() {
        createArticle("20", 10.5f);
        createArticle("60", 20f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", null));
        bq.add(createBQEntry("60", 2));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("BQ entry quantity is not present or zero for BQ entry Article number: 20"));
    }

    @Test
    public void testProjectPriceEndpointWithZeroQuantityInBQEntry() {
        createArticle("20", 10.5f);
        createArticle("60", 20f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 23));
        bq.add(createBQEntry("60", 0));

        createProjectWithBQ("911", "test", "DUO", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("BQ entry quantity is not present or zero for BQ entry Article number: 60"));
    }

    @Test
    public void testProjectPriceEndpointWithMissingProduct() {
        createArticle("20", 10.5f);
        createArticle("60", 20f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 20));
        bq.add(createBQEntry("60", 2));

        createProjectWithBQ("911", "test", null, bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(404)
                .body(containsString("Couldn't find Product unit(m2/m3) for Project number: 911"));
    }

    @Test
    public void testProjectPriceEndpointUsesDefaultUnitIfInvalidProductPassed() {
        createArticle("20", 10.5f);
        createArticle("60", 20f);

        List<BillOfQuantityEntry> bq = new ArrayList<>();
        bq.add(createBQEntry("20", 20));
        bq.add(createBQEntry("60", 2));

        createProjectWithBQ("911", "test", "InvalidProductType", bq);

        given()
                .when().get("/projects/911/price")
                .then()
                .statusCode(200)
                .body("price", is(250f))
                .body("currency", is("HKD"))
                .body("unit", is("M2"));
    }

    protected void createProjectWithBQ(String projectNumber, String projectName,
                                       String product, List<BillOfQuantityEntry> bq) {
        Project project = new Project();
        project.setProjectNumber(projectNumber);
        project.setProjectName(projectName);
        project.setProduct(product);
        project.setBillOfQuantity(bq);
        project.persist();
    }

    protected BillOfQuantityEntry createBQEntry(String articleNumber, Integer quantity) {
        BillOfQuantityEntry entry = new BillOfQuantityEntry();
        entry.setArticleNumber(articleNumber);
        entry.setQuantity(quantity);
        return entry;
    }

    protected void createArticle(String articleNumber, Float listPrice) {
        Article article = new Article();
        article.setArticleNumber(articleNumber);
        article.setListPrice(listPrice);
        article.persist();
    }

}