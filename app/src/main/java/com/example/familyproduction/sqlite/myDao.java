package com.example.familyproduction.sqlite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface myDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addItem(CartItemsDB item);

    @Query("select * from CartItemsDB")
    public List<CartItemsDB> getItems();

    @Query("select * from CartItemsDB where id = :id1")
    public CartItemsDB getItem(String id1);

    @Delete
    public void deleteItem(CartItemsDB item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateItem(CartItemsDB item);

    @Delete
    public void deleteAll(List<CartItemsDB> items);

    @Query("select owner_id from CartItemsDB")
    public List<Integer> getFamilyId();
}
