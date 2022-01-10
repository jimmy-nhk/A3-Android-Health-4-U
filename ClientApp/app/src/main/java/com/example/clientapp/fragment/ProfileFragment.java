package com.example.clientapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private TextView addressTextView;
    private TextView dobTextView;
    private TextView weightTextView;
    private TextView heightTextView;
    private ImageView profileImage;
    private CardView whiteCover;
    private CardView backBtn;
    private Button saveChangesBtn;

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String dob;
    private double weight;
    private double height;

    private FirebaseFirestore fireStore;
    private DocumentReference clientDocRef;
    private Client client;
    private boolean isImageChanged;
    private final Calendar calendar = Calendar.getInstance();

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
        initService();
        isImageChanged = false;

        // Inflate the layout for this fragment
        return view;
    }

    private void initService() {
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
                    address = c.getAddress();
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
        addressTextView.setText(address);
        dobTextView.setText(dob);
        weightTextView.setText(String.valueOf(weight));
        heightTextView.setText(String.valueOf(height));
        setStoreImage(client.getImage());
    }

    // update fire store client
    private void updateFirestoreClient(String phone,
                                       String address,
                                       String dob,
                                       String weightStr,
                                       String heightStr) {

        clientDocRef
                .update("phone", phone,
                        "address", address,
                        "dob", dob,
                        "weight", (Double.parseDouble(weightStr)),
                        "height", (Double.parseDouble(heightStr)))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    Toast.makeText(getContext(), "Successfully updated client information", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(getContext(), "Error updating client phone, address, etc.", Toast.LENGTH_SHORT).show();
                });
    }

    private void getViews(View view) {
        try {
            fullNameTextView = view.findViewById(R.id.fullNameTxt);
            usernameTextView = view.findViewById(R.id.usernameTxt);
            emailTextView = view.findViewById(R.id.emailTxt);
            phoneTextView = view.findViewById(R.id.phoneTxt);
            addressTextView = view.findViewById(R.id.addressTxt);
            dobTextView = view.findViewById(R.id.dobTxt);
            weightTextView = view.findViewById(R.id.weightTxt);
            heightTextView = view.findViewById(R.id.heightTxt);
            whiteCover = view.findViewById(R.id.whiteCoverCard);
            saveChangesBtn = view.findViewById(R.id.saveChangesBtn);

            // Init date picker
            initDatePicker();

            // Profile Image
            profileImage = view.findViewById(R.id.profileImage);
            initProfileImageView();

            // Back button
            backBtn = view.findViewById(R.id.backCardBtn);
            initBackBtnView();

            // Save changes button
            saveChangesBtn.setOnClickListener(v -> handleSaveChangesBtnClick());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDatePicker() {
        try {
            // date picker dialog
            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDobLabel();
            };

            //edit dob
            dobTextView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return true;
            });

            dobTextView.setOnClickListener(v -> new DatePickerDialog(requireContext(), date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDobLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dobTextView.setText(sdf.format(calendar.getTime()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initProfileImageView() {
        try {
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
            profileImage.setOnClickListener(v -> selectImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initBackBtnView() {
        try {
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

    private void handleSaveChangesBtnClick() {
        if (isImageChanged)
            uploadImage();

        String phone = phoneTextView.getText().toString().trim();
        String address = addressTextView.getText().toString().trim();
        String dob = dobTextView.getText().toString().trim();
        String weightStr = weightTextView.getText().toString().trim();
        String heightStr = heightTextView.getText().toString().trim();
        updateFirestoreClient(phone, address, dob, weightStr, heightStr);
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
            isImageChanged = true;
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