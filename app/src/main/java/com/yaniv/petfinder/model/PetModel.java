package com.yaniv.petfinder.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.yaniv.petfinder.MyApplication;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PetModel {
    public static final PetModel instance = new PetModel();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompListener {
        void onComplete();
    }

    private PetModel() {
    }

    @SuppressLint("StaticFieldLeak")
    public void addPet(Pet pet, Listener<Boolean> listener) {
        PetFirebase.addPet(pet, listener);
        new AsyncTask<Pet, String, String>() {
            @Override
            protected String doInBackground(Pet... pets) {
                AppLocalDb.db.petDao().insertAll(pets[0]);
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute(pet);
    }

    public void refreshPetList(final CompListener listener) {
        long lastUpdated = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).getLong("PetsLastUpdateDate", 0);
        PetFirebase.getAllPetsSince(lastUpdated, new Listener<List<Pet>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Pet> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        long lastUpdated = 0;
                        for (Pet p : data) {
                            AppLocalDb.db.petDao().insertAll(p);
                            UserFirebase.getUser(p.ownerId, new UserModel.Listener<User>() {
                                @Override
                                public void onComplete(User data) {
                                    new AsyncTask<User, String, String>() {
                                        @Override
                                        protected String doInBackground(User... usr) {
                                            if (usr[0] != null) {
                                                AppLocalDb.db.usersDao().insertAll(usr[0]);
                                            }
                                            return "";
                                        }
                                    }.execute(data);
                                }
                            });
                            if (p.lastUpdated > lastUpdated) lastUpdated = p.lastUpdated;
                        }
                        SharedPreferences.Editor edit = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).edit();
                        edit.putLong("PetsLastUpdateDate", lastUpdated);
                        edit.commit();
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (listener != null) listener.onComplete();
                    }
                }.execute("");
            }
        });
    }

    public LiveData<List<Pet>> getAllPets() {
        LiveData<List<Pet>> liveData = AppLocalDb.db.petDao().getAll();
        refreshPetList(null);
        return liveData;
    }

    public LiveData<List<Pet>> getAllMyPets(String id) {
        LiveData<List<Pet>> liveData = AppLocalDb.db.petDao().getAllByOwnerId(id);
        refreshPetList(null);
        return liveData;
    }

    public List<Pet> getAllPetsByType(PetTypes type) {
        List<Pet> data = AppLocalDb.db.petDao().getAllPetsByType(type);
        return data;
    }


    public Pet getPet(String id) {
        return null;
    }

    public void update(Pet pet) {

    }

    @SuppressLint("StaticFieldLeak")
    public void deleteById(String petId) {
        PetFirebase.deletePet(petId);
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... petIds) {
                AppLocalDb.db.petDao().deleteById(petIds[0]);
                return "";
            }
        }.execute(petId);
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteAll() {
        PetFirebase.deleteAll();
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                AppLocalDb.db.petDao().deleteAll();
                return "";
            }
        }.execute("");
    }


}
