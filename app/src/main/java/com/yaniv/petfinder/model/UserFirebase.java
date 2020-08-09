package com.yaniv.petfinder.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserFirebase {
    final static String USER_COLLECTION = "users";

    public static void getAllUsers(final UserModel.Listener<List<User>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> usrData = null;
                if (task.isSuccessful()) {
                    usrData = new LinkedList<User>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        User user = doc.toObject(User.class);
                        usrData.add(user);
                    }
                }
                listener.onComplete(usrData);
            }
        });
    }

    public static void getUser(String userId, final UserModel.Listener<User> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).whereEqualTo("id", userId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        User usr = null;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> json = doc.getData();
                                usr = factory(json);
                            }
                        }
                        listener.onComplete(usr);
                    }
                }
        );
    }

    public static void addUser(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).document(user.getId()).set(toJson(user));
    }

    private static User factory(Map<String, Object> json) {
        User user = new User();
        user.id = (String) json.get("id");
        user.name = (String) json.get("name");
        user.imageUrl = (String) json.get("imageUrl");
        return user;
    }

    private static Map<String, Object> toJson(User user) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", user.id);
        result.put("name", user.name);
        result.put("imageUrl", user.imageUrl);
        return result;
    }

}
