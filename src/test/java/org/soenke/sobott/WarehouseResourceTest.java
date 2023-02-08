package org.soenke.sobott;

import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.soenke.sobott.entity.Article;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class WarehouseResourceTest {

    @Inject
    MongoClient mongoClient;

    @BeforeEach
    public void cleanDB() {
        Article.deleteAll();
    }

    @Test
    public void testGetAllArticlesEndpoint() {
        createArticle("12345", "Test description", 89000f, 0);
        createArticle("56788", "House Project 00X", 1080f, 23);
        createArticle("123BB", "MegaProject0815", 1022f, 2012);

        given()
                .when().get("/warehouse/articles")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].articleNumber", is("12345"))
                .body("[0].articleDescription", is("Test description"))
                .body("[0].listPrice", is(89000f))
                .body("[0].availability", is(0))
                .body("[1].articleNumber", is("56788"))
                .body("[1].articleDescription", is("House Project 00X"))
                .body("[1].listPrice", is(1080f))
                .body("[1].availability", is(23))
                .body("[2].articleNumber", is("123BB"))
                .body("[2].articleDescription", is("MegaProject0815"))
                .body("[2].listPrice", is(1022f))
                .body("[2].availability", is(2012));
    }

    @Test
    public void testUpdateArticleQuantityEndpoint() {
        createArticle("12345", "Test description", 100f, 0);

        // Check present
        Article article = Article.find("articleNumber", "12345").firstResult();
        Assertions.assertEquals("12345", article.getArticleNumber());
        Assertions.assertEquals(0, article.getAvailability());

        // Update quantity
        given()
                .when().post("/warehouse/articles?articleNumber=12345&newQuantity=230")
                .then()
                .statusCode(200)
                .body(containsString("Updated Article availability"));

        // Check quantity updated
        article = Article.find("articleNumber", "12345").firstResult();
        Assertions.assertEquals("12345", article.getArticleNumber());
        Assertions.assertEquals(230, article.getAvailability());
    }

    @Test
    public void testGetArticlesAvailability() {
        createArticle("12345", "Test description", 2310f, 0);
        createArticle("34234", "Test description", 13f, 23);
        createArticle("64445", "Test description", 1220f, 900);
        createArticle("23423", "Test description", 890f, 12);

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
        createArticle("12345", "Test description", 1020f, 22);
        createArticle("64445", "Test description", 1020f, 900);

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
        createArticle("12345", "Test description", 1000f, 0);
        createArticle("34234", "Test description", 80f, 23);
        createArticle("64445", "Test description", 120f, 900);
        createArticle("23423", "Test description", 200f, 12);

        // Update quantity
        given()
                .when().get("/warehouse/articles/availability")
                .then()
                .statusCode(400)
                .body(containsString("No article numbers passed"));
    }

    @Test
    public void testUpdateArticleQuantityEndpointWithInvalidArticleNumber() {
        given()
                .when().post("/warehouse/articles?articleNumber=12345&newQuantity=230")
                .then()
                .statusCode(404)
                .body(containsString("Article not found"));
    }

    protected void createArticle(String articleNumber, String articleDescription, Float listPrice, Integer availability) {
        Article article = new Article();
        article.setArticleNumber(articleNumber);
        article.setArticleDescription(articleDescription);
        article.setListPrice(listPrice);
        article.setAvailability(availability);
        article.persist();
    }
}