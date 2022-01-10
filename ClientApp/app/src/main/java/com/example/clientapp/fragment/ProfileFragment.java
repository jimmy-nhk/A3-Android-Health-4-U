package com.example.clientapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientapp.R;
import com.example.clientapp.helper.adapter.NewStoreRecyclerViewAdapter;
import com.example.clientapp.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class ProfileFragment extends Fragment {
    // request code
    private final int PICK_IMAGE_REQUEST = 22;


    private static final String TAG = "ProfileFragment";

    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView dobTextView;
    private TextView weightTextView;
    private TextView heightTextView;
    private ImageView profileImage;
    private CardView backBtn;

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String dob;
    private double weight;
    private double height;

    private FirebaseFirestore fireStore;
    private DocumentReference clientDocRef;
    private Client client;

    // Upload pfp
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            client = getArguments().getParcelable("client");
            username = client.getUserName();

            Log.d(TAG, "client=" + client);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getViews(view);
        initService(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void initService(View view) {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        clientDocRef = fireStore.collection("clients").document(client.getId() + "");

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // load items
        clientDocRef.addSnapshotListener((value, error) -> {
            if (value != null) {
                Log.d(TAG, "value != null");
                Client c = value.toObject(Client.class);

                Log.d(TAG, "c != null=" + (c != null));
                if (c != null) {
                    fullName = c.getFullName();
                    username = c.getUserName();
                    email = c.getEmail();
                    phone = c.getPhone();
                    dob = c.getDob();
                    weight = c.getWeight();
                    height = c.getHeight();

                    displayUserInfo();
                }
            }
        });
    }

    private void displayUserInfo() {
        fullNameTextView.setText(fullName);
        usernameTextView.setText(username);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        dobTextView.setText(dob);
        weightTextView.setText(String.valueOf(weight));
        heightTextView.setText(String.valueOf(height));
        setStoreImage(client.getImage());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void getViews(View view) {
        try {
            fullNameTextView = view.findViewById(R.id.fullNameTxt);
            usernameTextView = view.findViewById(R.id.usernameTxt);
            emailTextView = view.findViewById(R.id.emailTxt);
            phoneTextView = view.findViewById(R.id.phoneTxt);
            dobTextView = view.findViewById(R.id.dobTxt);
            weightTextView = view.findViewById(R.id.weightTxt);
            heightTextView = view.findViewById(R.id.heightTxt);
            CardView whiteCover = view.findViewById(R.id.whiteCoverCard);

            // Profile Image
            profileImage = view.findViewById(R.id.profileImage);
            profileImage.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    whiteCover.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    whiteCover.setVisibility(View.GONE);
                }
                return false;
            });
            profileImage.setOnLongClickListener(v -> {
                whiteCover.setVisibility(View.VISIBLE);
                return false;
            });
            profileImage.setOnClickListener(v -> handleProfileImageClick());

            // Back button
            backBtn = view.findViewById(R.id.backCardBtn);
            backBtn.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    backBtn.setCardBackgroundColor(getResources().getColor(R.color.white_transparent
                            , requireContext().getTheme()));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    backBtn.setCardBackgroundColor(getResources().getColor(R.color.transparent_100
                            , requireContext().getTheme()));
                }
                return false;
            });
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

    private void handleProfileImageClick() {
        selectImage();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
            try {
                Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmapImg);
                profileImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Adding item...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "items/"
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
                                // Call function to upload item to DB
                                updateProfileImage(path);

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

    private void updateProfileImage(String path) {
//        Toast.makeText(getContext(), "path=" + path, Toast.LENGTH_SHORT).show();

        clientDocRef
                .update("image", path)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    Toast.makeText(getContext(), "update succeeded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Error updating document path=" + path, Toast.LENGTH_SHORT).show();
                });

    }

    private void setStoreImage(String imageUrl) {
        try {
            if (imageUrl.length() > 0) {
//                Log.d("setClientImage", imageUrl);
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReference(imageUrl);

                final long ONE_MEGABYTE = 1024 * 1024 *5;
                // Handle any errors
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            if (isAdded()){
                                DisplayMetrics dm = new DisplayMetrics();
                                ((Activity) requireContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

                                profileImage.setMinimumHeight(dm.heightPixels);
                                profileImage.setMinimumWidth(dm.widthPixels);
                                profileImage.setImageBitmap(bm);
                            }

                        }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (Exception e) {
//            .setImageResource(R.drawable.bun); //Set something else
            e.printStackTrace();
        }
    }
}