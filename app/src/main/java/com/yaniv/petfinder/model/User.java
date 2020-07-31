package com.yaniv.petfinder.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;


@Entity
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    long lastUpdated;

    public User() {
    }

    public User(String id, String name, List<Pet> petsForAdoption) {
        this.id = id;
        this.name = name;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
