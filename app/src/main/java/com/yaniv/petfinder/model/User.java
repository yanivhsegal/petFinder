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
    public List<Pet> petsToAdopt;
    long lastUpdated;

    public User() {
    }

    public User(String id, String name, List<Pet> petsToAdopt) {
        this.id = id;
        this.name = name;
        this.petsToAdopt = petsToAdopt;
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

    public List<Pet> getPetsToAdopt() {
        return petsToAdopt;
    }

    public void setPetsToAdopt(List<Pet> petsToAdopt) {
        this.petsToAdopt = petsToAdopt;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
