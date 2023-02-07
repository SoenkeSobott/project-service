package org.soenke.sobott;

import com.google.gson.Gson;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.ArticleAvailableQuantity;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class WarehouseService implements PanacheMongoRepository<Article> {

    Logger LOGGER = Logger.getLogger(WarehouseService.class.getName());

    public Response getAllArticles() {
        return Response.status(Response.Status.OK).entity(Article.listAll()).build();
    }

    public Response updateArticleQuantity(String articleNumber, Integer newQuantity) {
        Article articleInDB = Article.findByArticleNumber(articleNumber);
        if (articleInDB != null) {
            articleInDB.setQuantity(newQuantity);
            articleInDB.persistOrUpdate();
            return Response.status(Response.Status.OK).entity("Updated Article quantity").build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
    }

    public Response getArticlesAvailability(String articleNumbers) {
        if (articleNumbers == null || articleNumbers.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No article numbers passed").build();
        }
        List<String> articleNumbersList = List.of(articleNumbers.split(","));
        List<ArticleAvailableQuantity> articleAvailabilityQuantities = new ArrayList<>();
        articleNumbersList.forEach(articleNumber -> {
            Article article = Article.findByArticleNumber(articleNumber);
            LOGGER.log(Level.SEVERE, "Article number origin: " + articleNumber);
            if (article != null) {
                LOGGER.log(Level.SEVERE, "Article number: " + article.getArticleNumber());
                LOGGER.log(Level.SEVERE, "Article quantity: " + article.getQuantity());
                ArticleAvailableQuantity articleAvailableQuantity = new ArticleAvailableQuantity();
                articleAvailableQuantity.setArticleNumber(articleNumber);
                articleAvailableQuantity.setAvailableQuantity(article.getQuantity());
                articleAvailabilityQuantities.add(articleAvailableQuantity);
            }
        });

        String json = new Gson().toJson(articleAvailabilityQuantities);
        return Response.status(Response.Status.OK).entity(json).build();
    }
}
