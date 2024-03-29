package org.soenke.sobott;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Article;

import javax.ws.rs.core.MediaType;
import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class WarehouseResourceTest {

    @BeforeEach
    public void cleanDB() {
        Article.deleteAll();
    }

    @Test
    public void testGetAllArticlesEndpoint() {
        createArticle("12345", "Test description", 200.34f, 89000f, 2333);
        createArticle("123BB", "MegaProject0815", 24f, 1022f, 0);
        createArticle("56788", "House Project 00X", 20.34f, 1080f, 273);

        given()
                .when().get("/warehouse/articles")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].articleNumber", is("12345"))
                .body("[0].articleDescription", is("Test description"))
                .body("[0].weight", is(200.34f))
                .body("[0].listPrice", is(89000f))
                .body("[0].availability", is(2333))
                .body("[1].articleNumber", is("56788"))
                .body("[1].articleDescription", is("House Project 00X"))
                .body("[1].weight", is(20.34f))
                .body("[1].listPrice", is(1080f))
                .body("[1].availability", is(273))
                .body("[2].articleNumber", is("123BB"))
                .body("[2].articleDescription", is("MegaProject0815"))
                .body("[2].weight", is(24f))
                .body("[2].listPrice", is(1022f))
                .body("[2].availability", is(0));
    }

    @Test
    public void testSearchArticlesEndpointByArticleNumber() {
        createArticle("12345", "Test description", 200.34f, 89000f, 0);
        createArticle("56788", "House Project 00X", 20.34f, 1080f, 23);
        createArticle("12344", "MegaProject0815", 24f, 1022f, 202);
        createArticle("34534", "My Mega Building 100x.11", 24f, 1022f, 2012);
        createArticle("00900", "MegaProject0815", 24f, 1022f, 2012);
        createArticle("10001", "MegaProject0815", 24f, 1022f, 2012);

        given()
                .when().get("/warehouse/articles?searchTerm=34")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].articleNumber", is("34534"))
                .body("[1].articleNumber", is("12344"))
                .body("[2].articleNumber", is("12345"));
    }

    @Test
    public void testSearchArticlesEndpointByDescription() {
        createArticle("12345", "Test description", 200.34f, 89000f, 0);
        createArticle("56788", "House Project 00X", 20.34f, 1080f, 23);
        createArticle("12344", "MegaProject0815", 24f, 1022f, 122);
        createArticle("34534", "My Mega Building 100x.11", 24f, 1022f, 2012);
        createArticle("00900", "MegaProject0815", 24f, 1022f, 323);
        createArticle("10001", "Jec De wid", 24f, 1022f, 11);

        given()
                .when().get("/warehouse/articles?searchTerm=jec")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("[0].articleNumber", is("00900"))
                .body("[1].articleNumber", is("12344"))
                .body("[2].articleNumber", is("56788"))
                .body("[3].articleNumber", is("10001"));
    }

