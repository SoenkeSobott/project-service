package org.soenke.sobott.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Page;

import java.util.List;

@MongoEntity(collection = "articles")
public class Article extends PanacheMongoEntity {
    private String articleNumber;
    private String articleDescription;
    private Float weight;
    private Float listPrice;
    private Integer availability;

    public static Article findByArticleNumber(String articleNumber) {
        return find("articleNumber", articleNumber).firstResult();
    }

    public static List<Article> getFirstArticles(Integer amount) {
        return findAll().page(Page.ofSize(amount)).list();
    }

    public static List<Article> searchArticles(String searchTerm) {
        return find("articleDescription like ?1 or articleNumber like ?1", searchTerm).page(Page.ofSize(1000)).list();
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getListPrice() {
        return listPrice;
    }

    public void setListPrice(Float listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

}
