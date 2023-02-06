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
        createArticle("12345", "Test description", 0);
        createArticle("56788", "House Project 00X", 23);
        createArticle("123BB", "MegaProject0815", 2012);

        given()
                .when().get("/warehouse/articles")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].articleNumber", is("12345"))
                .body("[0].articleDescription", is("Test description"))
                .body("[0].quantity", is(0))
                .body("[1].articleNumber", is("56788"))
                .body("[1].articleDescription", is("House Project 00X"))
                .body("[1].quantity", is(23))
                .body("[2].articleNumber", is("123BB"))
                .body("[2].articleDescription", is("MegaProject0815"))
                .body("[2].quantity", is(2012));
    }

    @Test
    public void testUpdateArticleQuantityEndpoint() {
        createArticle("12345", "Test description", 0);

        // Check present
        Article article = Article.find("articleNumber", "12345").firstResult();
        Assertions.assertEquals("12345", article.getArticleNumber());
        Assertions.assertEquals(0, article.getQuantity());

        // Update quantity
        given()
                .when().post("/warehouse/articles?articleNumber=12345&newQuantity=230")
                .then()
                .statusCode(200)
                .body(containsString("Updated Article quantity"));

        // Check quantity updated
        article = Article.find("articleNumber", "12345").firstResult();
        Assertions.assertEquals("12345", article.getArticleNumber());
        Assertions.assertEquals(230, article.getQuantity());
    }

    @Test
    public void testUpdateArticleQuantityEndpointWithInvalidArticleNumber() {
        given()
                .when().post("/warehouse/articles?articleNumber=12345&newQuantity=230")
                .then()
                .statusCode(404)
                .body(containsString("Article not found"));
    }

    protected void createArticle(String articleNumber, String articleDescription, Integer quantity) {
        Article article = new Article();
        article.setArticleNumber(articleNumber);
        article.setArticleDescription(articleDescription);
        article.setQuantity(quantity);
        article.persist();
    }
}