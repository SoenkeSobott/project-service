package org.soenke.sobott.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleAvailableQuantity {
    @JsonProperty
    private String articleNumber;
    @JsonProperty
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


