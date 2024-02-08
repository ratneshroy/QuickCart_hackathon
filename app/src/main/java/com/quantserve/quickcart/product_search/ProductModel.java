package com.quantserve.quickcart.product_search;

public class ProductModel {
    Integer id;
    String name;
    String description;
    String price;
    Integer Qty;
    String image;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        Qty = qty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductModel(int id, String name, String description, String price, int qty, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        Qty = qty;
        this.image = image;
    }
}
