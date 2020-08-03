package com.yaniv.petfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.yaniv.petfinder.model.StoreModel;
import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;

import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPetFragment extends Fragment {

    public NewPetFragment() {
        // Required empty public constructor
    }

    View view;
    ImageView imgaeView;
    TextView nameTv;
    TextView description;
    Bitmap imageBitmap;
    ProgressBar progressbr;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_pet, container, false);

        mAuth = FirebaseAuth.getInstance();
        progressbr = view.findViewById(R.id.new_pet_progress);
        progressbr.setVisibility(View.INVISIBLE);
        imgaeView = view.findViewById(R.id.new_pet_image_v);
        Button takePhBtn = view.findViewById(R.id.new_pet_take_photo_btn);
        takePhBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        nameTv = view.findViewById(R.id.new_pet_name_tv);
        description = view.findViewById(R.id.new_pet_description);

        Button saveBtn = view.findViewById(R.id.new_pet_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePet();
            }
        });
        return view;
    }

    void savePet() {
        progressbr.setVisibility(View.VISIBLE);
        final String name = nameTv.getText().toString();
        final String id = description.getText().toString();

        Date d = new Date();
        StoreModel.uploadImage(imageBitmap, "my_photo" + d.getTime(), new StoreModel.Listener() {
            @Override
            public void onSuccess(String url) {
                Log.d("TAG", "url: " + url);
                Pet pt = new Pet(id, name, url, mAuth.getCurrentUser().getUid());
                PetModel.instance.addPet(pt, new PetModel.Listener<Boolean>() {
                    @Override
                    public void onComplete(Boolean data) {
                        NavController navCtrl = Navigation.findNavController(view);
                        navCtrl.navigateUp();
                    }
                });
            }

            @Override
            public void onFail() {
                progressbr.setVisibility(View.INVISIBLE);
                Snackbar mySnackbar = Snackbar.make(view, R.string.fail_to_save_pet, Snackbar.LENGTH_LONG);
                mySnackbar.show();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = 0;

    void takePhoto() {
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //    private Bitmap imageBitmap;
//    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = rotateImage((Bitmap) extras.get("data"));
            imgaeView.setImageBitmap(imageBitmap);
        }
    }

    public static Bitmap rotateImage(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
