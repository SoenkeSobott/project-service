package org.soenke.sobott.entity;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ArticleAvailableQuantity {
    private String articleNumber;
    private Integer availableQuantity;

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}


