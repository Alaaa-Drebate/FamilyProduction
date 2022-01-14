package com.example.familyproduction.model;

public class Food {

    private int id;
    private String name;
    private String description;
    private double price;
    private int ownerId;
    private String ownerName;
    private String category;
    private String image;


    public Food(int id, String name, String description, double price, int ownerId, String ownerName, String category, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.category = category;
        this.image = image;
    }

    public Food(int id, String name, String category, String description, double price, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
