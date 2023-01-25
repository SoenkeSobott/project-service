package org.soenke.sobott.filter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductFilter {

    public String generateProductFilterQuery(String product) {
        if (product != null && !product.isEmpty()) {
            return "{product: \"" + product.toUpperCase() + "\"},";
        }
        return "";
    }
}
