package com.yaniv.petfinder.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StoreModel {

    public interface Listener {
        void onSuccess(List<String> urls);

        void onFail();
    }

    public static void uploadImages(List<Bitmap> imagesBmp, String name, final Listener listener) {
        if (imagesBmp != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            int count = 0;
            final List<String> uris = new ArrayList<>();
            for (Bitmap imgBmp : imagesBmp) {
                final StorageReference imagesRef = storage.getReference().child("images").child(name + count);
                count++;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = imagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        listener.onFail();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uris.add(uri.toString());
                            }
                        });
                    }
                });
            }
            listener.onSuccess(uris);
        } else {
            listener.onFail();
        }
    }
}
