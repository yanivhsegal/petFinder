package com.yaniv.petfinder.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.yaniv.petfinder.MyApplication;

@Database(entities = {Pet.class}, version = 10)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PetsDao petDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}


