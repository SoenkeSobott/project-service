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

}
