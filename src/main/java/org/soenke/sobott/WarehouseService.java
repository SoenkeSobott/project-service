package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.ArticleAvailableQuantity;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class WarehouseService implements PanacheMongoRepository<Article> {

    Logger LOGGER = Logger.getLogger(WarehouseService.class.getName());

    public Response getAllArticles() {
        return Response.status(Response.Status.OK).entity(Article.getFirst100Articles()).build();
    }

    public Response updateArticleAvailability(String articleNumber, Integer newAvailability) {
        if (newAvailability == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Availability empty").build();
        }
        Article articleInDB = Article.findByArticleNumber(articleNumber);
        if (articleInDB != null) {
            articleInDB.setAvailability(newAvailability);
            articleInDB.persistOrUpdate();
            return Response.status(Response.Status.OK).entity("Updated Article availability").build();
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
            if (article != null) {
                articleAvailabilityQuantities.add(new ArticleAvailableQuantity(articleNumber, article.getListPrice(),
                        article.getAvailability()));
            }
        });

        return Response.status(Response.Status.OK).entity(articleAvailabilityQuantities).build();
    }
}
