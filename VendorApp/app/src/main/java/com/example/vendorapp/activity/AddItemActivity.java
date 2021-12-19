package com.example.vendorapp.activity;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Item;
import com.example.vendorapp.model.Vendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {
    private EditText nameTxt, descriptionTxt, categoryTxt, priceTxt, quantityTxt, caloriesTxt, expireDateTxt;
    private ListView addedImageListview;

    private static final String TAG = "AddItemActivity";
    private static final String ITEM_COLLECTION = "items";
    private FirebaseFirestore fireStore;
    private CollectionReference itemCollection;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Log.d(TAG, "on Login AddItemActivity Create");

        // init services
        attachComponents();
        initService();
    }

    private void initService() {
        // init fireStore db
        fireStore = FirebaseFirestore.getInstance();
        itemCollection = fireStore.collection(ITEM_COLLECTION);

        //Fetch item from server
        itemList = new ArrayList<>(); //Reset value of item List

        // load items
        itemCollection.addSnapshotListener((value, error) -> {

            // clear again
            itemList.clear();

            // iterate through the list
            for (QueryDocumentSnapshot doc : value) {
                itemList.add(doc.toObject(Item.class));
            }
            Log.d(TAG, "Total item count: " + itemList.size());
        });

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void attachComponents() {
        nameTxt = findViewById(R.id.nameAdditemTxt);
        categoryTxt = findViewById(R.id.categoryAdditemTxt);
        descriptionTxt = findViewById(R.id.descriptionAdditemTxt);
        priceTxt = findViewById(R.id.priceAdditemTxt);
        quantityTxt = findViewById(R.id.quantityAdditemTxt);
        caloriesTxt = findViewById(R.id.caloriesAdditemTxt);
        expireDateTxt = findViewById(R.id.expireDateAdditemTxt);
        addedImageListview = findViewById(R.id.addedImageListview);
    }

    public void addAdditemOnClick(View view) {
        // Add item on ADD button clicked
        //Validation
        if (!validate())
            return;
        uploadImage();

    }

    public void uploadItemToDB(String imageURL) {
        // create Item
        int itemId = itemList.size() + 1;
        Item item = new Item();
        item.setId(itemId);
        item.setName(nameTxt.getText().toString().trim());
        item.setCategory(categoryTxt.getText().toString().trim());
        item.setDescription(descriptionTxt.getText().toString().trim());
        item.setImage(imageURL);
        item.setExpireDate(expireDateTxt.getText().toString().trim());
        item.setVendorID(1);
        item.setQuantity(Integer.parseInt(quantityTxt.getText().toString().trim()));
        itemCollection.document(itemId + "")
                .set(item.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added item to FireStore: " + item.toString());
                    updateUI(item);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + item.toString()));

    }

    private boolean validate() {
        return true;
    }

    //cancel button - finish activity
    public void cancelAdditemOnClick(View view) {
        //Finish activity on CANCEL button clicked
        try {
            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        } catch (Exception e) {
            Log.d(TAG, "Cannot finish AdditemActivity");
        }
    }

    //update ui
    private void updateUI(Item item) {
        try {
            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Log.d(TAG, "Cannot finish AdditemActivity");
        }
    }

    public void uploadImageAdditemOnClick(View view) {
        SelectImage();
    }

    // Select Image method
    private void SelectImage() {

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
//            ProgressDialog progressDialog
//                    = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "items/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                            new OnCompleteListener<Uri>() {

                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    //Get image link from uploadded iamge to firestore
                                                    String fileLink = task.getResult().toString();
                                                    // Call function to upload item to DB
                                                    uploadItemToDB(fileLink);
                                                }
                                            });
                                    // Image uploaded successfully
                                    // Dismiss dialog
//                                    progressDialog.dismiss();
//                                    Toast.makeText(AddItemActivity.this,
//                                                    "Image Uploaded!!",
//                                                    Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
//                            progressDialog.dismiss();
//                            Toast.makeText(AddItemActivity.this,
//                                            "Failed " + e.getMessage(),
//                                            Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
//                                    progressDialog.setMessage(
//                                            "Uploaded "
//                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}