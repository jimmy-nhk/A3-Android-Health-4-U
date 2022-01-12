package com.example.vendorapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Item;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    // attributes
    private static final String ARG_VENDOR = "vendor";
    private static final String TAG = "ProfileFragment";
    private static final String VENDOR_COLLECTION = "vendors";

    private EditText fullNameTxt, usernameTxt, emailTxt, phoneTxt, addressTxt, ratingTxt, totalSalesTxt;
    private ImageView coverImg;
    private ImageButton changeImgBtn;
    private ImageView backBtn;
    private Button profileSaveBtn;
    private Vendor vendor;

    // request code
    private final int PICK_IMAGE_REQUEST = 33;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore fireStore;
    private CollectionReference vendorCollection;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        vendorCollection = fireStore.collection(VENDOR_COLLECTION);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (getArguments() != null) {
            vendor = getArguments().getParcelable(ARG_VENDOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getViews(view);
        //Back button on click
            initBackBtnClick();
        // display vendor
        if (vendor != null) {
            updateUI();
            //Add event wait for change img btn click
            onUpdateImg(view);
            //Add event wait for change save profile btn click
            onSaveProfile(view);
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void initBackBtnClick() {
        try {
            backBtn.setOnClickListener(v -> {
                if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    requireActivity().finish();
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSaveProfile(View view) {
        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The app upload image first then call update vendor profile on image response successfull
                uploadImage();
            }
        });
    }

    private void onUpdateImg(View view) {
        changeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void uploadProfileToDB() {
        // create vendor
        vendor.setFullName(fullNameTxt.getText().toString());
        vendor.setStoreName(usernameTxt.getText().toString());
        vendor.setEmail(emailTxt.getText().toString());
        vendor.setPhone(phoneTxt.getText().toString());
        vendor.setAddress(addressTxt.getText().toString());
        vendor.setRating(Double.parseDouble(ratingTxt.getText().toString()));
        vendor.setTotalSale(Integer.parseInt(totalSalesTxt.getText().toString()));

        // vendor collection
        vendorCollection.document(vendor.getId() + "")
                .update(vendor.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added vendor to FireStore: " + vendor.toString());
                    updateUI();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + vendor.toString()));

    }

    // update ui
    private void updateUI() {
        fullNameTxt.setText(vendor.getFullName());
        usernameTxt.setText(vendor.getUserName());
        emailTxt.setText(vendor.getEmail());
        phoneTxt.setText(vendor.getPhone());
        addressTxt.setText(vendor.getAddress());

        try {
            // storage imgRef
            StorageReference mImageRef =
                    FirebaseStorage.getInstance().getReference(vendor.getImage());
            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            mImageRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

                            // coverImg
                            coverImg.setMinimumHeight(dm.heightPixels);
                            coverImg.setMinimumWidth(dm.widthPixels);
                            coverImg.setImageBitmap(bm);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (Exception e) {

        }
        // String rating totalSalesStr
        String ratingStr = String.valueOf(vendor.getRating());
        String totalSalesStr = String.valueOf(vendor.getTotalSale());
        ratingTxt.setText(ratingStr);
        totalSalesTxt.setText(totalSalesStr);
    }


    //get views
    private void getViews(View view) {
        fullNameTxt = view.findViewById(R.id.editFullName);
        usernameTxt = view.findViewById(R.id.editUsername);
        emailTxt = view.findViewById(R.id.editEmail);
        phoneTxt = view.findViewById(R.id.editPhone);
        addressTxt = view.findViewById(R.id.editAddress);
        ratingTxt = view.findViewById(R.id.editRating);
        totalSalesTxt = view.findViewById(R.id.editTotalSales);
        coverImg = view.findViewById(R.id.profileCoverImg);
        changeImgBtn = view.findViewById(R.id.changeCoverImgBtn);
        backBtn = view.findViewById(R.id.backBtn);
        profileSaveBtn = view.findViewById(R.id.profileSaveBtn);
    }

    // Select Image method
    private void selectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Adding vendor...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "vendors/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                // get path to add to item ọbject
                                String path = taskSnapshot.getStorage().getPath();
                                vendor.setImage(path);
                                uploadProfileToDB();
                                // Image uploaded successfully, turn off the process dialog
                                progressDialog.dismiss();
                            })

                    .addOnFailureListener(e -> {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Added "
                                                + (int) progress + "%");
                            });
        }
    }

    // In your activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                coverImg.setImageBitmap(bitmapImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}