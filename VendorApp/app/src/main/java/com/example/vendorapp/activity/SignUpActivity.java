package com.example.vendorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vendorapp.R;
import com.example.vendorapp.model.Vendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    // Constants
    private final String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
            + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
    private final String TAG ="RegisterActivity";

    // Views
    private EditText emailText, usernameText, passwordText, confirmPasswordText
            , fullNameText, phoneText, dobText;
    private TextView errorTxt;
    private Button signUpBtn;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore ;
    private CollectionReference userCollection;

    // Data
    private List<Vendor> vendorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Init necessary components
        getViews();
        initService();
        loadVendorData();

        signUpBtn.setOnClickListener(v -> {
            Log.d(TAG, "signUpBtn");

//            // validate name
//            if (!isUsernameUnique(usernameText.getText().toString())){
////                usernameText.setError("Username already existed");
//                Log.d(TAG, "Username already exists");
//                return;
//            }
//
//            // validate mail
//            if (!isEmailUnique(emailText.getText().toString())){
////                emailText.setError("Email already existed");
//                Log.d(TAG, "Email already exists");
//                return;
//            }
//
//            // validate the password
//            if (!isPasswordValid(passwordText.getText().toString(), confirmPasswordText.getText().toString())){
//                Log.d(TAG, "Password does not match or less than 6 characters ");
//                return;
//            }
            String username = usernameText.getText().toString().trim();
            String fullName = "fullname";
            String email = emailText.getText().toString().trim();
            String phone = "123456789";
            String dob = "123456789";
            String password = passwordText.getText().toString().trim();
            String confirmPassword = confirmPasswordText.getText().toString().trim();
            if (validateInput(username, email, password, confirmPassword, fullName, phone, dob))
                addVendorToAuthentication(emailText.getText().toString(),  confirmPasswordText.getText().toString());
        });

    }

    // load the Vendor data
    private void loadVendorData() {
        userCollection.addSnapshotListener((value, error) -> {
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
        String username = usernameText.getText().toString().trim();
        Vendor c = new Vendor();
        c.setEmail(emailText.getText().toString().trim());
        c.setUsername(usernameText.getText().toString().trim());

        userCollection.document(username)
                .set(c.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added vendor to FireStore: " + c.toString());
                    updateUI(c);
                })
                .addOnFailureListener(e -> Log.d(TAG, "Fail to add vendor to FireStore: " + c.toString()));
    }

    //update ui
    private void updateUI(Vendor vendor) {
        Intent intent = new Intent(SignUpActivity.this , LogInActivity.class);
        intent.putExtra("email" , vendor.getEmail());
        setResult(RESULT_OK , intent);
        finish();
    }

    // Validate vendor's fullname
    private boolean isFullNameValid(String fullName) {
        if (fullName.isEmpty()) {
            passwordText.setError("Name cannot be empty");
            return false;
        }
        return true;
    }

    // Validate vendor's phone
    private boolean isPhoneValid(String phone) {
        if (phone.isEmpty()) {
            String EMPTY_PHONE = "Phone cannot be empty. Please try again!";
            fullNameText.setError(EMPTY_PHONE);
            return false;
        } else if (phone.length() != 9) {
            String INVALID_PHONE = "Invalid phone number. Please enter the last 9 digits" +
                    "of your phone number!";
            fullNameText.setError(INVALID_PHONE);
            return false;
        }

        return true;
    }

    // Validate vendor's dob
    private boolean isDobValid(String dob) {
        if (dob.isEmpty()) {
            String EMPTY_DOB = "Date of birth cannot be empty. Please try again!";
            dobText.setError(EMPTY_DOB);
            return false;
        }
        return true;
    }

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

    // Check if username is unique
    private boolean isUsernameUnique(String username){
        for (Vendor vendor: vendorList) {
            if (username.equals(vendor.getUsername())) return false;
        }
        return true;
    }

    // Check if username is unique
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
                                  String email,
                                  String password,
                                  String confirmPassword,
                                  String fullName,
                                  String phone,
                                  String dob) {
//        boolean isValid = true;

        return isUsernameValid(username)
                && isFullNameValid(fullName)
                && isDobValid(dob)
                && isPhoneValid(phone)
                && isEmailValid(email)
                && isPasswordValid(password, confirmPassword);


//        if (phone.length() < 4 && isValid) {
//            warningMsg = EMPTY_PHONE;
//            isValid = false;
//        }
//        // Phone number format
//        if (phone.length() != 12 && isValid) {
//            warningMsg = INVALID_PHONE;
//            isValid = false;
//        }


//        if (dob.isEmpty() && isValid) {
//            warningMsg = EMPTY_DOB;
//            isValid = false;
//        }
//        try {
//            int year = Integer.parseInt(dob.substring(6, 10));
//            if ((2021 - year < 18) && isValid) {
//                warningMsg = INVALID_DOB;
//                isValid = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        if (email.isEmpty() && isValid) {
//            warningMsg = EMPTY_EMAIL;
//            isValid = false;
//        }
//        // Email format
//        if (!isPatternValid(email, EMAIL_REGEX) && isValid) {
//            warningMsg = INVALID_EMAIL;
//            isValid = false;
//        }
//
//
//        if (password.isEmpty() && isValid) {
//            warningMsg = EMPTY_PASSWORD;
//            isValid = false;
//        }
//        if (password.length() < 6 && isValid) {
//            warningMsg = INVALID_PASSWORD;
//            isValid = false;
//        }
//        if (confirmPassword.isEmpty() && isValid) {
//            warningMsg = EMPTY_CONFIRM_PASSWORD;
//            isValid = false;
//        }
//        if (!confirmPassword.equals(password) && isValid) {
//            warningMsg = INVALID_CONFIRM_PASSWORD;
//            isValid = false;
//        }
//
//        if (!isValid) {
//            warningTextView.setText(warningMsg);
//            warningTextView.setVisibility(View.VISIBLE);
//            return false;
//        }
    }

    // init service
    public void initService(){
        // Init firestore
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        String CLIENT_COLLECTION = "vendors";
        userCollection = fireStore.collection(CLIENT_COLLECTION);
        // init realtime db
//        firebaseDatabase = FirebaseDatabase.getInstance("https://a2-android-56cbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();
        vendorList = new ArrayList<>();


    }

    // attach components
    public void getViews() {
        errorTxt = findViewById(R.id.errorTxt);
        errorTxt.setVisibility(View.INVISIBLE);

        emailText = findViewById(R.id.editEmail);
        usernameText = findViewById(R.id.editUserName);
        passwordText = findViewById(R.id.editPassword);
        confirmPasswordText = findViewById(R.id.editConfirmPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
    }
}

//TODO: add datepicker @Phuc