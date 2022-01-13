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
import android.widget.TextView;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    // attributes
    private static final String ARG_VENDOR = "vendor";
    private static final String TAG = "ProfileFragment";
    private static final String VENDOR_COLLECTION = "vendors";

    private EditText fullNameTxt;
    private TextView usernameTxt;
    private TextView emailTxt;
    private EditText phoneTxt;
    private EditText addressTxt;
    private TextView ratingTxt;
    private TextView totalSalesTxt;

    private ImageView coverImg;
    private ImageButton changeImgBtn;
    private ImageView backBtn;
    private Button profileSaveBtn;
    private Vendor vendor;
    private boolean isImageChanged = false;

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
            initUpdateImgEventHandler(view);
            //Add event wait for change save profile btn click
            initSaveProfileHandler(view);
        }

        // Inflate the layout for this fragment
        return view;
    }

    //Init back button on click handler function
    private void initBackBtnClick() {
        try {
            // backBtn on Click event Handler
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

    //function init save profile button
    private void initSaveProfileHandler(View view) {
        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The app validate fullname, phone and address before upload, only execute upload when all is true
                if (validateInput(fullNameTxt.getText().toString(), phoneTxt.getText().toString(), addressTxt.getText().toString())) {
                    // Only upload image if image is Changed => save storage
                    if (isImageChanged) {
                        uploadImage(); // call upload image function, in upload image response, there is call of upload profile function.
                        isImageChanged = false; // set back to false if user change image again
                    }else{
                        uploadProfileToDB(); // call upload profile without upload image
                    }
                }
            }
        });
    }

    //input validation, if all true return true
    private boolean validateInput(String fullName, String phone, String address) {

        return validateFullName(fullName)
                && isPhoneValid(phone)
                && isAddressValid(address);
    }

    //Address validation if empty
    private boolean isAddressValid(String address) {
        if (address.isEmpty()) {
            addressTxt.setError("Address cannot be empty");
            return false;
        }

        return true;
    }

    //Fullname validation if empty
    private boolean validateFullName(String fullName) {
        if (fullName.isEmpty()) {
            fullNameTxt.setError("Full name cannot be empty");
            return false;
        }

        return true;
    }

    // Validate vendor's phone
    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            String EMPTY_PHONE = "Phone cannot be empty";
            phoneTxt.setError(EMPTY_PHONE);
            return false;
        } else if (countDigits(phone) != 9) { // check phone length
            String INVALID_PHONE = "Invalid phone number. Please enter the last 9 digits" +
                    "of your phone number!";
            phoneTxt.setError(INVALID_PHONE);
            return false;
        }

        return true;
    }
    // count digits function
    private int countDigits(String stringToSearch) {
        Pattern digitRegex = Pattern.compile("\\d");
        Matcher countEmailMatcher = digitRegex.matcher(stringToSearch);

        int count = 0;
        while (countEmailMatcher.find()) {
            count++;
        }

        return count;
    }
    private void initUpdateImgEventHandler(View view) {
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

        //set value for rating and totalsale text view
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
                // set state for isImage
                isImageChanged = true;

                coverImg.setImageBitmap(bitmapImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}