package com.example.recyclepro.models;

public class AssessmentCompleted {
    private String id;
    private String customerName;
    private String productName;
    private String time;
    private double finalPrice;
    private double avgRating;

    public AssessmentCompleted(String id, String customerName, String productName, String time, double finalPrice, double avgRating) {
        this.id = id;
        this.customerName = customerName;
        this.productName = productName;
        this.time = time;
        this.finalPrice = finalPrice;
        this.avgRating = avgRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}