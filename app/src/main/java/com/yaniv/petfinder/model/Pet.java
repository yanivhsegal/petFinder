package com.yaniv.petfinder.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Pet implements Serializable {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String description;
    public String imgUrl;
    public String ownerId;
    long lastUpdated;

    public Pet() {
    }

    public Pet(String id, String name, String imgUrl, String ownerId) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.ownerId = ownerId;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
