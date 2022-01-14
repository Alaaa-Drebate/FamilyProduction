package com.example.familyproduction.model;

public class Order {

    private int id;//
    private double lat;//
    private double lon;//
    private int status;//
    private int userId;//
    private String userName;//
    private double totalPrice;
    private String orderDetails;


    public Order(int id, double lat, double lon, int status, int userId, String userName, double totalPrice, String orderDetails) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.orderDetails = orderDetails;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}
