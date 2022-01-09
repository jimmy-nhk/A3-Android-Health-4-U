package com.example.vendorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Vendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignUpStep1Activity extends AppCompatActivity {
    // Constants
    private final String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
            + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
    private final String TAG ="RegisterActivity";

    // Views
    private EditText emailText, usernameText, fullnameText, storenameText, passwordText, confirmPasswordText ;
    private TextView errorTxt;
    private String fullName, username, email;
    private int vendorSize ;


    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference vendorCollection;

    // Data
    private List<Vendor> vendorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        //Init necessary components
        getViews();
        initService();
        loadVendorData();
        loadSizeClient();

    }

    // load the Vendor data
    private void loadVendorData() {
        vendorCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            Vendor c;
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        c = doc.toObject(Vendor.class);
                        vendorList.add(c);
                        Log.d(TAG, "Vendor data loaded successfully: " + c.toString());
                    }
                }
            }
        });
    }

    // add to authentication
    private void addVendorToAuthentication(String mail, String password){
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()){
                        // Sign in success, update UI
//                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Successfully added new Vendor in Register Activity");
                        addVendorToFireStore();
                    } else {
                        String INVALID_GMAIL = "The gmail format is invalid.";
                        errorTxt.setVisibility(View.VISIBLE);
                        errorTxt.setText(INVALID_GMAIL);

                        Log.w(TAG,"createVendorWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Create account fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addVendorToFireStore() {
        // create Vendor
        Vendor c = new Vendor();
        c.setId(vendorSize);
        c.setEmail(emailText.getText().toString().trim());
        c.setUserName(usernameText.getText().toString().trim());
        c.setFullName(fullnameText.getText().toString().trim());
        c.setStoreName(storenameText.getText().toString().trim());

        vendorCollection.document(vendorSize+"")
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added vendor to FireStore: " + c.toString());
                    updateUI(c);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + c.toString()));
    }

    private void loadSizeClient(){

        vendorCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                vendorSize = value.size();
            }
        });
    }

    //update ui
    private void updateUI(Vendor vendor) {
        Intent intent = new Intent(this, SignUpStep2Activity.class);
        intent.putExtra("vendor", vendor);
        startActivity(intent);
        finish();
    }

//    // Validate vendor's fullname
//    private boolean isFullNameValid(String fullName) {
//        if (fullName.isEmpty()) {
//            passwordText.setError("Name cannot be empty");
//            return false;
//        }
//        return true;
//    }

//    // Validate vendor's phone
//    private boolean isPhoneValid(String phone) {
//        if (phone.isEmpty()) {
//            String EMPTY_PHONE = "Phone cannot be empty. Please try again!";
//            fullNameText.setError(EMPTY_PHONE);
//            return false;
//        } else if (phone.length() != 9) {
//            String INVALID_PHONE = "Invalid phone number. Please enter the last 9 digits" +
//                    "of your phone number!";
//            fullNameText.setError(INVALID_PHONE);
//            return false;
//        }
//
//        return true;
//    }

//    // Validate vendor's dob
//    private boolean isDobValid(String dob) {
//        if (dob.isEmpty()) {
//            String EMPTY_DOB = "Date of birth cannot be empty. Please try again!";
//            dobText.setError(EMPTY_DOB);
//            return false;
//        }
//        return true;
//    }

    // Validate vendor's username
    private boolean isUsernameValid(String username) {
        if (username.isEmpty()) {
            usernameText.setError("Username cannot be empty");
            return false;
        }

        if (!isUsernameUnique(username)) {
            usernameText.setError("This username was already used by another account");
            return false;
        }

        return true;
    }
    // Validate vendor's fullName
    private boolean isFullNameValid(String fullName) {
        if (fullName.isEmpty()) {
            fullnameText.setError("fullName cannot be empty");
            return false;
        }
        return true;
    }
    // Validate vendor's username
    private boolean isStoreNameValid(String storeName) {
        if (storeName.isEmpty()) {
            storenameText.setError("storeName cannot be empty");
            return false;
        }
        return true;
    }

    // Check if username is unique
    private boolean isUsernameUnique(String username){
        for (Vendor vendor: vendorList) {
            if (username.equals(vendor.getUserName())) return false;
        }
        return true;
    }

