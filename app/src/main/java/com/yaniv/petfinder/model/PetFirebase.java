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
        Timestamp ts = new Timestamp(since,0);
        db.collection(PET_COLLECTION).whereGreaterThanOrEqualTo("lastUpdated", ts)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Pet> stData = null;
                if (task.isSuccessful()){
                    stData = new LinkedList<Pet>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Map<String, Object> json = doc.getData();
                        Pet pet = factory(json);
                        stData.add(pet);
                    }
                }
                listener.onComplete(stData);
                Log.d("TAG","refresh " + stData.size());
            }
        });
    }

    public static void getAllPets(final PetModel.Listener<List<Pet>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PET_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Pet> stData = null;
                if (task.isSuccessful()){
                    stData = new LinkedList<Pet>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Pet pet = doc.toObject(Pet.class);
                        stData.add(pet);
                    }
                }
                listener.onComplete(stData);
            }
        });
    }

    public static void addPet(Pet pet, final PetModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PET_COLLECTION).document(pet.getId()).set(toJson(pet)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener!=null){
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    private static Pet factory(Map<String, Object> json){
        Pet st = new Pet();
        st.id = (String)json.get("id");
        st.name = (String)json.get("name");
        st.imgUrl = (String)json.get("imgUrl");
        Timestamp ts = (Timestamp)json.get("lastUpdated");
        if (ts != null) st.lastUpdated = ts.getSeconds();
        return st;
    }

    private static Map<String, Object> toJson(Pet st){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", st.id);
        result.put("name", st.name);
        result.put("imgUrl", st.imgUrl);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        return result;
    }

}
