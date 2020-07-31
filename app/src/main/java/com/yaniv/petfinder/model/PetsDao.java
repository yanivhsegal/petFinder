package com.yaniv.petfinder.model;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PetsDao {
    @Query("select * from Pet")
    LiveData<List<Pet>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Pet... pets);

    @Delete
    void delete(Pet pet);

    @Query("select * from Pet where ownerId = :ownerId")
    LiveData<List<Pet>> getAllByOwnerId(String ownerId);
}
