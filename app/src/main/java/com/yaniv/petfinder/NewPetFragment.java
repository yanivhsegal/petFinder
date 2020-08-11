package com.yaniv.petfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
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
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.yaniv.petfinder.model.StoreModel;
import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPetFragment extends Fragment {

    public NewPetFragment() {
        // Required empty public constructor
    }

    int currentImageIndex = 0;
    Pet pet;
    View view;
    ImageView imgaeView;
    TextView nameTv;
    TextView description;
    List<Bitmap> imageBitmap = new ArrayList<>();
    ProgressBar progressbr;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_pet, container, false);

        pet = NewPetFragmentArgs.fromBundle(getArguments()).getPet();
        mAuth = FirebaseAuth.getInstance();
        progressbr = view.findViewById(R.id.new_pet_progress);
        progressbr.setVisibility(View.INVISIBLE);
        imgaeView = view.findViewById(R.id.new_pet_image_v);
        Button uploadFromGalleryBtn = view.findViewById(R.id.new_pet_upload_from_gallery);
        uploadFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFromGallery();
            }
        });
        Button takePhotoBtn = view.findViewById(R.id.new_pet_take_photo_btn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        AppCompatImageButton imageBack = view.findViewById(R.id.image_back_btn);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageBitmap.size() != 0) {
                    if (currentImageIndex == 0) {
                        currentImageIndex = imageBitmap.size() - 1;
                    } else {
                        currentImageIndex--;
                    }

                    imgaeView.setImageBitmap(imageBitmap.get(currentImageIndex));

                } else if (pet != null && pet.imgUrl != null) {
                    if (currentImageIndex == 0) {
                        currentImageIndex = pet.imgUrl.size() - 1;
                    } else {
                        currentImageIndex--;
                    }
                    if (!pet.imgUrl.get(currentImageIndex).equals("")) {
                        Picasso.get().load(pet.imgUrl.get(currentImageIndex)).placeholder(R.drawable.avatar).into(imgaeView);
                    }
                }
            }
        });

        AppCompatImageButton imageNext = view.findViewById(R.id.image_next_btn);
        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageBitmap.size() != 0) {
                    if (currentImageIndex == imageBitmap.size() - 1) {
                        currentImageIndex = 0;
                    } else {
                        currentImageIndex++;
                    }

                    imgaeView.setImageBitmap(imageBitmap.get(currentImageIndex));

                } else if (pet != null && pet.imgUrl != null) {
                    if (currentImageIndex == pet.imgUrl.size() - 1) {
                        currentImageIndex = 0;
                    } else {
                        currentImageIndex++;
                    }
                    if (!pet.imgUrl.get(currentImageIndex).equals("")) {
                        Picasso.get().load(pet.imgUrl.get(currentImageIndex)).placeholder(R.drawable.avatar).into(imgaeView);
                    }
                }
            }
        });

        if (pet != null) {
            nameTv.setText(pet.name);
            description.setText(pet.description);
            if (pet.imgUrl != null && pet.imgUrl.get(0) != "") {
                Picasso.get().load(pet.imgUrl.get(0)).placeholder(R.drawable.avatar).into(imgaeView);
            }
        }

        return view;
    }

    void savePet() {
        progressbr.setVisibility(View.VISIBLE);
        final String name = nameTv.getText().toString();
        final String desc = description.getText().toString();
        final String id = pet != null && !pet.getId().equals("") ? pet.getId() : UUID.randomUUID().toString();
        Date d = new Date();
        if (imageBitmap != null && imageBitmap.size() != 0) {
            StoreModel.uploadImages(imageBitmap, "my_photo" + d.getTime(), new StoreModel.Listener() {
                @Override
                public void onSuccess(List<String> uris) {
                    Pet pt = new Pet(id, name, uris, desc, mAuth.getCurrentUser().getUid());
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
                    Snackbar mySnackBar = Snackbar.make(view, R.string.fail_to_save_pet, Snackbar.LENGTH_LONG);
                    mySnackBar.show();
                }
            });
        } else {
            Pet pt = new Pet(id, name, pet.imgUrl, desc, mAuth.getCurrentUser().getUid());
            PetModel.instance.addPet(pt, new PetModel.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    NavController navCtrl = Navigation.findNavController(view);
                    navCtrl.navigateUp();
                }
            });
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = 0;

    void uploadFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        String[] extraMimeTypes = {"image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    void takePhoto() {
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Uri> imagesUri = new ArrayList<>();
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                Bundle extras = data.getExtras();
                imageBitmap.add(rotateImage((Bitmap) extras.get("data")));
            } else if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    imagesUri.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                imagesUri.add(data.getData());
            }

            for (Uri imageUri : imagesUri) {
                try {
                    imageBitmap.add(rotateImage(MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageUri)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            imgaeView.setImageBitmap(imageBitmap.get(0));
        }
    }

    public static Bitmap rotateImage(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
