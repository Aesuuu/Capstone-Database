package com.example.capstone_project_redo.model;

public class MyListModel {
    String name, category, price, priceExtension, description, imageUrl;

    MyListModel() {

    }

    public MyListModel(String name, String category, String price, String priceExtension, String description, String imageUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.priceExtension = priceExtension;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceExtension() {
        return priceExtension;
    }

    public void setPriceExtension(String priceExtension) {
        this.priceExtension = priceExtension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}