package com.example.familyproduction.sqlite;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CartItemsDB {

    @PrimaryKey
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "price")
    double price;

    @ColumnInfo(name = "quantity")
    int quantity;

    @ColumnInfo(name = "owner_id")
    int ownerId;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
