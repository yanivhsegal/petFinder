package com.yaniv.petfinder.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.yaniv.petfinder.MyApplication;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UserModel {
    public static final UserModel instance = new UserModel();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompListener {
        void onComplete();
    }

    private UserModel() {
    }

    @SuppressLint("StaticFieldLeak")
    public void addUser(User user) {
        new AsyncTask<User, String, String>() {
            @Override
            protected String doInBackground(User... user) {
                AppLocalDb.db.usersDao().insertAll(user[0]);
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute(user);
    }

//    public LiveData<List<User>> getAllPets() {
//        LiveData<List<Pet>> liveData = AppLocalDb.db.petDao().getAll();
//        return liveData;
//    }


    public User getUser(String id) {
        return AppLocalDb.db.usersDao().getUser(id);
    }

    public void update(User user) {

    }

    public void delete(User user) {

    }


}