//    // Validate vendor's storeName
//    private boolean isStoreNameValid(String storeName) {
//        if (storeName.isEmpty()) {
//            storenameText.setError("storeName cannot be empty");
//            return false;
//        }
//
//        if (!isStoreNameUnique(storeName)) {
//            usernameText.setError("This storeName was already used by another account");
//            return false;
//        }
//
//        return true;
//    }
//    // Check if storeName is unique
//    private boolean isStoreNameUnique(String storeName){
//        for (Vendor vendor: vendorList) {
//            if (storeName.equals(vendor.getStoreName())) return false;
//        }
//        return true;
//    }


    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            emailText.setError("Email cannot be empty. Please try again!");
            return false;
        }
        if (!Pattern.compile(emailRegex)
                .matcher(email)
                .matches()) {
            emailText.setError("Invalid email format (e.g abc@gmail.com)");
            return false;
        }
        if (!isEmailUnique(email)) {
            emailText.setError("This email was already used by another account");
            return false;
        }

        return true;
    }

    // Check if username is unique
    private boolean isEmailUnique(String email){
        for (Vendor vendor: vendorList) {
            if (email.equals(vendor.getEmail())) return false;
        }
        return true;
    }

    // validate password
    private boolean isPasswordValid(String password, String confirmPassword) {
//        String password = passwordText.getText().toString();
//        String confirmPassword = confirmPasswordText.getText().toString();

        if (password.isEmpty()) {
            passwordText.setError("Password cannot be empty");
            confirmPasswordText.setError("Password cannot be empty");
            return false;
        }

        // check length
        if (password.length() < 6){
            passwordText.setError("Password cannot have less than 6 characters");
            confirmPasswordText.setError("Password cannot have less than 6 characters");
            return false;
        }

        // check validation
        if (!password.equals(confirmPassword)){
            passwordText.setError("Confirm password does not match");
            confirmPasswordText.setError("Confirm password does not match");
            return false;
        }
        return true;
    }

    private boolean validateInput(String username,
                                  String storeName,
                                  String fullName,
                                  String email,
                                  String password,
                                  String confirmPassword) {
//        boolean isValid = true;

        return isUsernameValid(username)
                && isStoreNameValid(storeName)
                && isFullNameValid(fullName)
//                && isDobValid(dob)
//                && isPhoneValid(phone)
                && isEmailValid(email)
                && isPasswordValid(password, confirmPassword);
    }

    // init service
    public void initService(){
        // Init firestore
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        String CLIENT_COLLECTION = "vendors";
        vendorCollection = fireStore.collection(CLIENT_COLLECTION);
        // init realtime db
//        firebaseDatabase = FirebaseDatabase.getInstance("https://a2-android-56cbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();
        vendorList = new ArrayList<>();


    }

    // attach components
    public void getViews() {
        errorTxt = findViewById(R.id.errorTxt);
        errorTxt.setVisibility(View.INVISIBLE);

        emailText = findViewById(R.id.editEmailSignUpTxt);
        usernameText = findViewById(R.id.editUserNameSignUpTxt);
        storenameText = findViewById(R.id.editStoreNameSignUpTxt);
        fullnameText = findViewById(R.id.editFullNameSignUpTxt);
        passwordText = findViewById(R.id.editPasswordSignUpTxt);
        confirmPasswordText = findViewById(R.id.editConfirmPasswordSignUpTxt);

    }

    public void onNextBtnClick(View view) {
        try {
            String username = usernameText.getText().toString().trim();
            String storeName = storenameText.getText().toString().trim();
            String fullName = fullnameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String confirmPassword = confirmPasswordText.getText().toString().trim();

            if (validateInput(username,storeName, fullName, email, password, confirmPassword))
                addVendorToAuthentication(emailText.getText().toString(), confirmPasswordText.getText().toString());
        } catch (Exception ignored){

        }
    }
}