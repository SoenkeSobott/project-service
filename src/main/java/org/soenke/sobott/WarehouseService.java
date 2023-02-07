package org.soenke.sobott;

import com.google.gson.Gson;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Article;
import org.soenke.sobott.entity.ArticleAvailableQuantity;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class WarehouseService implements PanacheMongoRepository<Article> {

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
        List<ArticleAvailableQuantity> articlesAvailableQuantity = new ArrayList<>();
        articleNumbersList.forEach(articleNumber -> {
            Article article = Article.findByArticleNumber(articleNumber);
            if (article != null) {
                articlesAvailableQuantity.add(new ArticleAvailableQuantity(articleNumber, article.getQuantity()));
            }
        });

        String json = new Gson().toJson(articlesAvailableQuantity);
        return Response.status(Response.Status.OK).entity(json).build();
    }
}
