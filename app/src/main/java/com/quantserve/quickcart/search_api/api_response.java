package com.quantserve.quickcart.search_api;


import com.quantserve.quickcart.product_search.ProductModel;

import java.util.List;

public class api_response {
    public List<ProductModel> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductModel> productList) {
        this.productList = productList;
    }

    List<ProductModel> productList;


}