//    @Test
//    public void testUpdateArticleQuantityEndpoint() {
//        createArticle("12345", "Test description", 200.34f, 100f, 0);
//
//        // Check present
//        Article article = Article.find("articleNumber", "12345").firstResult();
//        Assertions.assertEquals("12345", article.getArticleNumber());
//        Assertions.assertEquals(0, article.getAvailability());
//
//        // Update quantity
//        given()
//                .when().post("/warehouse/articles?articleNumber=12345&newAvailability=230")
//                .then()
//                .statusCode(200)
//                .body(containsString("Updated Article availability"));
//
//        // Check quantity updated
//        article = Article.find("articleNumber", "12345").firstResult();
//        Assertions.assertEquals("12345", article.getArticleNumber());
//        Assertions.assertEquals(230, article.getAvailability());
//    }
//
//    @Test
//    public void testUpdateArticleQuantityEndpointWithoutNewAvailabilityQueryParam() {
//        createArticle("12345", "Test description", 200.34f, 100f, 0);
//
//        // Check present
//        Article article = Article.find("articleNumber", "12345").firstResult();
//        Assertions.assertEquals("12345", article.getArticleNumber());
//        Assertions.assertEquals(0, article.getAvailability());
//
//        // Update quantity
//        given()
//                .when().post("/warehouse/articles?articleNumber=12345")
//                .then()
//                .statusCode(400)
//                .body(containsString("Availability empty"));
//    }
//
//    @Test
//    public void testUpdateArticleQuantityEndpointWithInvalidArticleNumber() {
//        given()
//                .when().post("/warehouse/articles?articleNumber=12345&newAvailability=230")
//                .then()
//                .statusCode(404)
//                .body(containsString("Article not found"));
//    }

    @Test
    public void testGetArticlesAvailability() {
        createArticle("12345", "Test description", 200.34f, 2310f, 0);
        createArticle("34234", "Test description", 200.34f, 13f, 23);
        createArticle("64445", "Test description", 200.34f, 1220f, 900);
        createArticle("23423", "Test description", 200.34f, 890f, 12);

        // Update quantity
        given()
                .when().get("/warehouse/articles/availability?articleNumbers=12345,64445,34234")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].articleNumber", is("12345"))
                .body("[0].availability", is(0))
                .body("[1].articleNumber", is("64445"))
                .body("[1].availability", is(900))
                .body("[2].articleNumber", is("34234"))
                .body("[2].availability", is(23));
    }

    @Test
    public void testGetArticlesAvailabilityWithInvalidArticleNumbers() {
        createArticle("12345", "Test description", 200.34f, 1020f, 22);
        createArticle("64445", "Test description", 200.34f, 1020f, 900);

        // Update quantity
        given()
                .when().get("/warehouse/articles/availability?articleNumbers=99393,12345,23423")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].articleNumber", is("12345"))
                .body("[0].availability", is(22));
    }

    @Test
    public void testGetArticlesAvailabilityWithEmptyQuery() {
        createArticle("12345", "Test description", 200.34f, 1000f, 0);
        createArticle("34234", "Test description", 200.34f, 80f, 23);
        createArticle("64445", "Test description", 200.34f, 120f, 900);
        createArticle("23423", "Test description", 200.34f, 200f, 12);

        // Update quantity
        given()
                .when().get("/warehouse/articles/availability")
                .then()
                .statusCode(400)
                .body(containsString("No article numbers passed"));
    }

    @Test
    public void testUploadArticlesWeeklyAvailabilityFile() {
        createArticle("018040", "My Test Description", 10f, 210.06f, 300);
        createArticle("073659", "Porsche 918", 1634f, 845000f, 918);

        File file = new File("src/test/TestWeeklyAvailability.xlsx");
        given()
                .when()
                .multiPart(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .post("/warehouse/articles")
                .then()
                .statusCode(200)
                .body(containsString("Transferred all Article data"));

        // Check articles in Database
        Assertions.assertEquals(8, Article.listAll().size());

        checkArticleIsCorrect("013010", null, 81, 0, 0f, 0f);

        checkArticleIsCorrect("018040", "My Test Description", 12, 0, 210.06f, 10f);

        checkArticleIsCorrect("200344", null, 2340, 2, 0f, 0f);
        checkArticleIsCorrect("018060", null, 333, 2, 0f, 0f);

        checkArticleIsCorrect("074970", null, -607, 4, 0f, 0f);
        checkArticleIsCorrect("073800", null, 101, 4, 0f, 0f);
        checkArticleIsCorrect("073659", "Porsche 918", 47, 4, 845000f, 1634f);
        checkArticleIsCorrect("201047", null, 0, 4, 0f, 0f);
    }

    @Test
    public void testUploadArticlesWeeklyAvailabilityNoFilePassed() {
        given()
                .when()
                .multiPart("Invalid name", "Invalid stuff")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .post("/warehouse/articles")
                .then()
                .statusCode(400)
                .body(containsString("No file passed in upload"));
    }

    @Test
    public void testUploadArticlesWeeklyAvailabilityFileWithInvalidTypePassed() {
        File invalidFile = new File("src/test/InvalidFile.txt");
        given()
                .when()
                .multiPart(invalidFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .post("/warehouse/articles")
                .then()
                .statusCode(400)
                .body(containsString("Uploaded file can't be read into Excel workbook: Can't open workbook - unsupported file type: UNKNOWN"));
    }

    @Test
    public void testUploadArticlesWeeklyAvailabilityInvalidExcelFilePassed() {
        File invalidFile = new File("src/test/InvalidExcelFile.xlsx");
        given()
                .when()
                .multiPart(invalidFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .post("/warehouse/articles")
                .then()
                .statusCode(400)
                .body(containsString("The Excel format is corrupted"));
    }

    protected void createArticle(String articleNumber, String articleDescription, Float weight, Float listPrice, Integer availability) {
        Article article = new Article();
        article.setArticleNumber(articleNumber);
        article.setArticleDescription(articleDescription);
        article.setWeight(weight);
        article.setListPrice(listPrice);
        article.setAvailability(availability);
        article.persist();
    }

    protected void checkArticleIsCorrect(String articleNumber, String description, int availability, int amountOfSubstituteArticles, float listPrice, float weight) {
        Article article = Article.findByArticleNumber(articleNumber);
        Assertions.assertEquals(articleNumber, article.getArticleNumber());
        Assertions.assertEquals(description, article.getArticleDescription());
        Assertions.assertEquals(availability, article.getAvailability());
        Assertions.assertEquals(listPrice, article.getListPrice());
        Assertions.assertEquals(weight, article.getWeight());
        if (amountOfSubstituteArticles > 0) {
            Assertions.assertEquals(amountOfSubstituteArticles, article.getSubstituteArticles().size());
        } else {
            Assertions.assertNull(article.getSubstituteArticles());
        }
    }
}