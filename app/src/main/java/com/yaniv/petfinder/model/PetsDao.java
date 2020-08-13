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

    @Query("Delete from Pet where id = :petId")
    void deleteById(String petId);

    @Query("Delete from Pet where id = id")
    void deleteAll();

    @Query("select * from Pet where ownerId = :ownerId")
    LiveData<List<Pet>> getAllByOwnerId(String ownerId);

    @Query("select * from Pet where petType = :type")
    List<Pet> getAllPetsByType(PetTypes type);
}
