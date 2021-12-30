package com.example.vendorapp.activity;

import com.example.vendorapp.R;
import com.example.vendorapp.helper.MaskWatcher;
import com.example.vendorapp.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int vendorID = 1;
    private EditText nameTxt, descriptionTxt, categoryTxt, priceTxt, quantityTxt, caloriesTxt, expireDateTxt;
    private ImageView addedImageListview;
    private Button addImageBtn;
    private String category;

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
        getViews();
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

    private void getViews() {
        nameTxt = findViewById(R.id.nameAdditemTxt);
//        categoryTxt = findViewById(R.id.categoryAdditemTxt);
        descriptionTxt = findViewById(R.id.descriptionAdditemTxt);
        priceTxt = findViewById(R.id.priceAdditemTxt);
        quantityTxt = findViewById(R.id.quantityAdditemTxt);
        caloriesTxt = findViewById(R.id.caloriesAddItemTxt);
        expireDateTxt = findViewById(R.id.expireDateAdditemTxt);
        addedImageListview = findViewById(R.id.addedImageview);
        addImageBtn = findViewById(R.id.addImageBtn);

        expireDateTxt.addTextChangedListener(new MaskWatcher("##/##/####"));
        Spinner spinner = findViewById(R.id.categorySpinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void addAddItemOnClick(View view) {
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
//        item.setCategory(categoryTxt.getText().toString().trim());
        item.setCategory(category);
        item.setDescription(descriptionTxt.getText().toString().trim());
        item.setImage(imageURL);
        item.setExpireDate(expireDateTxt.getText().toString().trim());
        item.setVendorID(vendorID);
        item.setQuantity(Integer.parseInt(quantityTxt.getText().toString().trim()));
        item.setPrice(Double.parseDouble(priceTxt.getText().toString().trim()));
        item.setCalories(Double.parseDouble(caloriesTxt.getText().toString().trim()));
        itemCollection.document(itemId + "")
                .set(item.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added item to FireStore: " + item.toString());
                    updateUI(item);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + item.toString()));

    }

    private boolean validate() {

        return isItemNameValid()
                && isItemExpiredDateValid()
                && isItemCaloriesValid()
                && isItemPriceValid()
                && isItemDescriptionValid()
                && isItemQuantityValid()
                && isItemImageValid();
    }

    // Validate item's name
    private boolean isItemNameValid() {
        if (nameTxt.getText().toString().isEmpty()) {
            nameTxt.setError("Item name cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's description
    private boolean isItemDescriptionValid() {
        if (descriptionTxt.getText().toString().isEmpty()) {
            descriptionTxt.setError("Item description cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's price
    private boolean isItemPriceValid() {
        if (priceTxt.getText().toString().isEmpty()) {
            priceTxt.setError("Item price cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's quantity
    private boolean isItemQuantityValid() {
        if (quantityTxt.getText().toString().isEmpty()) {
            quantityTxt.setError("Item quantity cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's calories
    private boolean isItemCaloriesValid() {
        if (caloriesTxt.getText().toString().isEmpty()) {
            caloriesTxt.setError("Item calories cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's expire day
    private boolean isItemExpiredDateValid() {

        if (expireDateTxt.getText().toString().isEmpty()) {
            expireDateTxt.setError("Item expire day cannot be empty");
            return false;
        }
        return true;
    }

    // Validate item's image day
    private boolean isItemImageValid() {
        if (addedImageListview.getVisibility() == View.GONE) {
            addImageBtn.setError("Item image cannot be empty");
            return false;
        }
        return true;
    }

    //cancel button - finish activity
    public void cancelAddItemOnClick(View view) {
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

    public void uploadImageAddItemOnClick(View view) {
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

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
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
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    // get path to add to item ọbject
                                    String path = taskSnapshot.getStorage().getPath();
                                    // Call function to upload item to DB
                                    uploadItemToDB(path);

                                    // Image uploaded successfully, turn off the process dialog
                                    progressDialog.dismiss();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
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
                                    progressDialog.setMessage(
                                            "Added "
                                                    + (int) progress + "%");
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
                addedImageListview.setImageBitmap(bitmapImg);
                addedImageListview.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category = "Rice";
    }
}