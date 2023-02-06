package org.soenke.sobott;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.soenke.sobott.entity.Article;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class WarehouseService implements PanacheMongoRepository<Article> {

    public Response getAllArticles() {
        return Response.status(Response.Status.OK).entity(Article.listAll()).build();
    }

    public Response updateArticleQuantity(String articleNumber, Integer newQuantity) {
        Article articleInDB = Article.find("articleNumber", articleNumber).firstResult();
        if (articleInDB != null) {
            articleInDB.setQuantity(newQuantity);
            articleInDB.persistOrUpdate();
            return Response.status(Response.Status.OK).entity("Updated Article quantity").build();
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
    }
}
