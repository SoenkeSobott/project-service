package org.soenke.sobott.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ArticleAvailableQuantity {
    @JsonProperty
    private String articleNumber;
    @JsonProperty
    private Float listPrice;
    @JsonProperty
    private Integer availability;

    public ArticleAvailableQuantity(String articleNumber, Float listPrice, Integer availability) {
        this.articleNumber = articleNumber;
        this.listPrice = listPrice;
        this.availability = availability;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
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


