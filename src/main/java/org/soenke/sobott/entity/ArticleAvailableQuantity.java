package org.soenke.sobott.entity;

public class ArticleAvailableQuantity {
    private String articleNumber;
    private Integer availableQuantity;

    public ArticleAvailableQuantity(String articleNumber, Integer availableQuantity) {
        this.articleNumber = articleNumber;
        this.availableQuantity = availableQuantity;
    }

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


