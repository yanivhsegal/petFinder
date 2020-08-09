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

public class PetFirebase {
    final static String PET_COLLECTION = "pets";

    public static void getAllPetsSince(long since, final PetModel.Listener<List<Pet>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(since, 0);
        db.collection(PET_COLLECTION).whereGreaterThanOrEqualTo("lastUpdated", ts)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Pet> ptData = null;
                if (task.isSuccessful()) {
                    ptData = new LinkedList<Pet>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Map<String, Object> json = doc.getData();
                        Pet pet = factory(json);
                        ptData.add(pet);
                    }
                }
                listener.onComplete(ptData);
                Log.d("TAG", "refresh " + ptData.size());
            }
        });
    }

    public static void addPet(Pet pet, final PetModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PET_COLLECTION).document(pet.getId()).set(toJson(pet)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    public static void deletePet(String petId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PET_COLLECTION).document(petId).delete();
    }

    public static void deleteAll() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PET_COLLECTION).document().delete();
    }


    private static Pet factory(Map<String, Object> json) {
        Pet pet = new Pet();
        pet.id = (String) json.get("id");
        pet.name = (String) json.get("name");
        pet.imgUrl = Converters.fromString((String) json.get("imgUrl"));
        pet.description = (String) json.get("description");
        pet.ownerId = (String) json.get("ownerId");
        Timestamp ts = (Timestamp) json.get("lastUpdated");
        if (ts != null) pet.lastUpdated = ts.getSeconds();
        return pet;
    }

    private static Map<String, Object> toJson(Pet pt) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", pt.id);
        result.put("name", pt.name);
        result.put("imgUrl", pt.imgUrl);
        result.put("description", pt.description);
        result.put("ownerId", pt.ownerId);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        return result;
    }

}
