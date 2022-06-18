package com.example.capstone_project_redo.model;

public class DtiListModel {

    String category, product, brand, unit, srp, id;

    DtiListModel() {

    }

    public DtiListModel(String category, String product, String brand, String unit, String srp, String id) {
        this.category = category;
        this.product = product;
        this.brand = brand;
        this.unit = unit;
        this.srp = srp;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSrp() {
        return srp;
    }

    public void setSrp(String srp) {
        this.srp = srp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
