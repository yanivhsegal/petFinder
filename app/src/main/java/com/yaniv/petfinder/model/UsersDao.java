package com.yaniv.petfinder.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface UsersDao {
    @Query("select * from User")
    LiveData<List<User>> getAll();

    @Query("select * from User where id == :id")
    LiveData<User> getUser(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
